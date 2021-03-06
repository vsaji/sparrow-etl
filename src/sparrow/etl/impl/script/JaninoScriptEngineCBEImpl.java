package sparrow.etl.impl.script;

import java.io.StringReader;

import org.codehaus.janino.ClassBodyEvaluator;
import org.codehaus.janino.Scanner;

import sparrow.etl.core.DataSet;
import sparrow.etl.core.config.SparrowDataTransformerConfig;
import sparrow.etl.core.exception.InitializationException;
import sparrow.etl.core.exception.ScriptException;
import sparrow.etl.core.script.AbstractScriptEngine;
import sparrow.etl.core.transformer.DataTransformer;
import sparrow.etl.core.vo.DataOutputHolder;

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public class JaninoScriptEngineCBEImpl
    extends AbstractScriptEngine {

  private DataTransformerWrapper wrapper;

  /**
   *
   * @param config ConfigParam
   */
  public JaninoScriptEngineCBEImpl(SparrowDataTransformerConfig config) {
    super(config);

  }

  /**
   *
   */
  public void initialize() {
    try {
      StringBuffer defaultImport = new StringBuffer();
      defaultImport.append("import ").append(ScriptVariables.class.getName()).
          append(";\n");
      defaultImport.append(getScript());
      setScriptContent(defaultImport.toString());

      wrapper = (DataTransformerWrapper) ClassBodyEvaluator.
          createFastClassBodyEvaluator(
          new Scanner(null, new StringReader(getScript())),"sparrow.etl.impl.script.DataTransformer",
          null,new Class[]{DataTransformerWrapper.class},
          (ClassLoader)null);
    }
    catch (Exception ex) {
      throw new InitializationException(ex);
    }
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

    DataOutputHolder dataout = new DataOutputHolder();

    try {
      ScriptVariables vars = new ScriptVariables(dataSet, dt, config.getContext(),
                                                 config, logger, dataout);
      getInstance().enrichData(vars);
    }
    catch (Exception ex) {
      ex.printStackTrace();
      throw new ScriptException("Janino CBE [ClassBodyEvaluator] Implemention", ex.getMessage(), ex);
    }

    return dataout;
  }

  /**
   *
   * @throws Exception
   * @throws InstantiationException
   * @return DataTransformerWrapper
   */
  private DataTransformerWrapper getInstance() throws Exception,
      InstantiationException {
    return (isNewInstancePerRequest()) ?
        (DataTransformerWrapper) wrapper.getClass().newInstance() : wrapper;
  }

}
