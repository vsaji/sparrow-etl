/**
 *
 */
package sparrow.etl.core.lang.function;

import java.util.ArrayList;
import java.util.List;

import sparrow.etl.core.exception.ParserException;


/**
 * @author Saji
 *
 */
public abstract class AbstractFunction extends AbstractExpression implements
		Function {

	Expression[] arguments;

	final String functionName;
	final int numberOfArg;

	private static final int STATE_START_ARG = 1;

	private static final int STATE_CONTINUE_ARG = 2;

	private static final int STATE_IN_QUOTE = 3;

	/**
	 *
	 * @param arguments
	 */
	AbstractFunction(String functionName,int numberOfArg) {
		this.functionName = functionName;
		this.numberOfArg = numberOfArg;
	}

	/**
	 *
	 *
	 */
	abstract void resolveArgumentType(List args) throws ParserException;

	/**
	 *
	 *
	 */
	public void parse(String expression) throws ParserException {

		int openBrackets = 0;
		int functionStartPos = expression.indexOf("(");
		int functionEndPos = expression.lastIndexOf(")");
		int lcalState = STATE_START_ARG;

		StringBuffer sb = new StringBuffer();
		List args = new ArrayList();

		for (int i = functionStartPos + 1; i < functionEndPos; i++) {

			char c = expression.charAt(i);
			//System.out.println(i + "==" + c);
			int category = categorise(c);

			switch (lcalState) {

			case STATE_START_ARG: {
				sb = new StringBuffer();
				switch (category) {
				case EOL:
					break;
				case WHITESPACE:
				case ORDINARY:
				case EXPRESSION_SEPARATOR:
					sb.append(c);
					if ((i + 1) == functionEndPos) {
						args.add(sb.toString());
					}
					lcalState = STATE_CONTINUE_ARG;
					break;
				case ARGSEPARATOR:
					args.add(sb.toString());
					// System.out.println(sb.toString());
					lcalState = STATE_START_ARG;
					break;
				case QUOTE:
					lcalState = STATE_IN_QUOTE;

					break;
				case OPENBRACKET:
					lcalState = STATE_CONTINUE_ARG;
					openBrackets++;
					sb.append(c);
					break;
				}
				break;
			}
			case STATE_CONTINUE_ARG: {
				switch (category) {
				case EOL:
					break;
				case WHITESPACE:
				case ORDINARY:
				case EXPRESSION_SEPARATOR:
					sb.append(c);
					if ((i + 1) == functionEndPos) {
						args.add(sb.toString());
					}
					break;
				case ARGSEPARATOR:
					if (openBrackets == 0) {
						args.add(sb.toString());
						lcalState = STATE_START_ARG;
					} else {
						sb.append(c);
					}
					break;
				case OPENBRACKET:
					openBrackets++;
					sb.append(c);
					break;
				case CLOSEBRACKET:
					openBrackets--;
					sb.append(c);
					if ((i + 1) == functionEndPos) {
						args.add(sb.toString());
					}
					break;
				}
				break;
			}
			case STATE_IN_QUOTE: {
				switch (category) {
				case QUOTE:
					if ((i + 1) == functionEndPos) {
						args.add(sb.toString());
					}
					lcalState = STATE_CONTINUE_ARG;
					break;
				default:
					sb.append(c);
					break;
				}
			}
				break;
			}
		}
		//System.out.println(args);
		if(numberOfArg!=-1 && args.size()!=numberOfArg){
			throw new ParserException("ARG_LEN_MISMATCH","Arguments Length mismatch for Function ["+getFunctionName()+"].Expected ["+numberOfArg+"].Found["+args.size()+"].Function ["+expression+"]");
		}
		resolveArgumentType(args);

	}

	/**
	 *
	 */
	public String getFunctionName() {
		return functionName;
	}

	/**
	 *
	 */
	public boolean isFunction() {
		return true;
	}


}
