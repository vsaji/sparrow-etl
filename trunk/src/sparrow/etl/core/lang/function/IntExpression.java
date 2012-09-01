/**
 *
 */
package sparrow.etl.core.lang.function;

import java.util.Map;

/**
 * @author Saji
 *
 */
public class IntExpression extends AbstractExpression {

	private int value = 0;
	private String token = null;

	/**
	 *
	 * @param token
	 */
	public IntExpression(String token){
		this.token=token;
	}

	/**
	 *
	 * @param value
	 */
	public IntExpression(int value){
		this.value=value;
	}

	/**
	 *
	 */
	public int getIntValue(Map values) {
		if(token!=null){
		    return ((Number)values.get(token)).intValue();
		}else{
			return value;
		}
	}

  /**
   *
   * @param values Map
   * @return long
   */
  public long getLongValue(Map values) {
  if(token!=null){
      return ((Number)values.get(token)).longValue();
  }else{
    return value;
  }
}


  /**
  *
  * @param values Map
  * @return String
  */
 public String getValue(Map values) {
   return String.valueOf(getIntValue(values));
 }


}
