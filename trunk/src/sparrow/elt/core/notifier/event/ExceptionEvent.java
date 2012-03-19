package sparrow.elt.core.notifier.event;

import java.io.StringWriter;
import java.io.PrintWriter;

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public class ExceptionEvent
    extends Event {

  private String exceptionClass = null;
  private String exceptionMessage = null;
  private String exceptionStackTrace = null;

  /**
   *
   * @return String
   */
  public String getExceptionStackTrace() {
    return exceptionStackTrace;
  }

  /**
   *
   * @return String
   */
  public String getExceptionMessage() {
    return exceptionMessage;
  }

  /**
   *
   * @param throwable Throwable
   */
  public void setExceptionClass(String exceptionMessage) {
    this.exceptionMessage = exceptionMessage;
  }

  /**
   *
   * @param throwable Throwable
   */
  public void setExceptionStackTrace(String exceptionStackTrace) {
    this.exceptionStackTrace = exceptionStackTrace;
  }

  /**
   *
   * @param throwable Throwable
   */
  public void setExceptionMessage(String exceptionMessage) {
    this.exceptionMessage = exceptionMessage;
  }

  /**
   *
   * @return String
   */
  public String getExceptionClass() {
    return exceptionClass;
  }

  /**
   *
   */
  public ExceptionEvent(Throwable throwable) {
    super();
    this.exceptionMessage = throwable.getMessage();
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    throwable.printStackTrace(pw);
    this.exceptionStackTrace = sw.toString();
    this.exceptionMessage = throwable.getClass().getName();
    super.setType(EXCEPTION);
    super.setMessage(exceptionStackTrace);
  }

}
