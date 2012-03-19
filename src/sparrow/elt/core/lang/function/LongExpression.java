package sparrow.elt.core.lang.function;

import java.util.Map;

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public class LongExpression extends AbstractExpression{
  private long value = 0l;
  private String token = null;

  /**
   *
   * @param token
   */
  public LongExpression(String token){
    this.token=token;
  }

  /**
   *
   * @param value
   */
  public LongExpression(long value){
    this.value=value;
  }

  /**
   *
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
   return String.valueOf(getLongValue(values));
 }

}
