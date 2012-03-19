package sparrow.elt.core.initializer;

import sparrow.elt.core.context.SparrowContext;
import sparrow.elt.core.monitor.AppMonitor;
import sparrow.elt.core.monitor.CycleMonitor;

public interface Initializable {

  public abstract void initialize(SparrowContext context, AppMonitor appMon,
                                  CycleMonitor cycleMon);

}
