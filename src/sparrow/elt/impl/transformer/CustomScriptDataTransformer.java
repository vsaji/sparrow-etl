package sparrow.elt.impl.transformer;

import sparrow.elt.core.DataSet;
import sparrow.elt.core.config.SparrowDataTransformerConfig;
import sparrow.elt.core.context.SparrowContext;
import sparrow.elt.core.exception.EnrichDataException;
import sparrow.elt.core.exception.InitializationException;
import sparrow.elt.core.exception.ScriptException;
import sparrow.elt.core.log.SparrowLogger;
import sparrow.elt.core.log.SparrowrLoggerFactory;
import sparrow.elt.core.script.ScriptEngine;
import sparrow.elt.core.script.ScriptFactory;
import sparrow.elt.core.transformer.AbstractDataTransformer;
import sparrow.elt.core.transformer.DataTransformer;
import sparrow.elt.core.util.ConfigKeyConstants;
import sparrow.elt.core.util.SparrowUtil;
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
