package sparrow.elt.core.config;

import sparrow.elt.core.context.SparrowContext;

public interface SparrowConfig {

  abstract ConfigParam getInitParameter();

  abstract SparrowContext getContext();

  abstract String getName();

//  abstract String getDescription();

}
