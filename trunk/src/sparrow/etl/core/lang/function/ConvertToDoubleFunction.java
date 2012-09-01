package sparrow.etl.core.lang.function;

import java.util.Map;

/**
 * @author Saji
 *
 */
public class ConvertToDoubleFunction
    extends ConvertToNumberFunction {

  /**
   * @param functionName
   */
  public ConvertToDoubleFunction() {
    super("ConvertToDouble");
  }

  /**
   * @param functionName
   */
  public ConvertToDoubleFunction(String functionName) {
    super(functionName);
  }

  /**
   *
   * @param values Map
   * @return int
   */
  public double getDoubleValue(Map values) {
    return Double.parseDouble(arguments[0].getValue(values));
  }

}
