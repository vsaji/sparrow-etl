/* Generated by Together */

package sparrow.etl.core.initializer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;

import sparrow.etl.core.config.ConfigParam;
import sparrow.etl.core.config.ServiceConfig;
import sparrow.etl.core.config.ServicesConfig;
import sparrow.etl.core.config.SparrowServiceConfig;
import sparrow.etl.core.context.SparrowContext;
import sparrow.etl.core.exception.EventNotifierException;
import sparrow.etl.core.exception.InitializationException;
import sparrow.etl.core.log.SparrowLogger;
import sparrow.etl.core.log.SparrowrLoggerFactory;
import sparrow.etl.core.monitor.AppObserver;
import sparrow.etl.core.monitor.CycleObserver;
import sparrow.etl.core.services.PluggableService;
import sparrow.etl.core.services.SchedulerService;
import sparrow.etl.core.services.ServiceTimerTask;
import sparrow.etl.core.util.DependentSequenzer;
import sparrow.etl.core.util.SparrowUtil;


/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public class ServiceInitializer
    implements AppObserver, CycleObserver {

  private SparrowApplicationContextImpl context = null;
  private List initializedServices = null;
  private Map serviceMap = null;

  private List appNotifServices = null;
  private List cycleNotifServices = null;
  private List schedulerServices = null;

  /**
   * Logger
   */
  private static final SparrowLogger logger = SparrowrLoggerFactory.
      getCurrentInstance(
      ServiceInitializer.class);

  /**
   *
   * @param context SparrowApplicationContextImpl
   */
  ServiceInitializer(SparrowApplicationContextImpl context) {
    this.context = context;
    this.initializedServices = new ArrayList();
    this.appNotifServices = new ArrayList();
    this.cycleNotifServices = new ArrayList();
    this.schedulerServices = new ArrayList();
  }

  /**
   *
   * @throws InitializationException
   */
  void initialize() {
    ServicesConfig servicesConfig = this.context.getConfiguration().
        getServices();
    List services = servicesConfig.getServices();
    List serviceNames = DependentSequenzer.sequenceDependent(services);
    this.serviceMap = DependentSequenzer.getItemsInMap(services);

    try {
      if (!services.isEmpty()) {
        for (Iterator iter = serviceNames.iterator(); iter.hasNext(); ) {
          String serviceName = (String) iter.next();
          ServiceConfig item = (ServiceConfig) serviceMap.get(serviceName);
          this.initializeService(item);
        }
      }

    }
    catch (InitializationException e) {
      throw e;
    }
    catch (Exception e) {
      throw new InitializationException(e);
    }
  }

  /**
   *
   * @param item DatasourceConfig
   * @throws ClassNotFoundException
   * @throws IllegalAccessException
   * @throws InstantiationException
   * @return IDataSourceInitializer
   */
  private PluggableService getServiceObject(ServiceConfig item,SparrowServiceConfig config) {

    if (logger.isDebugEnabled()) {
      logger.log("Initializing Service : " + item.getName(), logger.DEBUG);
    }
    PluggableService serviceIniti = (PluggableService)
        SparrowUtil.createObject(item.getType().getServiceClass(),SparrowServiceConfig.class,config);
    return serviceIniti;
  }

  /**
   *
   * @param service ServiceConfig
   * @throws InitializationException
   */
  private void initializeService(final ServiceConfig service) {
    try {
      if (!this.initializedServices.contains(service.getName())) {
        PluggableService servce = this.getServiceObject(service,new SparrowServiceConfig() {
          /**
           * getInitParameter
           *
           * @return ConfigParam
           */
          public ConfigParam getInitParameter() {
            return service;
          }

          /**
           * getContext
           *
           * @return SparrowContext
           */
          public SparrowContext getContext() {
            return context;
          }

          /**
           * getName
           *
           * @return String
           */
          public String getName() {
            return service.getName();
          }
        });

        if (service.isAppNotificatinonRequired()) {
          this.appNotifServices.add(servce);
        }

        if (service.isCycleNotificatinonRequired()) {
          this.cycleNotifServices.add(servce);
        }

        this.initializedServices.add(service.getName());
        this.context.addService(service.getName(), servce);

        if (servce instanceof SchedulerService) {
          this.schedulerServices.add(new ServiceTimerTask( (SchedulerService)
              servce, service));
        }
      }
    }
    catch (Exception e) {
      throw new InitializationException(e);
    }

  }

  /**
   * beginApplication
   */
  public void beginApplication() throws EventNotifierException {
    for (Iterator it = appNotifServices.iterator(); it.hasNext(); ) {
      PluggableService servce = (PluggableService) it.next();
      servce.beginApplication();
    }

    for (Iterator it = schedulerServices.iterator(); it.hasNext(); ) {
      ServiceTimerTask stt = (ServiceTimerTask) it.next();
      Timer timer = new Timer(true);
      timer.schedule(stt, 0, stt.getRefreshInterval());
    }

  }

  /**
   * endApplication
   */
  public void endApplication() throws EventNotifierException {

    for (Iterator it = appNotifServices.iterator(); it.hasNext(); ) {
      PluggableService servce = (PluggableService) it.next();
      servce.endApplication();
    }

    for (Iterator it = schedulerServices.iterator(); it.hasNext(); ) {
      ServiceTimerTask stt = (ServiceTimerTask) it.next();
      stt.cancel();
    }

  }

  /**
   * beginCycle
   */
  public void beginCycle() throws EventNotifierException {
    for (Iterator it = cycleNotifServices.iterator(); it.hasNext(); ) {
      PluggableService servce = (PluggableService) it.next();
      servce.beginCycle();
    }

  }

  /**
   * endCycle
   */
  public void endCycle() throws EventNotifierException {
    for (Iterator it = cycleNotifServices.iterator(); it.hasNext(); ) {
      PluggableService servce = (PluggableService) it.next();
      servce.endCycle();
    }
  }

  /**
   * getPriority
   *
   * @return int
   */
  public int getPriority() {
    return PRIORITY_ABOVE_MEDIUM;
  }

}