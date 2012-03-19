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
public class SystemGCForcer
    implements CycleEventListener {

  private long startMem, startMemMB, startMemKB = 0;
  private long endMem = 0;

  private static final SparrowLogger logger = SparrowrLoggerFactory.
      getCurrentInstance(
      SystemGCForcer.class);

  /**
   *
   * @param param ConfigParam
   * @param context SparrowApplicationContext
   */
  public SystemGCForcer(ConfigParam param,
                        SparrowApplicationContext context) {
  }

  /**
   * beginCycle
   */
  public void beginCycle() {
    startMem = Runtime.getRuntime().freeMemory();
    startMemMB = (startMem / 1024) / 1024;
    startMemKB = (startMem / 1024);

  }

  /**
   * endCycle
   */
  public void endCycle() {
    Runtime.getRuntime().runFinalization();
    Runtime.getRuntime().gc();
    endMem = Runtime.getRuntime().freeMemory();
    long endMemMB = (endMem / 1024) / 1024;
    long endMemKB = (endMem / 1024);
    String show = ( (endMemMB - startMemMB) <= 0) ?
        "[" + (endMemKB - startMemKB) + " KB]" :
        "[" + (endMemMB - startMemMB) + " MB]";

    logger.info("Memory Usage for the last cycle " + show);
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
    return "SystemGCForcer";
  }

  /**
   * getStatusDescription
   *
   * @return String
   */
  public String getStatusDescription() {
    return "Force System.gc in every Cycle";
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
