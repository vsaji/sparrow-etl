package sparrow.elt.core.cycledependency;

import sparrow.elt.core.config.ConfigParam;
import sparrow.elt.core.context.SparrowApplicationContext;
import sparrow.elt.core.log.SparrowLogger;
import sparrow.elt.core.log.SparrowrLoggerFactory;
import sparrow.elt.core.util.Constants;
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
public class CycleCountListener
    implements CycleEventListener {

  private static final SparrowLogger logger = SparrowrLoggerFactory.
      getCurrentInstance(
      CycleCountListener.class);

  private final int CYCLE_MAX_RUN_COUNT;
  private int cycleCount = 0;
  private boolean processTerminate = false;

  /**
   *
   * @param context SparrowApplicationContext
   */
  public CycleCountListener(ConfigParam param,
                            SparrowApplicationContext context) {
    this.CYCLE_MAX_RUN_COUNT = SparrowUtil.performTernary(context.
        getConfiguration().getModule(), Constants.SPEAR_CYCLE_COUNT, 0);
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
    if (CYCLE_MAX_RUN_COUNT != 0) {
      cycleCount++;
      if (cycleCount == CYCLE_MAX_RUN_COUNT) {
        processTerminate = true;
      }
    }
  }

  /**
   * checkDependency
   *
   * @return boolean
   */
  public boolean checkDependency() {
    return processTerminate;
  }

  /**
   * getName
   *
   * @return String
   */
  public String getName() {
    return "EndCycleCheckDependant";
  }

  /**
   * isProcessTerminationRequired
   *
   * @return boolean
   */
  public boolean isProcessTerminationRequired() {
    if (processTerminate) {
      logger.warn("PROCESS SHUTDOWN initiated. Total no. of cycle completed [" +
                  cycleCount + "][spear.cycle.count=" + CYCLE_MAX_RUN_COUNT +
                  "]");
    }

    return processTerminate;
  }

  /**
   * getStatusDescription
   *
   * @return String
   */
  public String getStatusDescription() {
    return (CYCLE_MAX_RUN_COUNT == 0) ?
        "[" + Constants.SPEAR_CYCLE_COUNT + "] is NOT set. Infinite RUN" :
        "Cycle Count [" + cycleCount + "]";
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
