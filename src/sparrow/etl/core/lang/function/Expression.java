/**
 *
 */
package sparrow.etl.core.lang.function;

import java.util.Date;
import java.util.Map;

import sparrow.etl.core.exception.ParserException;


/**
 * @author Saji
 *
 */
public interface Expression {

  public static final int RT_INT=1;
  public static final int RT_LONG=2;
  public static final int RT_DOUBLE=3;
  public static final int RT_STRING=4;
  public static final int RT_BOOLEAN=5;


	public void parse(String expression) throws ParserException;
	public String getValue(Map values);
	public boolean getBooleanValue(Map values);
	public int getIntValue(Map values);
	public long getLongValue(Map values);
	public double getDoubleValue(Map values);
	public Date getDateValue(Map values);
	public boolean isFunction();
  public boolean isBooleanExpression();
  public boolean isStringExpression();

}
