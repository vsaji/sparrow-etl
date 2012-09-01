package sparrow.etl.core.config;

import sparrow.etl.core.context.SparrowContext;

public interface SparrowConfig {

  abstract ConfigParam getInitParameter();

  abstract SparrowContext getContext();

  abstract String getName();

//  abstract String getDescription();

}
