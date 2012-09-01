package sparrow.etl.core.lang.function;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import sparrow.etl.core.exception.ParserException;


/**
 *
 * @author Saji
 *
 */
public class TernaryExpression extends EvalExpressionHandler {

	private static final int STATE_BOOLEAN = 34;

	private static final int STATE_STRING = 35;

	public TernaryExpression() {
		super();
	}

	/**
	 *
	 */
	public boolean getBooleanValue(Map values) {

		boolean start = true;
		boolean result = false;

		int state = STATE_BOOLEAN;

    if( expressions.length==1 && operators.size()==0){
      return expressions[0].getBooleanValue(values);
    }

		for (int i = 0, j = 0; i < expressions.length && j < operators.size();) {

			boolean bLft = false;
			String sLft = null;

			if (start) {
				Expression left = expressions[i++];

				if (left.isStringExpression()) {
					state = STATE_STRING;
					sLft = left.getValue(values);
					sLft = (sLft == null) ? "" : sLft.trim();
				} else {
					bLft = left.getBooleanValue(values);
					state = STATE_BOOLEAN;
				}
				start = false;
			} else {
				bLft = result;
			}

			Expression right;
			int oprtor;
			String sRgt;
			boolean bRgt;

			switch (state) {
			case STATE_BOOLEAN:
				right = expressions[i++];
				oprtor = ((Integer) operators.get(j++)).intValue();
				bRgt = right.getBooleanValue(values);
				switch (oprtor) {
				case OPERATOR_AND:
					result = (bLft && bRgt);
					break;
				case OPERATOR_OR:
					result = (bLft || bRgt);
					break;
				}
				break;
			case STATE_STRING:
				right = expressions[i++];
				oprtor = ((Integer) operators.get(j++)).intValue();
				sRgt = right.getValue(values);
        sRgt = (sRgt == null) ? "" : sRgt.trim();
				switch (oprtor) {
				case OPERATOR_EQ:
					result = (sLft.equals(sRgt));
					break;
				case OPERATOR_NEQ:
					result = !(sLft.equals(sRgt));
					break;
        case OPERATOR_GT:
          sRgt=(sRgt.equals("")) ? "0":sRgt;
          sLft=(sLft.equals("")) ? "0":sLft;
          result = Long.parseLong(sLft) > Long.parseLong(sRgt);
          break;
        case OPERATOR_LT:
          sRgt=(sRgt.equals("")) ? "0":sRgt;
          sLft=(sLft.equals("")) ? "0":sLft;
          result = Long.parseLong(sLft) < Long.parseLong(sRgt);
          break;
        case OPERATOR_GT_EQ:
          sRgt=(sRgt.equals("")) ? "0":sRgt;
          sLft=(sLft.equals("")) ? "0":sLft;
          result = Long.parseLong(sLft) >= Long.parseLong(sRgt);
          break;
        case OPERATOR_LT_EQ:
          sRgt=(sRgt.equals("")) ? "0":sRgt;
          sLft=(sLft.equals("")) ? "0":sLft;
          result = Long.parseLong(sLft) <= Long.parseLong(sRgt);
          break;

				}
				break;
			}
		}

		return result;
	}

  /**
   *
   * @return boolean
   */
  public boolean isStringExpression(){
    return false;
  }
	/**
	 * @throws ParserException
	 *
	 */
	void resolveArgumentType(List args) throws ParserException {
		List expr = new ArrayList();

		for (int i = 0; i < args.size(); i++) {
			String element = args.get(i).toString();

			if (isOperator(element)) {
				operators.add(new Integer(getOperator(element)));
				continue;
			}

			Expression e = null;
			if (isEval(element)) {
				e = ExpressionResolverFactory.parse(
						new StringMatchExpression(), element);
			} else {
				e = evaluateStringArgument(element);
			}
			expr.add(e);
		}
		expressions = (Expression[]) expr.toArray(new Expression[expr.size()]);
	}

}
