/* Generated by Together */

package sparrow.etl.core.monitor;

import sparrow.etl.core.exception.EventNotifierException;
import sparrow.etl.core.util.Sortable;

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public interface CycleObserver extends Sortable{

  abstract void beginCycle() throws EventNotifierException;

  abstract void endCycle() throws EventNotifierException;

}