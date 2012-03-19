package sparrow.elt.core.lang.function;

import java.util.List;
import java.util.Map;

import sparrow.elt.core.exception.ParserException;

public class TernaryFunction
    extends AbstractFunction {

  public TernaryFunction(String functionName) {
    super(functionName, 3);
    // TODO Auto-generated constructor stub
  }

  /**
   *
   */
  public String getValue(Map values) {
    boolean b = arguments[0].getBooleanValue(values);
    return (b) ? arguments[1].getValue(values) : arguments[2].getValue(values);
  }

  /**
   *
   */
  void resolveArgumentType(List args) throws ParserException {
    arguments = new Expression[3];

    String arg1 = args.get(0).toString();
    String arg2 = args.get(1).toString();
    String arg3 = args.get(2).toString();

    //	arguments[0]=evaluateStringArgument(arg1);
    arguments[0] = ExpressionResolverFactory.parse(new TernaryExpression(),
        arg1);
    arguments[1] = evaluateStringArgument(arg2);
    arguments[2] = evaluateStringArgument(arg3);
  }

}
