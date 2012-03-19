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
public class BooleanExpression extends AbstractExpression{
  private boolean value = false;
  private String token = null;

  /**
   *
   * @param token
   */
  public BooleanExpression(String token){
    this.token=token;
  }

  /**
   *
   * @param value
   */
  public BooleanExpression(boolean value){
    this.value=value;
  }

  /**
   *
   */
  public boolean getBooleanValue(Map values) {
    if(token!=null){
      return ((Boolean)values.get(token)).booleanValue();
    }else{
      return value;
    }
  }


}
