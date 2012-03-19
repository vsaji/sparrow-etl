package sparrow.elt.core.cycledependency;

import sparrow.elt.core.exception.DependancyCheckException;
import sparrow.elt.core.monitor.CycleObserver;

public interface CycleEventListener extends CycleObserver {

public abstract String getName();

public abstract boolean checkDependency() throws DependancyCheckException;

public abstract boolean isProcessTerminationRequired();

public abstract String getStatusDescription();

}
