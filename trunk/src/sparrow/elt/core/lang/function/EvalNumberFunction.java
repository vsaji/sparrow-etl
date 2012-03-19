package sparrow.elt.core.lang.function;

import java.util.List;
import java.util.Map;


/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public class EvalNumberFunction extends AbstractFunction{

  public EvalNumberFunction(String functionName) {
    super(functionName,1);
  }


  /**
   *
   * @param values Map
   * @return String
   */
  public String getValue(Map values){
    return String.valueOf(getLongValue(values));
  }

  /**
   *
   * @param <any> values
   * @return double
   */
  public long getLongValue(Map values){
    return arguments[0].getLongValue(values);
  }


  /**
   * resolveArgumentType
   *
   * @param args List
   */
  void resolveArgumentType(List args) {
    arguments = new Expression[1];
    String arg = "("+args.get(0).toString()+")";
    arguments[0] = ExpressionResolverFactory.getEvalExceptionHandlerInstance(arg,RT_LONG);
  }
}
