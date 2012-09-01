/**
 * 
 */
package sparrow.etl.core.lang.function;

import java.util.List;
import java.util.Map;

/**
 * @author Saji
 *
 */
public class LCaseFunction extends AbstractFunction {

	/**
	 * @param expression
	 */
	public LCaseFunction() {
		super("LCase",1);
	}

	/**
	 * 
	 * @param functionName
	 */
	public LCaseFunction(String functionName) {
		super(functionName,1);
	}	
	
	/* (non-Javadoc)
	 * @see sparrow.elt.core.lang.function.Expression#getValue(java.util.Map)
	 */
	public String getValue(Map values) {
		String string = arguments[0].getValue(values);
		if(string==null){
			return null;
		}
		return string.toLowerCase();
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
