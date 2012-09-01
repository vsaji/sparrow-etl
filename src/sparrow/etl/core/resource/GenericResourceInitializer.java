package sparrow.etl.core.resource;

import sparrow.etl.core.monitor.AppObserver;

public interface GenericResourceInitializer
    extends AppObserver {

  abstract void initializeResource();

  abstract Resource getResource();

}
