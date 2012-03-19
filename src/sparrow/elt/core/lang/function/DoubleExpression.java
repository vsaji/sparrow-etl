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
public class DoubleExpression extends AbstractExpression{
  private double value = 0.0;
  private String token = null;

  /**
   *
   * @param token
   */
  public DoubleExpression(String token){
    this.token=token;
  }

  /**
   *
   * @param value
   */
  public DoubleExpression(double value){
    this.value=value;
  }

  /**
   *
   */
  public double getDoubleValue(Map values) {
    if(token!=null){
      return ((Number)values.get(token)).doubleValue();
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
    return String.valueOf(getDoubleValue(values));
  }

}
