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
public class ConvertToString
    extends AbstractFunction {

  /**
   * @param functionName
   */
  public ConvertToString(String functionName) {
    super(functionName,1);
  }

  /**
   * @param functionName
   */
  public ConvertToString() {
    super("ConvertToString", 1);
  }

  /**
   *
   * @param values Map
   * @return String
   */
  public String getValue(Map values) {
    return arguments[0].getValue(values);
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
