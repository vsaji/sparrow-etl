/**
 *
 */
package sparrow.elt.core.lang.function;

import java.util.List;
import java.util.Map;

/**
 * @author Saji
 *
 */
public class LengthFunction extends AbstractFunction {

	/**
	 * @param functionName
	 */
	public LengthFunction(String functionName) {
		super(functionName,1);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param functionName
	 */
	public LengthFunction() {
		super("Length",1);
		// TODO Auto-generated constructor stub
	}

	/**
	 *
	 */
	public int getIntValue(Map values) {
		String string = arguments[0].getValue(values);
		if(string==null){
			return -1;
		}
		return string.length();
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
		arguments = new Expression[1];
		String arg1 = args.get(0).toString();
		arguments[0]=evaluateStringArgument(arg1);
	}

}
