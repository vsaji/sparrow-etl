package sparrow.elt.impl.script;

import java.lang.reflect.InvocationTargetException;

import org.codehaus.janino.ScriptEvaluator;

import sparrow.elt.core.DataSet;
import sparrow.elt.core.config.SparrowDataTransformerConfig;
import sparrow.elt.core.exception.InitializationException;
import sparrow.elt.core.exception.ScriptException;
import sparrow.elt.core.script.AbstractScriptEngine;
import sparrow.elt.core.transformer.DataTransformer;
import sparrow.elt.core.vo.DataOutputHolder;

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public class JaninoScriptEngine
    extends AbstractScriptEngine {

  private ScriptEvaluator se;

  /**
   *
   * @param config ConfigParam
   */
  public JaninoScriptEngine(SparrowDataTransformerConfig config) {
    super(config);
  }
  /**
   *
   */
  public JaninoScriptEngine(){
    super();
  }

  /**
   *
   * @param expression String
   * @param returnType Class
   * @param paramName String[]
   * @param classTypes Class[]
   */
  public JaninoScriptEngine(String expression, Class returnType,
                            String[] paramName, Class[] classTypes) {
    super(expression);
    try {
      se = new ScriptEvaluator(expression, returnType, paramName, classTypes);
    }
    catch (Exception ex) {
      throw new InitializationException(ex);
    }
  }

  /**
   *
   */
  public void initialize() {
      try {
        se = new ScriptEvaluator(getScript(), getReturnType(), getVariableNames(),getVariableTypeClasses());
      }
      catch (Exception ex) {
        throw new InitializationException(ex);
      }
      super.initialize();
  }


  /**
   *
   * @param map Map
   * @throws ScriptException
   * @return Object
   */
  public Object evaluate(Object[] values) throws ScriptException {
    try {
      return se.evaluate(values);
    }
    catch (InvocationTargetException ex) {
      ex.printStackTrace();
      throw new ScriptException(ex);
    }
  }

  /**
   * evaluate
   *
   * @param expression String
   * @param varaible Map
   * @return Object
   */
  public Object evaluate(DataSet dataSet, DataTransformer dt) throws
      ScriptException {

    DataOutputHolder dataout = new DataOutputHolder();

    try {
      se.evaluate(new Object[] {dataSet, dataout, config.getContext(), config,
                  logger, dt});

    }
    catch (Exception ex) {
      ex.printStackTrace();
      throw new ScriptException("Janino", ex.getMessage(), ex);
    }

    return dataout;
  }
}
