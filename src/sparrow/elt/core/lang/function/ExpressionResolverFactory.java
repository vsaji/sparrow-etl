package sparrow.elt.core.lang.function;

import java.util.Map;

import sparrow.elt.core.exception.InitializationException;
import sparrow.elt.core.exception.ParserException;
import sparrow.elt.core.util.SparrowUtil;


/**
 *
 * @author Saji
 *
 */
public class ExpressionResolverFactory {

  private static final Map EXPRESSION_REPOSITORY = SparrowUtil.getImplConfig(
      "functions");

  /**
   *
   * @param expression
   * @return
   */
  public static final Expression resolveExpression(String function) {
    String functionName = function.substring(0, function.indexOf("("));
    String className = (String) EXPRESSION_REPOSITORY.get(functionName);
    Expression e = null;
    if (className != null) {
      e = (Expression) SparrowUtil.createObject(className, String.class,
                                              functionName);
      try {
        e.parse(function);
      }
      catch (ParserException e1) {
        e1.printStackTrace();
      }
    }
    else {
      throw new InitializationException("FUNCTION_DOESNOT_EXIST",
                                        "Function [" + functionName +
                                        "] does not exist");
    }
    return e;
  }

  /**
   *
   * @param expression
   * @return
   */
  public static final Expression getEvalExceptionHandlerInstance(String
      function) {
    return getEvalExceptionHandlerInstance(function, Expression.RT_INT);
  }

  /**
   *
   * @param expression
   * @return
   */
  public static final Expression getEvalExceptionHandlerInstance(String function,int returnType){
    Expression e = null;
    try {
      e = parse(new EvalExpressionHandler(returnType),function);
    } catch (ParserException e1) {
      e1.printStackTrace();
    }
    return e;
  }

  /**
   *
   * @param function
   * @return
   */
  public static final Expression parse(Expression e,String function) throws ParserException{
    e.parse(function);
    return e;
  }


}
