package sparrow.elt.core.lang.function;

/**
 * 
 * @author Saji Venugopalan
 *
 */
public class RPadFunction extends LPadFunction {

	public RPadFunction(String functionName) {
		super(functionName);
		// TODO Auto-generated constructor stub
	}

	public RPadFunction() {
		super();
		// TODO Auto-generated constructor stub
	}

	protected String pad(String string,int totalLen,String padWith){
		return string+getPadString(string,totalLen,padWith);
		
	}
	
}
