package sparrow.etl.impl.transformer;

import java.util.ArrayList;
import java.util.List;

import sparrow.etl.core.DataSet;
import sparrow.etl.core.config.SparrowDataTransformerConfig;
import sparrow.etl.core.exception.RejectionException;
import sparrow.etl.core.lang.function.Expression;
import sparrow.etl.core.lang.function.ExpressionResolverFactory;
import sparrow.etl.core.lang.function.FunctionUtil;
import sparrow.etl.core.log.SparrowLogger;
import sparrow.etl.core.log.SparrowrLoggerFactory;
import sparrow.etl.core.transformer.AbstractDataTransformer;
import sparrow.etl.core.transformer.PlaceHolder;
import sparrow.etl.core.transformer.PlaceHolderFactory;
import sparrow.etl.core.util.ConfigKeyConstants;
import sparrow.etl.core.util.Constants;
import sparrow.etl.core.util.SparrowUtil;
import sparrow.etl.core.vo.DataOutputHolder;


/**
 *
 * <p>Title: </p>
 * <p>Description: This class is deprecated please use TemplateBasedTransformer instead</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 * @deprecated
 */
public class ExpressionBasedTransformer
    extends AbstractDataTransformer {

  protected static Expression[] functions;
  protected static boolean functionExists;
  protected static String transExpression;
  protected static String key;
  protected static PlaceHolder[] placeHolder;
  /**
   *
   */
  protected static final SparrowLogger logger = SparrowrLoggerFactory.
      getCurrentInstance(
      ExpressionBasedTransformer.class);

  /**
   *
   * @param config SparrowDataTransformerConfig
   */
  public ExpressionBasedTransformer(SparrowDataTransformerConfig config) {
    super(config);
    SparrowUtil.validateParam(new String[] {ConfigKeyConstants.PARAM_EXPRESSION,
                            ConfigKeyConstants.PARAM_REJECTION_REPORT_TYPE,
                            ConfigKeyConstants.PARAM_REJECTION_REPORT_SRC}
                            , "ExpressionBasedTransformer",
                            config.getInitParameter());
  }

  /**
   * enrichData
   *
   * @param dataSet DataSet
   * @return DataOutputHolder
   */
  public DataOutputHolder enrichData(DataSet dataSet) {
    String transE = transExpression;

    DataOutputHolder doh = null;
    try {
      if (functionExists) {
        transE = FunctionUtil.executeFunction(transE, dataSet.getDataSetAsKeyValue(),
                                           functions);
      }
      transE = SparrowUtil.replaceTokens(transE, dataSet.getDataSetAsKeyValue());
      doh = new DataOutputHolder();

      for(int i=0; i < placeHolder.length; i++){
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
    transExpression = config.getInitParameter().getParameterValue(
        ConfigKeyConstants.PARAM_EXPRESSION);
    key = SparrowUtil.performTernary(config.getInitParameter(),
                                   ConfigKeyConstants.PARAM_KEY_NAME,
                                   config.getName());
    String placeHlder = SparrowUtil.performTernary(config.getInitParameter(),
                                                 ConfigKeyConstants.
                                                 PARAM_PLACEHOLDER,
                                                 Constants.OBJECT);
    String[] plcHlders = (placeHlder.indexOf(",") > 0) ? placeHlder.split("[,]") :
        new String[] {placeHlder};

    placeHolder = new PlaceHolder[plcHlders.length];

    for(int i=0; i < plcHlders.length; i++){
       placeHolder[i]=PlaceHolderFactory.resolvePlaceHolder(plcHlders[i]);
    }

    //placeHolder = PlaceHolderFactory.resolvePlaceHolder(placeHlder);

    functionExists = (transExpression.indexOf(Constants.FUNCTION_TOKEN) != -1);
    if (functionExists) {
      List temp = new ArrayList();
      transExpression = FunctionUtil.resolveFunctions(transExpression, temp);
      System.out.println(transExpression);
      functions = getFunctions(temp);
    }
  }

  /**
   *
   * @param functions
   * @return
   */
  private Expression[] getFunctions(List functions) {
    Expression[] e = new Expression[functions.size()];
    for (int i = 0; i < functions.size(); i++) {
      String element = (String) functions.get(i);
      e[i] = ExpressionResolverFactory.resolveExpression(element);
    }
    return e;
  }

}
