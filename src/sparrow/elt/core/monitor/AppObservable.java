package sparrow.elt.core.monitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import sparrow.elt.core.log.SparrowLogger;
import sparrow.elt.core.log.SparrowrLoggerFactory;
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
public class AppObservable {

  private List obs;

  /** Construct an Observable with zero Observers. */

  private static final SparrowLogger logger = SparrowrLoggerFactory.
      getCurrentInstance(
      AppObservable.class);

  public AppObservable() {
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
  public synchronized void addObserver(AppObserver o) {
    if (o == null) {
      throw new NullPointerException();
    }
    if (!obs.contains(o)) {
      obs.add(o);
      Collections.sort(obs, SparrowUtil.OBJECT_PRIORITY_SORTER);
    }
  }

  /**
   * Deletes an observer from the set of observers of this object.
   *
   * @param   o   the observer to be deleted.
   */
  public synchronized void deleteObserver(AppObserver o) {
    obs.remove(o);
    Collections.sort(obs, SparrowUtil.OBJECT_PRIORITY_SORTER);
  }

  public void notifyBeginApplication() {
    /*
     * a temporary array buffer, used as a snapshot of the state of
     * current Observers.
     */
    Object[] arrLocal;

    synchronized (this) {
      arrLocal = obs.toArray();
    }

    for (int i = 0; i < arrLocal.length; i++) {
      try {
        ( (AppObserver) arrLocal[i]).beginApplication();
      }
      catch (Exception ex) {
        logger.error(ex.getMessage());
      }
    }
  }

  public void notifyEndApplication() {
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
        ( (AppObserver) arrLocal[i]).endApplication();
      }
      catch (Exception ex) {
        logger.error(ex.getMessage());
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
