package sparrow.elt.core.lang.function;

import java.util.Date;
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
public class DateExpression
    extends AbstractExpression {
  private Date value = null;
  private String token = null;

  /**
   *
   * @param token
   */
  public DateExpression(String token) {
    this.token = token;
  }

  /**
   *
   * @param value
   */
  public DateExpression(Date value) {
    this.value = value;
  }

  /**
   *
   */
  public Date getDateValue(Map values) {
    if (token != null) {
      return (Date) values.get(token);
    }
    else {
      return value;
    }
  }

  /**
   *
   * @param values Map
   * @return String
   */
  public String getValue(Map values) {
    return String.valueOf(getDateValue(values));
  }

}
