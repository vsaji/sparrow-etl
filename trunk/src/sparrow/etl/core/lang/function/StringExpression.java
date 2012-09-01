/**
 *
 */
package sparrow.etl.core.lang.function;

import java.util.Map;

/**
 * @author Saji
 *
 */
public class StringExpression extends AbstractExpression {

	private boolean isToken = false;
	private String value = null;

	/**
	 *
	 * @param token
	 */
	public StringExpression(String value,boolean isToken){
		this.value=value;
		this.isToken = isToken;
	}

  /**
   *
   * @param values Map
   * @return String
   */
  public String getValue(Map values) {
		if(isToken){
			Object val = values.get(value);
			return (val!=null) ? val.toString() : null;
		}else{
			return value;
		}	}


  /**
   *
   * @return boolean
   */
  public boolean isStringExpression(){
    return true;
  }

}
