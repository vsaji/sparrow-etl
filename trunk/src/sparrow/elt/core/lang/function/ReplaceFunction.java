/**
 *
 */
package sparrow.elt.core.lang.function;

import java.util.List;
import java.util.Map;

import sparrow.elt.core.util.SparrowUtil;


/**
 * @author Saji
 *
 */
public class ReplaceFunction
    extends AbstractFunction {

  public ReplaceFunction(String functionName) {
    super(functionName, 3);
  }

  public ReplaceFunction() {
    super("Replace", 3);
  }

  /* (non-Javadoc)
   * @see sparrow.elt.core.lang.function.Expression#getValue(java.util.Map)
   */
  public String getValue(Map values) {
    String string = arguments[0].getValue(values);
    String source = arguments[1].getValue(values);
    String replaceWith = arguments[2].getValue(values);

    if (string == null || source == null || replaceWith == null) {
      return null;
    }
    return SparrowUtil.replace(string, source, replaceWith);
  }

  /* (non-Javadoc)
   * @see sparrow.elt.core.lang.function.AbstractFunction#resolveArguments()
   */
  void resolveArgumentType(List args) {
    arguments = new Expression[3];
    String arg1 = args.get(0).toString();
    String arg2 = args.get(1).toString();
    String arg3 = args.get(2).toString();

    arguments[0] = evaluateStringArgument(arg1);
    arguments[1] = evaluateStringArgument(arg2);
    arguments[2] = evaluateStringArgument(arg3);
  }

}
