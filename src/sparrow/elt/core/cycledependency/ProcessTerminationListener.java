package sparrow.elt.core.cycledependency;

import sparrow.elt.core.config.ConfigParam;
import sparrow.elt.core.context.SparrowApplicationContext;
import sparrow.elt.core.exception.DependancyCheckException;
import sparrow.elt.core.exception.ServiceUnavailableException;
import sparrow.elt.core.exception.SparrowRuntimeException;
import sparrow.elt.core.util.ConfigKeyConstants;
import sparrow.elt.core.util.Semaphore;
import sparrow.elt.core.util.Sortable;
import sparrow.elt.core.util.SparrowUtil;

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */
public class ProcessTerminationListener
    implements CycleEventListener {

  private static boolean processTerminationRequired = false;
  private boolean semaphoreCheckReqd = false;
  private Semaphore semaphore = null;

  /**
   *
   * @param context SparrowApplicationContext
   */
  public ProcessTerminationListener(ConfigParam param,
                                          SparrowApplicationContext context) {

    semaphoreCheckReqd = SparrowUtil.performTernary(context.getConfiguration().
                                                  getModule(),
                                                  ConfigKeyConstants.
                                                  PARAM_SEMAPHORE_CHECK, false);
    if (semaphoreCheckReqd) {
      try {
        semaphore = (Semaphore) context.getService("semaphore");
      }
      catch (ServiceUnavailableException ex) {
        throw new SparrowRuntimeException(ex);
      }
    }
  }

  /**
   * beginCycle
   */
  public void beginCycle() {
  }

  /**
   * endCycle
   */
  public void endCycle() {
  }

  /**
   * checkDependency
   *
   * @return boolean
   */
  public boolean checkDependency() throws DependancyCheckException {
    if (semaphoreCheckReqd) {
      try {
        processTerminationRequired = semaphore.isProcessTerminationFlagOn();
        if (processTerminationRequired) {
          throw new DependancyCheckException(
              "[SEMAPHORE] Process termination signal received");
        }
      }
      catch (Exception ex) {
        throw new DependancyCheckException(ex);
      }
    }
    return processTerminationRequired;
  }

  /**
   * getName
   *
   * @return String
   */
  public String getName() {
    return "ProcessTerminationCheckDependant";
  }

  /**
   * isProcessTerminationRequired
   *
   * @return boolean
   */
  public boolean isProcessTerminationRequired() {
    return processTerminationRequired;
  }

  /**
   * getStatusDescription
   *
   * @return String
   */
  public String getStatusDescription() {
    return "Termination falg [" + processTerminationRequired + "]";
  }


  /**
   * getPriority
   *
   * @return int
   */
  public int getPriority() {
    return Sortable.PRIORITY_ABOVE_LOW;
  }

}
