package sparrow.elt.core.resource;

import sparrow.elt.core.monitor.AppObserver;

public interface GenericResourceInitializer
    extends AppObserver {

  abstract void initializeResource();

  abstract Resource getResource();

}
