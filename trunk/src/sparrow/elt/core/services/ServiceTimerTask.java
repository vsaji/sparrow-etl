package sparrow.elt.core.services;

import java.util.TimerTask;

import sparrow.elt.core.config.ServiceConfig;
import sparrow.elt.core.exception.SchedulerException;
import sparrow.elt.core.log.SparrowLogger;
import sparrow.elt.core.log.SparrowrLoggerFactory;
import sparrow.elt.core.util.ConfigKeyConstants;
import sparrow.elt.core.util.SparrowUtil;


/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public class ServiceTimerTask
    extends TimerTask {

  private static final SparrowLogger logger = SparrowrLoggerFactory.
      getCurrentInstance(
      ServiceTimerTask.class);

  private final SchedulerService ss;
  private final ServiceConfig serviceConfig;
  private final long interval;

  /**
   *
   * @param ss SchedulerService
   * @param serviceConfig ServiceConfig
   */
  public ServiceTimerTask(SchedulerService ss, ServiceConfig serviceConfig) {
    this.ss = ss;
    this.serviceConfig = serviceConfig;
    this.interval = SparrowUtil.performTernaryForLong(serviceConfig,
        ConfigKeyConstants.PARAM_SERVICE_INTERVAL, 30000);
  }

  /**
   *
   * @return long
   */
  public long getRefreshInterval() {
    return interval;
  }

  /**
   * run
   */
  public void run() {
    try {
      ss.executeTask();
    }
    catch (SchedulerException ex) {
      logger.error("Error occure while executing Scheduler Service[" +
                   serviceConfig.getName() + "]->[" + ex.getMessage() + "]", ex);
    }
  }
}
