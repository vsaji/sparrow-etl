package sparrow.elt.core;

import sparrow.elt.core.exception.RequestProcessException;
import sparrow.elt.core.monitor.CycleObserver;
import sparrow.elt.core.monitor.EOPObserver;
import sparrow.elt.core.vo.SparrowResultHolder;
import sparrow.elt.core.writer.DataWriter;

public interface RequestFinalizer
    extends EOPObserver, CycleObserver {

  abstract void processRequest(SparrowResultHolder resultHolder) throws
      RequestProcessException;

  abstract void setWriters(DataWriter[] writer);
}
