package sparrow.elt.impl.transformer.template;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import sparrow.elt.core.exception.EvaluatorException;
import sparrow.elt.core.exception.ParserException;
import sparrow.elt.core.lang.function.Expression;
import sparrow.elt.core.lang.function.FunctionUtil;
import sparrow.elt.core.log.SparrowLogger;
import sparrow.elt.core.log.SparrowrLoggerFactory;
import sparrow.elt.core.transformer.PlaceHolder;
import sparrow.elt.core.util.Constants;
import sparrow.elt.core.util.SparrowUtil;


/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public class SparrowTemplateProcessor
    implements TemplateProcessor {

  protected static Expression[] functions;
  protected static boolean functionExists;
  protected static String transExpression;
  protected static PlaceHolder[] placeHolder;
  /**
   *
   */
  protected static final SparrowLogger logger = SparrowrLoggerFactory.
      getCurrentInstance(
      SparrowTemplateProcessor.class);

  /**
   *
   * @param config SparrowDataTransformerConfig
   */
  public SparrowTemplateProcessor() {
  }

  /**
   * enrichData
   *
   * @param dataSet DataSet
   * @return DataOutputHolder
   */
  public String render(Map values) throws
      EvaluatorException {
    String transE = transExpression;
    try {
      if (functionExists) {
        transE = FunctionUtil.executeFunction(transE, values,
                                              functions);
      }
      transE = SparrowUtil.replaceTokens(transE, values);
    }
    catch (Exception e) {
      throw new EvaluatorException(
          "Exception occured while rendering the teamplate", e);
    }
    return transE;
  }

  /**
   *
   */
  public void parse(String expression) throws ParserException {
    functionExists = (expression.indexOf(Constants.FUNCTION_TOKEN) != -1);
    transExpression = expression;
    if (functionExists) {
      List temp = new ArrayList();
      transExpression = FunctionUtil.resolveFunctions(expression, temp);
      functions = FunctionUtil.getFunctions(temp);
    }
  }
}
