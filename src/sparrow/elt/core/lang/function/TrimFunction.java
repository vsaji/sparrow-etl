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
public class TrimFunction extends AbstractFunction {

	/**
	 * @param functionName
	 */
	public TrimFunction(String funcName) {
		super(funcName,1);
		// TODO Auto-generated constructor stub
	}

	
	/* (non-Javadoc)
	 * @see sparrow.elt.core.lang.function.Expression#getValue(java.util.Map)
	 */
	public String getValue(Map values) {
		String string = arguments[0].getValue(values);
		if(string==null){
			return null;
		}
		return string.trim();
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
