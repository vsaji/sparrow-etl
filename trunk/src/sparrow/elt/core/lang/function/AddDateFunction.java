package sparrow.elt.core.lang.function;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import sparrow.elt.core.exception.ParserException;
import sparrow.elt.core.util.SparrowUtil;


/**
 * @author Saji Venugopalan
 *
 */
public class AddDateFunction
    extends AbstractFunction {

  /**
   * @param functionName
   */
  public AddDateFunction(String funcName) {
    super(funcName, 3);
  }

  /* (non-Javadoc)
   * @see sparrow.elt.core.lang.function.Expression#getValue(java.util.Map)
   */
  public Date getDateValue(Map values) {
    Date date = arguments[0].getDateValue(values);
    String field = arguments[1].getValue(values);
    int amount = arguments[2].getIntValue(values);

    if (date != null) {
      Calendar cal = Calendar.getInstance();
      cal.setTime(date);
      try {
        int a = Calendar.class.getDeclaredField(field.toUpperCase()).getInt(cal);
        cal.add(a, amount);
      }
      catch (Exception ex) {
        ex.printStackTrace();
      }
      return cal.getTime();
    }

    return null;
  }

  /**
   *
   * @param values Map
   * @return Date
   */
  public String getValue(Map values) {
    Date date = getDateValue(values);
    if (date != null) {
      return SparrowUtil.formatDate(date, "yyyy-MM-dd HH:mm:ss");
    }
    return null;
  }

  /* (non-Javadoc)
   * @see sparrow.elt.core.lang.function.AbstractFunction#resolveArguments()
   */
  void resolveArgumentType(List args) throws ParserException {
    arguments = new Expression[3];
    String arg1 = args.get(0).toString();
    String arg2 = args.get(1).toString();
    String arg3 = args.get(2).toString();
    arguments[0] = evaluateDateArgument(arg1);
    arguments[1] = evaluateStringArgument(arg2);
    arguments[2] = evaluateIntArgument(arg3);
  }

}
