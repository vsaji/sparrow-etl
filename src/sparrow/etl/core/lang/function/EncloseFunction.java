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
public class EncloseFunction extends AbstractFunction {

	String encloseToken = null;
	boolean isDoubleQuote,isSingleQuote = false;

	
	/**
	 * @param functionName
	 */
	public EncloseFunction(String functionName) {
		super(functionName,2);
	}

	/**
	 * @param functionName
	 */
	public EncloseFunction() {
		super("func_enclose",2);
	}

	/**
	 *
	 */
	public String getValue(Map values) {
		String value = arguments[0].getValue(values);
		if(isDoubleQuote && value!=null){
			return "\""+value+"\"";
		}
		if(isSingleQuote && value!=null){
			return "\'"+value+"\'";
		}
		return (value==null) ? null : encloseToken+value+encloseToken;
	}

	/* (non-Javadoc)
	 * @see sparrow.elt.core.lang.function.AbstractFunction#resolveArguments(java.util.List)
	 */
	void resolveArgumentType(List args) {
		arguments = new Expression[1];
		String arg1 = args.get(0).toString();
		this.encloseToken = args.get(1).toString().toUpperCase();
		this.isDoubleQuote = DOUBLE_QUOTE.equals(encloseToken);
		this.isSingleQuote = SINGLE_QUOTE.equals(encloseToken);
		arguments[0]=evaluateStringArgument(arg1);
	}

}
