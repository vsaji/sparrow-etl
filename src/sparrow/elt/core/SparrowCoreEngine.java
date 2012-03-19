package sparrow.elt.core;

import sparrow.elt.core.context.ContextVariables;
import sparrow.elt.core.context.SparrowApplicationContext;
import sparrow.elt.core.cycledependency.CycleDependencyChecker;
import sparrow.elt.core.exception.DataException;
import sparrow.elt.core.exception.ExceptionHandler;
import sparrow.elt.core.extractor.DataExtractor;
import sparrow.elt.core.extractor.DataExtractorFactory;
import sparrow.elt.core.initializer.ApplicationInitializer;
import sparrow.elt.core.log.SparrowLogger;
import sparrow.elt.core.log.SparrowrLoggerFactory;
import sparrow.elt.core.monitor.AppMonitor;
import sparrow.elt.core.monitor.AppObserver;
import sparrow.elt.core.monitor.CycleMonitor;
import sparrow.elt.core.monitor.CycleObserver;
import sparrow.elt.core.monitor.EOPMonitor;
import sparrow.elt.core.util.ConfigKeyConstants;
import sparrow.elt.core.util.Constants;
import sparrow.elt.core.util.Sortable;
import sparrow.elt.core.util.SparrowUtil;
import sparrow.elt.core.vo.DataHolder;

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */
public class SparrowCoreEngine
    implements Runnable, CycleObserver, AppObserver {

  private final CycleMonitor cycleMonitor;
  private final AppMonitor appMonitor;
  private final EOPMonitor eopMonitor;

  private int cycleCount = 0;
  private int recordCount =0;

  private SparrowApplicationContext context = null;
  private DataExtractor extractor = null;
  
  private boolean stopProcess = false;
  private boolean shutdownInitByCore, shutdownDueToExep=false;

  private boolean ignoreExtractorException = false;
  
  private static boolean shutdownInProgress = false;

  /**
   *
   */
  private static final SparrowLogger logger = SparrowrLoggerFactory.
      getCurrentInstance(
      SparrowCoreEngine.class);

  /**
   *
   */
  public SparrowCoreEngine() {
    this.cycleMonitor = new CycleMonitor();
    this.appMonitor = new AppMonitor();
    this.eopMonitor = new EOPMonitor();
    Runtime.getRuntime().addShutdownHook(new ShutDownInitializer());
  }

  /**
   *
   */
  void initialize() {
    initialize(null);
  }

  /**
   *
   */
  public void initialize(String configFile) {

    //----------------------------------------------------------------
    ApplicationInitializer appInitializer = ApplicationInitializer.getInstance();
    appInitializer.setCycleMonitor(cycleMonitor);
    appInitializer.setAppMonitor(appMonitor);
    appInitializer.setEOPMonitor(eopMonitor);

    if (configFile == null) {
      appInitializer.initialize();
    }
    else {
      appInitializer.initialize(configFile);
    }
      //----------------------------------------------------------------
      context = appInitializer.getContext();
      //----------------------------------------------------------------
      cycleCount = SparrowUtil.performTernary(context.getConfiguration().
                                            getModule(),
                                            ConfigKeyConstants.
                                            PARAM_SPEAR_CYCLE_COUNT, 0);
      //----------------------------------------------------------------
      cycleMonitor.addObserver(this);
      appMonitor.addObserver(this);
      //----------------------------------------------------------------
      SparrowLoadBalancer loadBalancer = SparrowLoadBalancer.getInstance();
      loadBalancer.setCycleMonitor(this.cycleMonitor);
      loadBalancer.setFIFOCollection(appInitializer.getTransformerFIFOs());
      loadBalancer.setContext(this.context);
      loadBalancer.initializeKeyAssigner();
      //----------------------------------------------------------------
      ignoreExtractorException = SparrowUtil.performTernary(context.getConfiguration().
              getModule(),ConfigKeyConstants.PARAM_SPEAR_IGNORE_EE, false);
      //----------------------------------------------------------------
      extractor = DataExtractorFactory.getLoader(this.context);
      //----------------------------------------------------------------
      appMonitor.notifyBeginApplication();

    }

    /**
     *
     * @return SparrowApplicationContext
     */
    public SparrowApplicationContext getContext() {
      return this.context;
    }

    /**
     *
     * @return boolean
     */
    public static final boolean isShutdownInProgress() {
      return shutdownInProgress;
    }

    /**
     *
     * @throws DataException
     */
    private void loadData() throws DataException {
      logger.info("[--- LOADING DATA FOR NEW CYCLE ---]");
      DataHolder dataHolder = extractor.loadData();
      logger.info("[--- DRIVER RESULT SIZE : " +
                  dataHolder.getData().getRowCount() + " ---]");
      updateRecordCount(dataHolder.getData().getRowCount());
      checkCycleCountAgainstShutdown(dataHolder.getData().getRowCount());
      SparrowLoadBalancer.getInstance().processData(dataHolder);
    }

    /**
     *
     */
    private void updateRecordCount(int currCount){
      if(currCount > 0){
    	recordCount = recordCount + currCount;
        context.setAttribute(ContextVariables.FETCH_COUNT, new Integer(recordCount));
      }
    }


    /**
     *
     * @param recordCount int
     */
    private void checkCycleCountAgainstShutdown(int recCount) {
      if (recCount == 0) {
        eopMonitor.notifyEOP(Constants.EP_NO_RECORD);
        if (cycleCount == -1) {
          logger.info("[SPEAR SHUTTING DOWN]");
          shutdownInitByCore = true;
          killApp(Constants.NORMAL_EXIT);
        }
      }
    }

    /**
     *
     */
    public void run() {

      long configInterval = SparrowUtil.performTernaryForLong(this.context.
          getConfiguration().getModule(),
          ConfigKeyConstants.PARAM_SPEAR_CYCLE_INTERVAL, 5000);

      long interval = configInterval;

      extractor.initialize();

      while (true && !stopProcess) {
        try {
         // System.out.println("***********>"+interval);
          CycleDependencyChecker.Status status = CycleDependencyChecker.
              checkDependencyChain();
          //cycleMonitor.notifyBeginCycle();
          //System.out.println("=========>"+!status.getOverAllStatus());
          if (!status.getOverAllStatus()) {
            loadData();
            interval = configInterval;
           // System.out.println("=========>"+interval);
          }
          else {
            //interval = 1000;
            interval = (configInterval / 2);
            if (status.getProcessTerminationStatus()) {
              stopProcess = true;
              interval = 0;
            }
          }

          if (configInterval > 0) {
            Thread.sleep(interval);
          }
        }
        catch (Exception ex) {
          logger.error("Exception while loding data in the run method",
                       ex);
          ex.printStackTrace();
          
          if(!ignoreExtractorException){
        	  shutdownInitByCore = true;
        	  shutdownDueToExep = true;
        	  killApp(Constants.ERROR_EXIT);
        	  return;
          }
        }
      }

      if (stopProcess) {
        shutdownInitByCore = true;
        killApp(Constants.NORMAL_EXIT);
      }
    }

    /**
     * beginCycle
     */
    public void beginCycle() {
      //logger.info("[--- BEGIN CYCLE ---]");
    }

    /**
     * endCycle
     */
    public void endCycle() {
      //logger.info("[--- END CYCLE ---]");
    }

    /**
     *
     */
    public void endApplication() {
      logger.info("[--- END APPLICATION ---]");
    }

    /**
     *
     * @return int
     */
    public int getPriority() {
      return Sortable.PRIORITY_ABOVE_LOW;
    }

    /**
     *
     */
    private void finalizeApplication() {
      finalizeExtractor();
      //Commented on 28-Dec-2010
//      eopMonitor.notifyEOP( (shutdownDueToExep) ? Constants.EP_CORE_ERROR :
//                           Constants.EP_END_APP);
      appMonitor.notifyEndApplication();
      appMonitor.deleteObservers();
      cycleMonitor.deleteObservers();
    }

    /**
     *
     */
    private void finalizeExtractor() {
      try {
        extractor.destroy();
      }
      catch (DataException ex1) {
        logger.error(
            "DataException occure while invoking destroy method in DataExtractor",
            ex1);
      }
    }

    /**
     *
     */
    private void killApp(int exitCode) {
      eopMonitor.notifyEOP( (shutdownDueToExep) ? Constants.EP_CORE_ERROR :
                           Constants.EP_END_APP);
      ExceptionHandler.setShutdownInProgress(true);

      Boolean b = (Boolean) context.getAttribute(ContextVariables.FAIL_PROCESS);

      if(Constants.NORMAL_EXIT==exitCode && Boolean.TRUE.equals(b)){
        logger.warn("Context variable [FAIL_PROCESS] is set to TRUE. Hence the process is exiting with NON-ZERO value.");
         exitCode = Constants.ERROR_EXIT;
      }

      System.exit(exitCode);
    }

    /**
     * beginApplication
     */
    public void beginApplication() {
      logger.info("[--- BEGIN APPLICATION ---]");
    }

    /**
     *
     * <p>Title: </p>
     * <p>Description: </p>
     * <p>Copyright: Copyright (c) 2004</p>
     * <p>Company: </p>
     * @author Saji Venugopalan
     * @version 1.0
     */
    private class ShutDownInitializer
        extends Thread {
      public void run() {
        if (shutdownInitByCore) {
          shutdownInProgress = true;
          finalizeApplication();
        }
      }
    }

  }
