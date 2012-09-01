package sparrow.etl.core.monitor;

import java.util.ArrayList;
import java.util.List;

import sparrow.etl.core.log.SparrowLogger;
import sparrow.etl.core.log.SparrowrLoggerFactory;


/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */
public class EOPMonitor {

  private List obs;

  /** Construct an Observable with zero Observers. */

  private static final SparrowLogger logger = SparrowrLoggerFactory.
      getCurrentInstance(
      EOPMonitor.class);

  public EOPMonitor() {
    obs = new ArrayList();
  }

  /**
   * Adds an observer to the set of observers for this object, provided
   * that it is not the same as some observer already in the set.
   * The order in which notifications will be delivered to multiple
   * observers is not specified. See the class comment.
   *
   * @param   o   an observer to be added.
   * @throws NullPointerException   if the parameter o is null.
   */
  public synchronized void addObserver(EOPObserver o) {
    if (o == null) {
      throw new NullPointerException();
    }
    if (!obs.contains(o)) {
      obs.add(o);
    }
  }

  /**
   * Deletes an observer from the set of observers of this object.
   *
   * @param   o   the observer to be deleted.
   */
  public synchronized void deleteObserver(EOPObserver o) {
    obs.remove(o);
  }

  public void notifyEOP(int flag) {
    /*
     * a temporary array buffer, used as a snapshot of the state of
     * current Observers.
     */
    Object[] arrLocal;

    synchronized (this) {
      arrLocal = obs.toArray();
    }

    for (int i = arrLocal.length - 1; i >= 0; i--) {
      try {
        ( (EOPObserver) arrLocal[i]).endOfProcess(flag);
      }
      catch (Exception ex) {
        logger.error(ex.getMessage());
        ex.printStackTrace();
      }
    }
  }

  /**
   * Clears the observer list so that this object no longer has any observers.
   */
  public synchronized void deleteObservers() {
    obs.clear();
  }

}
