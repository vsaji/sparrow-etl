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
public class EvalDoubleFunction extends AbstractFunction{

  public EvalDoubleFunction(String functionName) {
    super(functionName,1);
  }


  /**
   *
   * @param values Map
   * @return String
   */
  public String getValue(Map values){
    String val=String.valueOf(getDoubleValue(values));
    val = (val.endsWith(".0")) ? val.replaceAll(".0",""):val;
    return val;
  }

  /**
   *
   * @param <any> values
   * @return double
   */
  public double getDoubleValue(Map values){
    return arguments[0].getDoubleValue(values);
  }

  /**
   * resolveArgumentType
   *
   * @param args List
   */
  void resolveArgumentType(List args) {
    arguments = new Expression[1];
    String arg = "("+args.get(0).toString()+")";
    arguments[0] = ExpressionResolverFactory.getEvalExceptionHandlerInstance(arg,RT_DOUBLE);
  }
}
