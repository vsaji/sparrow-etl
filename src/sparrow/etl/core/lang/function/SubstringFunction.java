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
public class SubstringFunction extends AbstractFunction implements Function {

	/**
	 * 
	 * @param expression
	 */
	public SubstringFunction(){
		super("Substring",3);
	}
	
	
	public SubstringFunction(String functionName){
		super(functionName,3);
	}
	
	
	/* (non-Javadoc)
	 * @see sparrow.elt.core.lang.function.Expression#getValue(java.util.Map)
	 */
	public String getValue(Map values) {
		String string = arguments[0].getValue(values);
		int startPos = arguments[1].getIntValue(values);
		int endPos = arguments[2].getIntValue(values);
		
		if(string==null||startPos <0||endPos<1){
			return null;
		}
		
		return string.substring(startPos,endPos);
	}

/*
 *  (non-Javadoc)
 * @see sparrow.elt.core.lang.function.AbstractFunction#resolveArguments()
 */
	void resolveArgumentType(List args) {
		arguments = new Expression[3];
		String arg1 = args.get(0).toString();
		String arg2 = args.get(1).toString();
		String arg3 = args.get(2).toString();
	
		arguments[0]=evaluateStringArgument(arg1);
		arguments[1]=evaluateIntArgument(arg2);
		arguments[2]=evaluateIntArgument(arg3);
	}
}
