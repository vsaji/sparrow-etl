package sparrow.elt.core.lang.function;

import java.util.List;
import java.util.Map;
/**
 * @author Saji
 *
 */
public class MathFunction extends AbstractFunction {

  private static final char ROUND='R';
  private static final char ABS='A';
  private static final char CEIL='C';
  private static final char FLOOR='F';

  private char mathFunction = ROUND;
  /**
   * @param functionName
   */
  public MathFunction(String functionName) {
    super(functionName, 2);
    // TODO Auto-generated constructor stub
  }

  /**
   * @param functionName
   */
  public MathFunction() {
    super("Math", 2);
    // TODO Auto-generated constructor stub
  }


  /**
   *
   */
  public String getValue(Map values) {
    double d = arguments[0].getDoubleValue(values);
    switch(mathFunction){
      case ROUND:
       return String.valueOf(Math.round(d));
     case ABS:
       return String.valueOf(Math.abs(d));
     case CEIL:
       return String.valueOf(Math.ceil(d));
     case FLOOR:
        return String.valueOf(Math.floor(d));
     default:
          return String.valueOf(d);
    }
  }

  /* (non-Javadoc)
   * @see sparrow.elt.core.lang.function.AbstractFunction#resolveArguments(java.util.List)
   */
  void resolveArgumentType(List args) {
    arguments = new Expression[1];
    String arg1 = args.get(0).toString();
    String arg2 = args.get(1).toString().toUpperCase();
    mathFunction = arg2.trim().charAt(0);
    arguments[0] = evaluateDoubleArgument(arg1);
  }

}
