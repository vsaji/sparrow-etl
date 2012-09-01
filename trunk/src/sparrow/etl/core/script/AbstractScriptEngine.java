package sparrow.etl.core.script;

import sparrow.etl.core.DataSet;
import sparrow.etl.core.config.ConfigParam;
import sparrow.etl.core.config.SparrowDataTransformerConfig;
import sparrow.etl.core.exception.ScriptException;
import sparrow.etl.core.log.SparrowLogger;
import sparrow.etl.core.log.SparrowrLoggerFactory;
import sparrow.etl.core.transformer.DataTransformer;
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
abstract public class AbstractScriptEngine
    implements ScriptEngine {

  protected final SparrowDataTransformerConfig config;
  protected final ScriptContent sc;
  protected String script;
  protected static final SparrowLogger logger = SparrowrLoggerFactory.
      getCurrentInstance(
      AbstractScriptEngine.class);

  protected String[] variableNames;
  protected Class[] variableTypeClasses;
  protected Class returnType;
  protected final boolean newInstancePerRequest;

  /**
   *
   * @param config ConfigParam
   */
  protected AbstractScriptEngine(SparrowDataTransformerConfig config) {
    this.config = config;
    this.sc = ScriptContent.getScriptContent(config.getInitParameter());
    this.script = sc.getContent();
    this.newInstancePerRequest = SparrowUtil.performTernary(config.
        getInitParameter(), "new.instance.per.request", false);
  }

  /**
   *
   */
  protected AbstractScriptEngine() {
    this.config = null;
    this.sc = null;
    this.newInstancePerRequest = false;
  }

  protected AbstractScriptEngine(String script) {
    this.script = script;
    this.config = null;
    this.sc = null;
    this.newInstancePerRequest = false;
  }

  /**
   *
   * @param param ConfigParam
   */
  protected AbstractScriptEngine(ConfigParam param) {
    this.sc = ScriptContent.getScriptContent(param);
    this.script = script;
    this.config = null;
    this.newInstancePerRequest = SparrowUtil.performTernary(param,
        "new.instance.per.request", false);
  }

  /**
   * initialize
   */
  public void initialize() {
  }

  protected Class getReturnType() {
    return returnType;
  }

  protected Class[] getVariableTypeClasses() {
    return variableTypeClasses;
  }

  protected String getScript() {
    return script;
  }

  protected String[] getVariableNames() {
    return variableNames;
  }

  protected boolean isNewInstancePerRequest(){
    return this.newInstancePerRequest;
  }

  /**
   * close
   */
  public void close() {
  }

  /**
   * evaluate
   *
   * @param expression String
   * @param varaible Map
   */
  public abstract Object evaluate(DataSet dataSet, DataTransformer dt) throws
      ScriptException;

  /**
   * setArgumentClassTypes
   *
   * @param clazzes Class[]
   */
  public void setArgumentClassTypes(Class[] clazzes) {
    this.variableTypeClasses = clazzes;
  }

  /**
   * setArgumentVariableNames
   *
   * @param names String[]
   */
  public void setArgumentVariableNames(String[] names) {
    this.variableNames = names;
  }

  /**
   * setReturnType
   *
   * @param clazz Class
   */
  public void setReturnType(Class clazz) {
    this.returnType = clazz;
  }

  /**
   * setScriptContent
   *
   * @param script String
   */
  public void setScriptContent(String script) {
    this.script = script;
  }

  public Object evaluate(Object[] values) throws
      ScriptException {
    return null;
  }

}
