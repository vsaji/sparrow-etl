/**
 * 
 */
package sparrow.elt.core.lang.function;

import java.util.Map;

/**
 * @author Saji
 *
 */
public class UCaseFunction extends LCaseFunction {

	/**
	 * 
	 */
	public UCaseFunction(String functionName) {
		super(functionName);
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
		return string.toUpperCase();
	}
	
	
}
