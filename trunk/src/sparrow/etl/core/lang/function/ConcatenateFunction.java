package sparrow.etl.core.lang.function;

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
public class ConcatenateFunction extends AbstractFunction {

  /**
   * @param functionName
   */
  public ConcatenateFunction(String funcName) {
    super(funcName,1);
  }


  /* (non-Javadoc)
   * @see sparrow.elt.core.lang.function.Expression#getValue(java.util.Map)
   */
  public String getValue(Map values) {

    StringBuffer sb = new StringBuffer();

    for(int i=0;i<arguments.length;i++){
       sb.append(arguments[i].getValue(values));
    }

    return sb.toString();
  }


  /* (non-Javadoc)
   * @see sparrow.elt.core.lang.function.AbstractFunction#resolveArguments()
   */
  void resolveArgumentType(List args) {
    String arg1 = args.get(0).toString();
    String[] ags = arg1.split("[|]");
    arguments = new Expression[ags.length];
    for(int i=0;i<ags.length;i++){
        arguments[i]=evaluateStringArgument(ags[i]);
    }
  }

}
