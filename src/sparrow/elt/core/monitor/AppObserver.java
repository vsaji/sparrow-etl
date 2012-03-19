package sparrow.elt.core.monitor;

import sparrow.elt.core.exception.EventNotifierException;
import sparrow.elt.core.util.Sortable;


/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public interface AppObserver extends Sortable{

  abstract void beginApplication() throws EventNotifierException;

  abstract void endApplication() throws EventNotifierException;

}
