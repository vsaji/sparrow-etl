package sparrow.elt.core.lang.function;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

import sparrow.elt.core.exception.ParserException;


/**
 * @author Saji
 *
 */
public class FormatDecimalFunction
    extends AbstractFunction {

  /**
   * @param functionName
   */
  public FormatDecimalFunction(String functionName) {
    super(functionName, 2);
  }

  /**
   * @param functionName
   */
  public FormatDecimalFunction() {
    super("FormatDecimal", 2);
  }

  /**
   *
   */
  public String getValue(Map values) {
    double d = arguments[0].getDoubleValue(values);
    String format = arguments[1].getValue(values);
    return new DecimalFormat(format).format(d);
  }

  /* (non-Javadoc)
   * @see sparrow.elt.core.lang.function.AbstractFunction#resolveArguments(java.util.List)
   */
  void resolveArgumentType(List args) throws ParserException {
    String arg1 = args.get(0).toString();
    String arg2 = args.get(1).toString();

    arguments = new Expression[2];
    arguments[0] = evaluateDoubleArgument(arg1);
    arguments[1] = evaluateStringArgument(arg2);
  }
}
