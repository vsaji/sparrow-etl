package sparrow.etl.core.script;

import java.util.Map;

import sparrow.etl.core.config.ConfigParam;
import sparrow.etl.core.config.SparrowDataTransformerConfig;
import sparrow.etl.core.exception.InitializationException;
import sparrow.etl.core.util.ConfigKeyConstants;
import sparrow.etl.core.util.SparrowUtil;


/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public class ScriptFactory {

  private static final Map CLASS_IDENTIFIER = SparrowUtil.getImplConfig("script");
  private static final String DEFAULT_LANG = "java";

  private ScriptFactory() {
  }

  /**
   *
   * @param config SparrowDataTransformerConfig
   * @return ScriptEngine
   */
  public static final ScriptEngine getScriptEngine(SparrowDataTransformerConfig
      config) {
    String type = config.getInitParameter().getParameterValue(ConfigKeyConstants.PARAM_SCRIPT_LANG);
    String className = getClassName(config.getInitParameter());
    if (className == null) {
      throw new InitializationException("Script Type:[" + type +
                                        "] is not supported");
    }
    return (ScriptEngine) SparrowUtil.createObject(className,
                                                 SparrowDataTransformerConfig.class,
                                                 config);
  }

  /**
   *
   * @param scriptName String
   * @param expression String
   * @return ScriptEngine
   */
  public static final ScriptEngine getScriptEngine(String scriptName,String expression){
    return (ScriptEngine) SparrowUtil.createObject(scriptName,String.class,
                                             expression);
  }

  /**
   *
   * @param scriptLang String
   * @return ScriptEngine
   */
  public static final ScriptEngine getScriptEngine(String scriptLang){
    String className = getClassName(scriptLang);
    return (ScriptEngine) SparrowUtil.createObject(className);
  }


  /**
   *
   * @return ScriptEngine
   */
  public static final ScriptEngine getScriptEngine(){
    String className = getClassName(DEFAULT_LANG);
    return (ScriptEngine) SparrowUtil.createObject(className);
  }


  /**
   *
   * @param type String
   * @return String
   */
  static String getClassName(ConfigParam param){
    String lang = SparrowUtil.performTernary(param,ConfigKeyConstants.PARAM_SCRIPT_LANG,DEFAULT_LANG);
    return getClassName(lang);
  }


  /**
   *
   * @param lang String
   * @return String
   */
  static String getClassName(String lang){
    return(String) CLASS_IDENTIFIER.get(lang);
  }


}
