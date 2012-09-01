package sparrow.etl.core.cycledependency;

import sparrow.etl.core.config.ConfigParam;
import sparrow.etl.core.context.SparrowApplicationContext;
import sparrow.etl.core.log.SparrowLogger;
import sparrow.etl.core.log.SparrowrLoggerFactory;
import sparrow.etl.core.util.Constants;
import sparrow.etl.core.util.Sortable;
import sparrow.etl.core.util.SparrowUtil;

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
        getConfiguration().getModule(), Constants.SPARROW_CYCLE_COUNT, 0);
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
                  cycleCount + "][sparrow.cycle.count=" + CYCLE_MAX_RUN_COUNT +
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
        "[" + Constants.SPARROW_CYCLE_COUNT + "] is NOT set. Infinite RUN" :
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
