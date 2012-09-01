package sparrow.etl.core.util;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import sparrow.etl.core.context.ContextVariables;
import sparrow.etl.core.context.SparrowContext;


/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */
public class GenericTokenResolver
    implements TokenResolver {

  protected SparrowContext context = null;
  private static Map reservedToken;

  private static final GenericTokenResolver instance = new GenericTokenResolver();

  /**
   *
   */
  protected GenericTokenResolver() {
    this.context = null;
    this.reservedToken = new HashMap();
  }

  /**
   *
   * @return GenericTokenResolver
   */
  public static final GenericTokenResolver getInstance() {
    return instance;
  }

  /**
   *
   * @param context SparrowContext
   * @return GenericTokenResolver
   */
  public static final GenericTokenResolver getInstance(SparrowContext context) {
    instance.setContext(context);
    return instance;
  }

  /**
   *
   * @param context SparrowContext
   */
  protected void setContext(SparrowContext context) {
    if (this.context == null) {
      this.context = context;
      context.getAttributes().putAll(reservedToken);
      this.reservedToken = context.getAttributes();
    }
  }

  /**
   * getTokenValue
   *
   * @param token String
   * @return String
   */
  public String getTokenValue(String token) {

    String[] token_format = token.split("[#]");
    String tempToken = token;
    String format = null;

    if (token_format.length > 1) {
      tempToken = token_format[0];
      format = token_format[1];
    }

    if (tempToken.indexOf("_DATE") != -1) {
      format = (format != null) ? format : Constants.DATE_FORMAT_YYYYMMDD;
      if (ContextVariables.CURRENT_DATE.equals(tempToken)) {
        return SparrowUtil.formatDate(new Date(), format);
      }
      else if (ContextVariables.NEXT_DATE.equals(tempToken)) {
        return SparrowUtil.formatDate(getDate(1), format);
      }
      else if (ContextVariables.PREVIOUS_DATE.equals(tempToken)) {
        return SparrowUtil.formatDate(getDate( -1), format);
      }
    }

    if (reservedToken.containsKey(tempToken)) {
      return reservedToken.get(tempToken).toString();
    }

    if (context != null && context.getAttributes().containsKey(tempToken)) {
      return context.getAttribute(tempToken).toString();
    }

    return token;
  }

  /**
   *
   * @param numberOfDays int
   * @return Date
   */
  protected Date getDate(int numberOfDays) {
    Calendar cal = Calendar.getInstance();
    cal.add(Calendar.DATE, numberOfDays);
    return cal.getTime();
  }

  /**
   *
   * @param token String
   * @param value String
   */
  public final void addTokenAndValue(String token, Object value) {
    reservedToken.put(token, value);
  }

  /**
   *
   * @return Map
   */
  public final Map getAllTokens() {
    return reservedToken;
  }

}
