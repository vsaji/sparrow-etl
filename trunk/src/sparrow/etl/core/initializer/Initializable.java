package sparrow.etl.core.initializer;

import sparrow.etl.core.context.SparrowContext;
import sparrow.etl.core.monitor.AppMonitor;
import sparrow.etl.core.monitor.CycleMonitor;

public interface Initializable {

  public abstract void initialize(SparrowContext context, AppMonitor appMon,
                                  CycleMonitor cycleMon);

}
