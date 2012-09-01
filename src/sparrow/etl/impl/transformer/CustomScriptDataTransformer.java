package sparrow.etl.impl.transformer;

import sparrow.etl.core.DataSet;
import sparrow.etl.core.config.SparrowDataTransformerConfig;
import sparrow.etl.core.context.SparrowContext;
import sparrow.etl.core.exception.EnrichDataException;
import sparrow.etl.core.exception.InitializationException;
import sparrow.etl.core.exception.ScriptException;
import sparrow.etl.core.log.SparrowLogger;
import sparrow.etl.core.log.SparrowrLoggerFactory;
import sparrow.etl.core.script.ScriptEngine;
import sparrow.etl.core.script.ScriptFactory;
import sparrow.etl.core.transformer.AbstractDataTransformer;
import sparrow.etl.core.transformer.DataTransformer;
import sparrow.etl.core.util.ConfigKeyConstants;
import sparrow.etl.core.util.SparrowUtil;
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
public class CustomScriptDataTransformer
    extends AbstractDataTransformer {

  /**
   *
   */
  protected static final SparrowLogger logger = SparrowrLoggerFactory.
      getCurrentInstance(
      CustomScriptDataTransformer.class);

  private static ScriptEngine se = null;

  /**
   *
   * @param config SparrowDataTransformerConfig
   */
  public CustomScriptDataTransformer(SparrowDataTransformerConfig config) {
    super(config);
    SparrowUtil.validateParam(new String[] {ConfigKeyConstants.PARAM_SCRIPT_VALUE,
                            ConfigKeyConstants.PARAM_REJECTION_REPORT_TYPE,
                            ConfigKeyConstants.PARAM_REJECTION_REPORT_SRC}
                            , "CustomScriptDataTransformer",
                            config.getInitParameter());
  }

  /**
   * enrichData
   *
   * @param dataSet DataSet
   * @return DataOutputHolder
   */
  public DataOutputHolder enrichData(DataSet dataSet) throws
      EnrichDataException {
    try {
      return (DataOutputHolder) se.evaluate(dataSet, this);
    }
    catch (ScriptException ex) {
      throw new EnrichDataException(ex);
    }
  }

  /**
   *
   */
  public void staticInitialize() {
    se = ScriptFactory.getScriptEngine(config);
    se.setArgumentVariableNames(new String[] {ScriptEngine.VAR_DATASET,
                                ScriptEngine.VAR_DATAOUT,
                                ScriptEngine.VAR_CONTEXT,
                                ScriptEngine.VAR_CONFIG,
                                ScriptEngine.VAR_LOGGER,
                                ScriptEngine.VAR_CURRENT});
    se.setArgumentClassTypes(new Class[] {DataSet.class, DataOutputHolder.class,
                             SparrowContext.class, SparrowDataTransformerConfig.class,
                             SparrowLogger.class, DataTransformer.class});
    se.setReturnType(void.class);
    try {
      se.initialize();
    }
    catch (ScriptException ex) {
      ex.printStackTrace();
      throw new InitializationException(
          "ScriptException occured while initializing Data transformer [" +
          name + "]", ex);
    }
    catch (Exception ex) {
      ex.printStackTrace();
      throw new InitializationException(
          "Exception occured while initializing Data transformer [" + name +
          "]", ex);
    }

  }

}
