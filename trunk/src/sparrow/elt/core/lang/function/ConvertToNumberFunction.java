package sparrow.elt.core.lang.function;

import java.util.List;
import java.util.Map;
/**
 * @author Saji
 *
 */
public class ConvertToNumberFunction
    extends AbstractFunction {

  /**
   * @param functionName
   */
  public ConvertToNumberFunction(String functionName) {
    super(functionName,1);
  }

  /**
   * @param functionName
   */
  public ConvertToNumberFunction() {
    super("ConvertToNumber", 1);
  }

  /**
   *
   * @param values Map
   * @return String
   */
  public String getValue(Map values) {
    return arguments[0].getValue(values);
  }

  /**
   *
   * @param values Map
   * @return int
   */
  public int getIntValue(Map values) {
    return Integer.parseInt(arguments[0].getValue(values));
  }

  /**
   *
   * @param values Map
   * @return long
   */
  public long getLongValue(Map values) {
    return Long.parseLong(arguments[0].getValue(values));
  }

  /* (non-Javadoc)
   * @see sparrow.elt.core.lang.function.AbstractFunction#resolveArguments(java.util.List)
   */
  void resolveArgumentType(List args){
    String arg1 = args.get(0).toString();
    arguments = new Expression[1];
    arguments[0] = evaluateStringArgument(arg1);
  }
}
