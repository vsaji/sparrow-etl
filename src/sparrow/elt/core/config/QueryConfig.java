package sparrow.elt.core.config;

import sparrow.elt.core.util.ConfigKeyConstants;
import sparrow.elt.core.util.Constants;
import sparrow.elt.core.util.SparrowUtil;

public interface QueryConfig {

  abstract String getSQL();

  abstract String getDBSource();

  abstract int getFetchSize();

  abstract String getResultWrapType();

  abstract ConfigParam getParameter();

  abstract String getSPOutParamter();

}

