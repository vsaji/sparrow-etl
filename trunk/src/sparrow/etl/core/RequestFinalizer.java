package sparrow.etl.core;

import sparrow.etl.core.exception.RequestProcessException;
import sparrow.etl.core.monitor.CycleObserver;
import sparrow.etl.core.monitor.EOPObserver;
import sparrow.etl.core.vo.SparrowResultHolder;
import sparrow.etl.core.writer.DataWriter;

public interface RequestFinalizer
    extends EOPObserver, CycleObserver {

  abstract void processRequest(SparrowResultHolder resultHolder) throws
      RequestProcessException;

  abstract void setWriters(DataWriter[] writer);
}
