package sparrow.elt.core.config;

import java.util.ArrayList;
import java.util.List;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

import sparrow.elt.core.dao.impl.ResultRow;

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public class DBVariableObservable {

  private Map holder = new HashMap() ;

  private static final DBVariableObservable instance = new DBVariableObservable();

  private DBVariableObservable() {
  }

  public static final DBVariableObservable getInstance(){
    return instance;
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
  public synchronized void addObserver(String dpName, DBVariableObserver o) {
    if (o == null) {
      throw new NullPointerException();
    }
    if (!holder.containsKey(dpName)) {
      holder.put(dpName, new ArrayList());
    }

    List l = ( (ArrayList) holder.get(dpName));

    if(!l.contains(o)){
      l.add(o);
    }
  }

  /**
   * Deletes an observer from the set of observers of this object.
   *
   * @param   o   the observer to be deleted.
   */
  public synchronized void deleteObserver(DBVariableObserver o) {
    //obs.remove(o);
  }

  public void notifyObserver(String dpName, ResultRow rr) {
    /*
     * a temporary array buffer, used as a snapshot of the state of
     * current Observers.
     */
    if (holder.containsKey(dpName)) {

      List obs = (ArrayList) holder.get(dpName);

      for (Iterator it = obs.iterator(); it.hasNext(); ) {
        try {
          ( (DBVariableObserver) it.next()).populateVariable(dpName, rr);
        }
        catch (Exception ex) {
          ex.printStackTrace();
        }
      }
    }
  }

}
