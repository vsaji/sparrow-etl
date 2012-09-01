package sparrow.etl.core.cycledependency;

import sparrow.etl.core.exception.DependancyCheckException;
import sparrow.etl.core.monitor.CycleObserver;

public interface CycleEventListener extends CycleObserver {

public abstract String getName();

public abstract boolean checkDependency() throws DependancyCheckException;

public abstract boolean isProcessTerminationRequired();

public abstract String getStatusDescription();

}
