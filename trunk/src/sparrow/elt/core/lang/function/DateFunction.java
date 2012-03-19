package sparrow.elt.core.lang.function;

import java.util.Date;
import java.util.List;
import java.util.Map;

import sparrow.elt.core.exception.ParserException;
import sparrow.elt.core.util.SparrowUtil;


/**
 * @author Saji
 *
 */
public class DateFunction
    extends AbstractFunction {

  private boolean isArgExist = false;
  /**
   * @param functionName
   */
  public DateFunction(String functionName) {
    super(functionName, -1);
  }

  /**
   * @param functionName
   */
  public DateFunction() {
    super("Date", -1);
  }

  /**
   *
   */
  public String getValue(Map values) {
    return SparrowUtil.formatDate(getDateValue(values), "dd-MM-yyyy");
  }

  /**
   *
   * @param values Map
   * @return Date
   */
  public Date getDateValue(Map values) {
    if (!isArgExist) {
      return new Date();
    }
    return arguments[0].getDateValue(values);
  }

  /* (non-Javadoc)
   * @see sparrow.elt.core.lang.function.AbstractFunction#resolveArguments(java.util.List)
   */
  void resolveArgumentType(List args) throws ParserException {
    if (args.size() != 0) {
      isArgExist = true;
      arguments = new Expression[1];

      String arg1 = args.get(0).toString();
      String arg2 = (args.size() > 1) ? args.get(1).toString() : null;
      arguments[0] = evaluateDateArgument(arg1,arg2);
    }
  }
}
