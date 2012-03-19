package sparrow.elt.core.monitor;

import sparrow.elt.core.exception.EventNotifierException;

public interface EOPObserver {

  abstract void endOfProcess(int flag) throws EventNotifierException;

}
