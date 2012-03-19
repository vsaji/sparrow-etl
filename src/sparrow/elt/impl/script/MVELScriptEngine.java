package sparrow.elt.impl.script;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.mvel.MVEL;

import sparrow.elt.core.DataSet;
import sparrow.elt.core.config.SparrowDataTransformerConfig;
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
public class MVELScriptEngine
    extends AbstractScriptEngine {

  private Serializable compiled;

  /**
   *
   * @param config ConfigParam
   */
  public MVELScriptEngine(SparrowDataTransformerConfig config) {
    super(config);
  }

  public MVELScriptEngine(){
    super();
  }


  public MVELScriptEngine(String expression){
    super(expression);
    compiled = MVEL.compileExpression(expression);
  }

  /**
   *
   */
  public void initialize() {
    compiled = MVEL.compileExpression(sc.getContent());
    super.initialize();
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

    Map vars = new HashMap();
    DataOutputHolder dataout = new DataOutputHolder();
    vars.put(VAR_DATASET, dataSet);
    vars.put(VAR_DATAOUT, dataout);
    vars.put(VAR_CONTEXT, config.getContext());
    vars.put(VAR_CONFIG, config);
    vars.put(VAR_LOGGER, logger);
    vars.put(VAR_CURRENT, dt);

    try {
      MVEL.executeExpression(compiled, vars);
    }
    catch (Exception ex) {
      ex.printStackTrace();
      throw new ScriptException("MVEL", ex.getMessage(), ex);
    }

    return dataout;
  }

}
