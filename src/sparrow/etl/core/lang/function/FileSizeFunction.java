/**
 * 
 */
package sparrow.etl.core.lang.function;

import java.io.File;
import java.util.List;
import java.util.Map;

import sparrow.etl.core.exception.ParserException;


/**
 * @author syadav15
 *
 */
public class FileSizeFunction extends AbstractFunction {
	
	/**
	 * 
	 * @param functionName
	 */
	public FileSizeFunction(String functionName){
		super(functionName,2);
	}

	/**
	 * 
	 *
	 */
	public FileSizeFunction() {
		super("FileSize",2);
	}
	
	
	/**
	 * 
	 */
	public long getLongValue(Map values) {
		String string = arguments[0].getValue(values);
		
		String sizeType = arguments[1].getValue(values);
		
		long fsize=0;
		
		if(string==null){
			return -1;	
		} else {
			fsize =(new File(string)).length();
		}

		if ("KB".equalsIgnoreCase(sizeType.trim())){
			fsize = fsize / 1024;
		} else if ("MB".equalsIgnoreCase(sizeType.trim())){
			fsize = fsize / (1024*1024);
		} 
		
		return fsize;
	}
	
	/**
	 * 
	 */
	public String getValue(Map values) {
		return String.valueOf(getLongValue(values));
	}
	
	/* (non-Javadoc)
	 * @see sparrow.elt.core.lang.function.AbstractFunction#resolveArgumentType(java.util.List)
	 */
	void resolveArgumentType(List args) throws ParserException {
		arguments = new Expression[2];
		String arg1 = args.get(0).toString();
		String arg2 = args.get(1).toString();
		arguments[0]=evaluateStringArgument(arg1);
		arguments[1]=evaluateStringArgument(arg2);
	}


}
