/**
 * 
 */
package sparrow.etl.core.lang.function;

import java.io.File;
import java.util.List;
import java.util.Map;

import sparrow.etl.core.exception.ParserException;


/**
 * @author vsaji
 *
 */
public class FileExistFunction extends AbstractFunction {

	/**
	 * @param functionName
	 * @param numberOfArg
	 */
	public FileExistFunction(String functionName) {
		super(functionName, 1);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	public boolean getBooleanValue(Map values){
		
		String fileName=arguments[0].getValue(values);
		
		if(fileName!=null){
			return new File(fileName).exists();
		}else{
			return false;
		}
	}
	
	/**
	 * 
	 */
	public String getValue(Map values){
		return String.valueOf(getBooleanValue(values));
	}
	
	/* (non-Javadoc)
	 * @see sparrow.elt.core.lang.function.AbstractFunction#resolveArgumentType(java.util.List)
	 */
	void resolveArgumentType(List args) throws ParserException {
		arguments = new Expression[1];
		String arg1 = args.get(0).toString();
		arguments[0]=evaluateStringArgument(arg1);
	}

}
