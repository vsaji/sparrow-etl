package sparrow.elt.core.cycledependency;

import sparrow.elt.core.config.ConfigParam;
import sparrow.elt.core.context.SparrowApplicationContext;
import sparrow.elt.core.log.SparrowLogger;
import sparrow.elt.core.log.SparrowrLoggerFactory;
import sparrow.elt.core.util.Sortable;

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */
public class StatsCollector
    implements CycleEventListener {

  private static final SparrowLogger logger = SparrowrLoggerFactory.
      getCurrentInstance(
      StatsCollector.class);

  private long startTime = 0;

  public StatsCollector(ConfigParam param,
                        SparrowApplicationContext context) {
  }

  /**
   * beginCycle
   */
  public void beginCycle() {
    startTime = System.currentTimeMillis();
  }

  /**
   * endCycle
   */
  public void endCycle() {
    long endTime = System.currentTimeMillis();
    logger.info("Total time taken for last cycle [" + (endTime - startTime) +
                " ms]");
  }

  /**
   * checkDependency
   *
   * @return boolean
   */
  public boolean checkDependency() {
    return false;
  }

  /**
   * getName
   *
   * @return String
   */
  public String getName() {
    return "StatsCollector";
  }

  /**
   * getStatusDescription
   *
   * @return String
   */
  public String getStatusDescription() {
    return "To trace the time taken between begin and end cycle";
  }

  /**
   * isProcessTerminationRequired
   *
   * @return boolean
   */
  public boolean isProcessTerminationRequired() {
    return false;
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
