package sparrow.etl.core.monitor;

import sparrow.etl.core.exception.EventNotifierException;

public interface EOPObserver {

  abstract void endOfProcess(int flag) throws EventNotifierException;

}
