package sparrow.elt.core.transformer;

import sparrow.elt.core.DataSet;
import sparrow.elt.core.config.SparrowDataTransformerConfig;
import sparrow.elt.core.context.ContextVariables;
import sparrow.elt.core.context.SparrowContext;
import sparrow.elt.core.dao.impl.QueryObject;
import sparrow.elt.core.dao.impl.RecordSet;
import sparrow.elt.core.dao.impl.ResultRow;
import sparrow.elt.core.exception.DataException;
import sparrow.elt.core.exception.EnrichDataException;
import sparrow.elt.core.exception.RejectionException;
import sparrow.elt.core.log.SparrowLogger;
import sparrow.elt.core.log.SparrowrLoggerFactory;
import sparrow.elt.core.report.FileReporter;
import sparrow.elt.core.report.RejectedEntry;
import sparrow.elt.core.util.AsyncRequestProcessor;
import sparrow.elt.core.util.ConfigKeyConstants;
import sparrow.elt.core.util.Constants;
import sparrow.elt.core.util.GenericTokenResolver;
import sparrow.elt.core.util.IObjectPoolLifeCycle;
import sparrow.elt.core.util.SparrowUtil;
import sparrow.elt.core.vo.DataOutputHolder;

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */
public abstract class AbstractDataTransformer
    implements DataTransformer, DriverRowEventListener {

  private static boolean rejectionReportInit = false;
  protected static AsyncRequestProcessor arp = null;
  private static int rejectCount = 0;

  protected final SparrowContext context;
  protected final String name;
  protected final SparrowDataTransformerConfig config;

  protected DriverRowEventListener lkuplistener = null;
  protected ResultRow driverRow;

  private IObjectPoolLifeCycle olc = null;

  private boolean markForRejection = false;
  private boolean softRejection = false;



  protected static final SparrowLogger logger = SparrowrLoggerFactory.
      getCurrentInstance(
      AbstractDataTransformer.class);

  /**
   *
   * @param context SparrowContext
   * @param name String
   */
  protected AbstractDataTransformer(SparrowContext context, String name) {
    this.config = null;
    this.context = context;
    this.name = name;
  }

  /**
   *
   * @param context SparrowContext
   */
  protected AbstractDataTransformer(SparrowDataTransformerConfig config) {
    this.config = config;
    this.context = config.getContext();
    this.name = config.getName();
    String reportType = config.getInitParameter().getParameterValue(
        ConfigKeyConstants.PARAM_REJECTION_REPORT_TYPE);

    if (!rejectionReportInit) {
      if (reportType != null) {
        String outputPattern = (config.getInitParameter().isParameterExist(
            ConfigKeyConstants.PARAM_REJECTION_REPORT_PATTERN)) ?
            config.getInitParameter().
            getParameterValue(ConfigKeyConstants.PARAM_REJECTION_REPORT_PATTERN) :
            "[" + Constants.TOKEN_START + RejectedEntry.REPORT_SOURCE_NAME +
            Constants.TOKEN_END + "][" + Constants.TOKEN_START +
            RejectedEntry.REJECT_DATE +
            Constants.TOKEN_END + "][" + Constants.TOKEN_START +
            RejectedEntry.PRIMARY_VALUE +
            Constants.TOKEN_END + "][" + Constants.TOKEN_START +
            RejectedEntry.REJECT_REASON + Constants.TOKEN_END + "][" +
            Constants.TOKEN_START + RejectedEntry.REJECTED_ENTRY
            + Constants.TOKEN_END + "]\n";

        if (Constants.TYPE_FILE.equals(config.getInitParameter().
                                       getParameterValue(ConfigKeyConstants.
            PARAM_REJECTION_REPORT_TYPE))) {
        	
        	String fileName = config.getInitParameter().getParameterValue(ConfigKeyConstants.PARAM_REJECTION_REPORT_FILE) ;
        	String filePath = config.getInitParameter().getParameterValue(ConfigKeyConstants.PARAM_REJECTION_REPORT_SRC);
        	fileName = (fileName!=null) ? 
        			SparrowUtil.evaluateAndReplace(filePath+"/"+fileName,GenericTokenResolver.getInstance()) : 
        			SparrowUtil.constructOutputFileName(filePath, name, ".error");
			
        	arp = AsyncRequestProcessor.createAsynchProcessor(Constants.REJECTION_SERVICE);
			arp.registerListener(name, new FileReporter(fileName,outputPattern));
			context.setAttribute(ContextVariables.REJECT_REC_FILE,fileName);
			arp.start();
        }
      }
      rejectionReportInit = true;
    }

    // Default and empty implementation
    this.lkuplistener = this;

  }

  /**
   *
   * @param olc IObjectPoolLifeCycle
   */
  public final void setOLC(IObjectPoolLifeCycle olc) {
    this.olc = olc;
  }

  /**
   *
   * @param dataSet DataSet
   * @throws EnrichDataException
   * @return DataOutputHolder
   */
  public abstract DataOutputHolder enrichData(DataSet dataSet) throws
      EnrichDataException;

  /**
   *
   */
  public final void returnObject() {
    markForRejection = false;
    softRejection = false ;
    driverRow = null;
    if(olc!=null){
      olc.returned(this);
    }
  }

  /**
   * finalizeObject
   */
  public final void finalizeObject() {
    olc = null;
  }

  /**
   *
   * @return DriverRowEventListener
   */
  public final DriverRowEventListener getDriverRowEventListener() {
    return this.lkuplistener;
  }

  /**
   * setLookupListner
   *
   * @param listener LookupListener
   */
  public final void setDriverRowEventListener(DriverRowEventListener listener) {
    this.lkuplistener = listener;
  }

  /**
   *
   * @param reason String
   * @param keyColumn String
   * @throws RejectionException
   */
  public final void markForRejection(String reason) throws
      RejectionException {
    if (arp != null) {

      if (reason == null || reason.trim().equals("")) {
        throw new RejectionException("Reason cannot be null or empty string");
      }

      RejectedEntry re = new RejectedEntry();
      re.setPrimaryValue(SparrowUtil.printDriverValue(driverRow));
      re.setRejectedEntry(driverRow.getChunk().toString());
      re.setReporterSource(name);
      re.setRejectReason(reason);
      arp.process(name, re);
    }
    markForRejection = true;
    context.setAttribute(ContextVariables.REJECT_COUNT,new Integer(rejectCount++));
  }

  /**
   *
   * @param reason String
   * @throws RejectionException
   */
  public final void markForRejection(String reason, Throwable t) throws
      RejectionException {
      markForRejection(reason+"-{"+t.getClass().getName()+"="+t.getMessage()+"}");
  }

  /**
   *
   * @param reason String
   * @param hardRejection boolean
   * @throws RejectionException
   */
  public void markForRejection(String reason, boolean hardRejection) throws
      RejectionException{
    softRejection = (hardRejection==false);
    markForRejection(reason);
  }
  /**
   *
   * @param row ResultRow
   */
  public final void setDriverRow(ResultRow row) {
    driverRow = row;
  }


  /**
   *
   * @return boolean
   */
  public boolean isSoftRejection() {
    return softRejection;
  }


  /**
   *
   * @return boolean
   */
  public final boolean isMarkedForRejection() {
    return markForRejection;
  }



  /**
   * initialize
   */
  public void initialize() {
    lkuplistener.clear();
  }

  /**
   * destory
   */
  public void destroy() {
  }

  /**
   * clear
   */
  public void clear() {
  }

  /**
   * postFinalize
   *
   * @param success boolean
   */
  public void postFinalize(boolean success) {
  }

  /**
   * postLookUp
   *
   * @param lookupName String
   * @param rs RecordSet
   */
  public void postLookUp(String lookupName, RecordSet rs) {
  }

  /**
   * postWrite
   *
   * @param writerName String
   * @param success boolean
   */
  public void postWrite(String writerName, boolean success) {
  }

  /**
   * preFilter
   *
   * @param lookupName String
   * @param filter String
   * @return String
   */
  public String preFilter(String lookupName, String filter) {
    return filter;
  }

  /**
   * preFinalize
   *
   * @return boolean
   */
  public boolean preFinalize() {
    return true;
  }

  /**
   * preLookUp
   *
   * @param lookupName String
   * @param query QueryObject
   * @return boolean
   */
  public boolean preLookUp(String lookupName, QueryObject query) {
    return true;
  }

  /**
   * preQueue
   *
   * @return boolean
   */
  public boolean preQueue() {
    return true;
  }

  /**
   * preWrite
   *
   * @param writerName String
   * @return boolean
   */
  public boolean preWrite(String writerName) {
    return true;
  }

  /**
   * This method will be called only once by the kernal.
   */
  public void staticInitialize(){

  }

  /**
   *
   * @param lookupName String
   * @param rs RecordSet
   * @return ResultRow
   */
  public ResultRow getSingleLookupResult(String lookupName, RecordSet rs) {
    try {
      return rs.getFirstRow();
    }
    catch (DataException ex) {
      logger.error("getSingleLookupResult[" + lookupName + "]", ex);
      return null;
    }
  }

}
