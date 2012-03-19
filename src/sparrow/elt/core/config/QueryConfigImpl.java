package sparrow.elt.core.config;

import sparrow.elt.core.util.ConfigKeyConstants;
import sparrow.elt.core.util.Constants;
import sparrow.elt.core.util.SparrowUtil;

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */
public class QueryConfigImpl
    implements QueryConfig {

  private final String sql;
  private final String dbSource;
  private final int fetchSize;
  private final String resultWrapType;
  private final String spOutParameter;
  private final ConfigParam param;

  public QueryConfigImpl(ConfigParam param) {
    sql = param.getParameterValue(ConfigKeyConstants.PARAM_QUERY);
    dbSource = param.getParameterValue(ConfigKeyConstants.PARAM_RESOURCE);
    fetchSize = SparrowUtil.performTernary(param,
                                         ConfigKeyConstants.PARAM_FETCH_SIZE, 0);

    resultWrapType = SparrowUtil.performTernary(param, ConfigKeyConstants.
                                              PARAM_RESULT_WRAP,
                                              Constants.RESULT_WRAP_DISCONNECTED);

    spOutParameter= param.getParameterValue("sp.out.param");
    this.param = param;
  }

  /**
   * getSQL
   *
   * @return String
   */
  public String getSQL() {
    return sql;
  }

  /**
   * getDBSource
   *
   * @return String
   */
  public String getDBSource() {
    return dbSource;
  }

  /**
   * getFetchSize
   *
   * @return int
   */
  public int getFetchSize() {
    return fetchSize;
  }

  /**
   * getResultWrapType
   *
   * @return String
   */
  public String getResultWrapType() {
    return resultWrapType;
  }

  /**
   * getParameter
   *
   * @return ConfigParam
   */
  public ConfigParam getParameter() {
    return param;
  }

  /**
   * getSPOutParamter
   *
   * @return String
   */
  public String getSPOutParamter() {
    return spOutParameter;
  }

}
