package sparrow.etl.core.lang.function;

import java.util.Map;
import java.util.List;

public class IndexOfFunction extends AbstractFunction {
  /**
     * @param functionName
     */
    public IndexOfFunction(String functionName) {
      super(functionName,2);
      // TODO Auto-generated constructor stub
    }

    /**
     * @param functionName
     */
    public IndexOfFunction() {
      super("IndexOf",2);
      // TODO Auto-generated constructor stub
    }

    /**
     *
     */
    public int getIntValue(Map values) {
      String string1 = arguments[0].getValue(values);
      String string2 = arguments[1].getValue(values);
      if(string1==null || string2==null){
        return -1;
      }
      return string1.indexOf(string2);
    }

    /**
     *
     */
    public String getValue(Map values) {
      return String.valueOf(getIntValue(values));
    }

    /* (non-Javadoc)
     * @see sparrow.elt.core.lang.function.AbstractFunction#resolveArguments(java.util.List)
     */
    void resolveArgumentType(List args) {
      arguments = new Expression[2];
      String arg1 = args.get(0).toString();
      String arg2 = args.get(1).toString();
      arguments[0]=evaluateStringArgument(arg1);
      arguments[1]=evaluateStringArgument(arg2);
    }

}
