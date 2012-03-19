package sparrow.elt.core.lang.function;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author Saji
 *
 */
public class StringMatchExpression extends EvalExpressionHandler {

	int operator = 0;

	public StringMatchExpression() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	public boolean getBooleanValue(Map values) {

		String left = expressions[0].getValue(values);
		String right = expressions[1].getValue(values);
		left = (left == null) ? "" : left;
		right = (right == null) ? "" : right;

		switch (operator) {
			case OPERATOR_EQ:
				return left.equals(right);
			case OPERATOR_NEQ:
				return !left.equals(right);
		}

		return false;
	}

	/**
	 * 
	 */
	void resolveArgumentType(List args) {
		List expr = new ArrayList();

		for (int i = 0; i < args.size(); i++) {
			String element = args.get(i).toString();

			if (isOperator(element)) {
				operator = getOperator(element);
				continue;
			}

			Expression e = evaluateStringArgument(element);
			expr.add(e);
		}
		expressions = (Expression[]) expr.toArray(new Expression[expr.size()]);
	}

}
