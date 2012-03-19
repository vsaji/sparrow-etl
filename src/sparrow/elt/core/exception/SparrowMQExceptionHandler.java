package sparrow.elt.core.exception;

import javax.jms.JMSException;

public class SparrowMQExceptionHandler
    extends ExceptionHandlerAdapter {

  private String errorCode = null;
  private String errorDesc = null;
  private ErrorConfig config = null;

  public SparrowMQExceptionHandler(ErrorConfig config) {
    super(config);
  }

  /**
   *
   * @param e JMSException
   */
  public void handle(JMSException e) {
    setErrorCode(e.getErrorCode());
    setErrorDesc(e.getMessage());
  }

  /**
   * getHandlerClass
   *
   * @return String
   */
  public String getHandlerClass() {
    return "";
  }

}
