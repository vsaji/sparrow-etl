package sparrow.etl.impl.transformer;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.mvel.MVEL;

import sparrow.etl.core.DataSet;
import sparrow.etl.core.config.SparrowDataTransformerConfig;
import sparrow.etl.core.exception.InitializationException;
import sparrow.etl.core.log.SparrowLogger;
import sparrow.etl.core.log.SparrowrLoggerFactory;
import sparrow.etl.core.script.ScriptContent;
import sparrow.etl.core.transformer.AbstractDataTransformer;
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
public class MVELBasedDataTransformer
    extends AbstractDataTransformer {

  /**
   *
   */
  protected static final SparrowLogger logger = SparrowrLoggerFactory.
      getCurrentInstance(
      MVELBasedDataTransformer.class);


  private static Serializable compiled;

  /**
   *
   * @param config SparrowDataTransformerConfig
   */
  public MVELBasedDataTransformer(SparrowDataTransformerConfig config) {
    super(config);
  }

  /**
   * enrichData
   *
   * @param dataSet DataSet
   * @return DataOutputHolder
   */
  public DataOutputHolder enrichData(DataSet dataSet) {

    Map vars = new HashMap();
    DataOutputHolder dataout = new DataOutputHolder();
    vars.put("dataset",dataSet);
    vars.put("dataout",dataout);
    vars.put("context",context);
    vars.put("config",config);
    vars.put("logger",logger);
    vars.put("current",this);

    MVEL.executeExpression(compiled,vars);

    return dataout;
  }

  /**
   *
   */
  public void staticInitialize() {
    try {
      ScriptContent sc = ScriptContent.getScriptContent(config.getInitParameter());
      compiled = MVEL.compileExpression(sc.getContent());
    }
    catch (Exception ex) {
      ex.printStackTrace();
      throw new InitializationException(
          "Exception occured while initializing Data transformer [" + name +
          "]", ex);
    }
  }

}
