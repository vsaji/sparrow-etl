package sparrow.etl.core.config;

import sparrow.etl.core.util.ConfigKeyConstants;
import sparrow.etl.core.util.Constants;
import sparrow.etl.core.util.SparrowUtil;

public interface QueryConfig {

  abstract String getSQL();

  abstract String getDBSource();

  abstract int getFetchSize();

  abstract String getResultWrapType();

  abstract ConfigParam getParameter();

  abstract String getSPOutParamter();

}

