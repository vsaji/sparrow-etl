package sparrow.etl.core.util;

import sparrow.etl.core.config.ConfigParam;

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */
public final class ContextParam {


  private static ConfigParam staticParam = null;

  ContextParam() {
  }

  final static void setContextConfigParam(ConfigParam param) {
    staticParam = param;
  }

  public final static String getContextParamValue(String key) {
    return staticParam.getParameterValue(key);
  }

}
