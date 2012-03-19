package sparrow.elt.core.lang.function;

import java.util.Map;
import java.util.List;

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public class IsEmptyFunction extends AbstractFunction {
  /**
  * @param functionName
  */
 public IsEmptyFunction(String funcName) {
   super(funcName,1);
 }


 /* (non-Javadoc)
  * @see sparrow.elt.core.lang.function.Expression#getValue(java.util.Map)
  */
 public boolean getBooleanValue(Map values) {
   String string = arguments[0].getValue(values);
   if(string!=null && string.trim().equals("")){
     return true;
   }
   return false;
 }


 /* (non-Javadoc)
  * @see sparrow.elt.core.lang.function.AbstractFunction#resolveArguments()
  */
 void resolveArgumentType(List args) {
   arguments = new Expression[1];
   String arg1 = args.get(0).toString();
   arguments[0]=evaluateStringArgument(arg1);
 }

}
