package sparrow.etl.impl.transformer;

import sparrow.etl.core.DataSet;
import sparrow.etl.core.config.ConfigParamImpl;
import sparrow.etl.core.config.SparrowDataTransformerConfig;
import sparrow.etl.core.exception.EnrichDataException;
import sparrow.etl.core.exception.InitializationException;
import sparrow.etl.core.exception.ParserException;
import sparrow.etl.core.exception.RejectionException;
import sparrow.etl.core.log.SparrowLogger;
import sparrow.etl.core.log.SparrowrLoggerFactory;
import sparrow.etl.core.transformer.AbstractDataTransformer;
import sparrow.etl.core.transformer.PlaceHolder;
import sparrow.etl.core.transformer.PlaceHolderFactory;
import sparrow.etl.core.util.ConfigKeyConstants;
import sparrow.etl.core.util.Constants;
import sparrow.etl.core.util.SparrowUtil;
import sparrow.etl.core.vo.DataOutputHolder;
import sparrow.etl.impl.transformer.template.SparrowTemplateFactory;
import sparrow.etl.impl.transformer.template.TemplateProcessor;

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public class TemplateBasedTransformer
    extends AbstractDataTransformer {

  private static final SparrowLogger logger = SparrowrLoggerFactory.
      getCurrentInstance(
      TemplateBasedTransformer.class);

  private static PlaceHolder[] placeHolder;
  private static String key;

  private final TemplateProcessor template;

  /**
   *
   * @param config SparrowDataTransformerConfig
   */
  public TemplateBasedTransformer(SparrowDataTransformerConfig config) {
    super(config);
    SparrowUtil.validateParam(new String[] {ConfigKeyConstants.PARAM_EXPRESSION,
                            ConfigKeyConstants.PARAM_REJECTION_REPORT_TYPE,
                            ConfigKeyConstants.PARAM_REJECTION_REPORT_SRC}
                            , "TemplateBasedTransformer",
                            config.getInitParameter());

    String templateProcessor = SparrowUtil.performTernary(config.getInitParameter(),
        "template.processor", "sparrow");
    template = SparrowTemplateFactory.createTemplateProcessor(templateProcessor);

    /**
     * enrichData
     *
     * @param dataSet DataSet
     * @return DataOutputHolder
     */
  }

  public DataOutputHolder enrichData(DataSet dataSet) throws
      EnrichDataException {
    DataOutputHolder doh = null;
    try {
      String transE = template.render(dataSet.getDataSetAsKeyValue());
      doh = new DataOutputHolder();

      for (int i = 0; i < placeHolder.length; i++) {
        placeHolder[i].setValue(doh, transE, key);
      }
    }
    catch (Exception e) {
      try {
        super.markForRejection("Exception [" + e.getClass().getName() +
                               "] occured while transforming input");
      }
      catch (RejectionException ex) {
        ex.printStackTrace();
      }
    }
    return doh;
  }

  /**
   *
   */
  public void staticInitialize() {
    key = SparrowUtil.performTernary(config.getInitParameter(),
                                   ConfigKeyConstants.PARAM_KEY_NAME,
                                   config.getName());

    this.resolvePlaceHolder();
    String expression = this.resolveExpression();

    try {
      template.parse(expression);
    }
    catch (ParserException ex) {
      throw new InitializationException(
          "ParserException occured while parsing Expression [" + expression +
          "]", ex);
    }
  }

  /**
   *
   * @return String
   */
  private String resolveExpression() {
    String expression = config.getInitParameter().getParameterValue(
        ConfigKeyConstants.PARAM_EXPRESSION);

    if (expression.trim().endsWith(".tmplt")) {
      try {
        expression = SparrowUtil.readTextFile(expression);
        expression = ConfigParamImpl.scanVars(expression, false, null);
      }
      catch (Exception ex) {
        throw new InitializationException(
            "Exception occured while reading or scanning the varaibles in expression [" +
            expression +
            "]", ex);
      }
    }
    return expression;
  }

  /**
   *
   */
  private void resolvePlaceHolder() {
    String placeHlder = SparrowUtil.performTernary(config.getInitParameter(),
                                                 ConfigKeyConstants.
                                                 PARAM_PLACEHOLDER,
                                                 Constants.OBJECT);

    String[] plcHlders = (placeHlder.indexOf(",") > 0) ? placeHlder.split("[,]") :
        new String[] {
        placeHlder};

    placeHolder = new PlaceHolder[plcHlders.length];

    for (int i = 0; i < plcHlders.length; i++) {
      placeHolder[i] = PlaceHolderFactory.resolvePlaceHolder(plcHlders[i]);
    }
  }

}
