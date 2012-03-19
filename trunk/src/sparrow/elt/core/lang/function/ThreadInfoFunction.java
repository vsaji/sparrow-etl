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
public class ThreadInfoFunction extends AbstractFunction {

	/**
	 * @param functionName
	 */
	public ThreadInfoFunction(String funcName) {
		super(funcName,0);
	}

	
	/* (non-Javadoc)
	 * @see sparrow.elt.core.lang.function.Expression#getValue(java.util.Map)
	 */
	public String getValue(Map values) {
		return "["+Thread.currentThread().getName()+"]";
	}
	
	
	/* (non-Javadoc)
	 * @see sparrow.elt.core.lang.function.AbstractFunction#resolveArguments()
	 */
	void resolveArgumentType(List args) {
	}

}
