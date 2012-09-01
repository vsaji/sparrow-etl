package sparrow.etl.core.monitor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import sparrow.etl.core.exception.InitializationException;
import sparrow.etl.core.log.SparrowLogger;
import sparrow.etl.core.log.SparrowrLoggerFactory;
import sparrow.etl.core.util.ContextParam;
import sparrow.etl.core.util.Sortable;


/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */
public class Watcher
    extends TimerTask
    implements AppObserver {

  private static final SparrowLogger logger = SparrowrLoggerFactory.
      getCurrentInstance(
      Watcher.class);
  private static final HashMap REPORTER_REGISTRY = new HashMap();
  private static boolean initialized = false;
  private Timer timer = null;
  private long interval = 0;
  private static boolean watcherEnable = false;

  /**
   *
   */
  public Watcher() {
    if (!initialized) {

      watcherEnable = true;

      String wIntvl = ContextParam.getContextParamValue(
          "sparrow.watcher.interval");

      interval = (wIntvl == null || Integer.parseInt(wIntvl) < 1000) ?
          1000 : Integer.parseInt(wIntvl);
      initialized = true;
    }
    else {
      throw new InitializationException("Watcher is already initialized");
    }
  }

  /**
   *
   * @param name String
   * @param reporter Reporter
   */
  public static final void registerReporters(String name, Reporter reporter) {
    if (watcherEnable) {
      REPORTER_REGISTRY.put(name, reporter);
    }
  }

  /**
   * run
   */
  public void run() {
    Set keys = REPORTER_REGISTRY.keySet();
    for (Iterator it = keys.iterator(); it.hasNext(); ) {
      String key = it.next().toString();
      Reporter r = (Reporter) REPORTER_REGISTRY.get(key);
      try {
        r.report();
      }
      catch (Exception ex) {
        logger.warn("Exception occured while invoking Reporter[" + key + "]",
                    ex);
      }
    }
  }

  /**
   * beginApplication
   */
  public void beginApplication() {
    timer = new Timer(true);
    timer.schedule(this, 0, interval);
  }

  /**
   * endApplication
   */
  public void endApplication() {
    Set keys = REPORTER_REGISTRY.keySet();
    for (Iterator it = keys.iterator(); it.hasNext(); ) {
      String key = it.next().toString();
      Reporter r = (Reporter) REPORTER_REGISTRY.get(key);
      try {
        r.destory();
      }
      catch (Exception ex) {
        logger.warn("Exception occured while destroying Reporter[" + key + "]",
                    ex);
      }
    }
    timer.cancel();
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
