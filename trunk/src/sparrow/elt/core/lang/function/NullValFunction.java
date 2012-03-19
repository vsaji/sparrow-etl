package sparrow.elt.core.lang.function;

import java.util.List;
import java.util.Map;

/**
 * 
 * @author Saji Venugopalan
 *
 */
public class NullValFunction extends AbstractFunction {

	public NullValFunction(String functionName) {
		super(functionName,2);
		// TODO Auto-generated constructor stub
	}

	
	
	public String getValue(Map values){
		String string = arguments[0].getValue(values);
		return (string==null) ? arguments[1].getValue(values) : string;  
	}
	/**
	 * 
	 */
	void resolveArgumentType(List args) {
		arguments = new Expression[2];
		
		String arg1 = args.get(0).toString();
		String arg2 = args.get(1).toString();
		
		arguments[0]=evaluateStringArgument(arg1);
		arguments[1]=evaluateStringArgument(arg2);
	}

}
