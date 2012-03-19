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
public class LPadFunction extends AbstractFunction {

	/**
	 * @param functionName
	 */
	public LPadFunction(String functionName) {
		super(functionName, 3);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	public LPadFunction() {
		super("LPad", 3);
		// TODO Auto-generated constructor stub
	}

	public String getValue(Map values) {
		String string = arguments[0].getValue(values);
		int totalLen = arguments[1].getIntValue(values);
		String padWith = arguments[2].getValue(values);

		if (string == null || totalLen < 0 || padWith == null) {
			return null;
		}
		return pad(string, totalLen, padWith);
	}

	/**
	 * 
	 * @param string
	 * @param totalLen
	 * @param padWith
	 * @return
	 */
	protected String getPadString(String string, int totalLen, String padWith) {

		int len = string.length();
		if (len>=totalLen){
			return "";
		}
		int cover = totalLen - len;
		String pad = "";
		for (int i = 0; i < cover; i++) {
			pad += padWith;
		}
		return pad;
	}

	/**
	 * 
	 * @param string
	 * @param totalLen
	 * @param padWith
	 * @return
	 */
	protected String pad(String string, int totalLen, String padWith) {
		return getPadString(string, totalLen, padWith) + string;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sparrow.elt.core.lang.function.AbstractFunction#resolveArgumentType(java.util.List)
	 */
	void resolveArgumentType(List args) {
		arguments = new Expression[3];
		String arg1 = args.get(0).toString();
		String arg2 = args.get(1).toString();
		String arg3 = args.get(2).toString();

		arguments[0]=evaluateStringArgument(arg1);
		arguments[1]=evaluateIntArgument(arg2);
		arguments[2]=evaluateStringArgument(arg3);
	}

}
