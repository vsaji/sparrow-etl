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
public class StringTernaryFunction extends AbstractFunction {

  public StringTernaryFunction(String functionName) {
     super(functionName,1);
   }


   /**
    *
    * @param values Map
    * @return String
    */
   public String getValue(Map values){
     boolean b = arguments[0].getBooleanValue(values);
     return (b) ? arguments[1].getValue(values) : arguments[2].getValue(values);
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
     arguments = new Expression[3];
     String arg1 = "("+args.get(0).toString()+")";
     String arg2 = args.get(1).toString();
     String arg3 = args.get(2).toString();

     arguments[0] = ExpressionResolverFactory.getEvalExceptionHandlerInstance(arg1,RT_STRING);
     arguments[1] = evaluateStringArgument(arg2);
     arguments[2] = evaluateStringArgument(arg3);
   }

}
