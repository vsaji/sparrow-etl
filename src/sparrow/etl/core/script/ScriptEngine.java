package sparrow.etl.core.script;

import sparrow.etl.core.DataSet;
import sparrow.etl.core.exception.ScriptException;
import sparrow.etl.core.transformer.DataTransformer;

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public interface ScriptEngine {


  public static final String VAR_DATASET = "_dataset";
  public static final String VAR_DATAOUT ="_dataout";
  public static final String VAR_CONTEXT ="_context";
  public static final String VAR_CONFIG = "_config";
  public static final String VAR_LOGGER = "_logger";
  public static final String VAR_CURRENT = "_this";
  public static final String VAR_DATAHANDLER = "_datahandler";
  public static final String VAR_UTIL ="_util";

  abstract void initialize() throws ScriptException;

  abstract Object evaluate(DataSet ds, DataTransformer dt) throws
      ScriptException;

  abstract Object evaluate(Object[] values) throws
      ScriptException;

  abstract void close() throws ScriptException;

  abstract void setReturnType(Class clazz);

  abstract void setArgumentClassTypes(Class[] clazzes);

  abstract void setArgumentVariableNames(String[] names);

  abstract void setScriptContent(String script);

}
