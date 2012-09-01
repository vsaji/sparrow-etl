package sparrow.etl.core.exception;


public class MethodInvocationException
    extends Exception {

  public MethodInvocationException() {
    super();
  }

  public MethodInvocationException(Exception e) {
    super(e);
  }

  public MethodInvocationException(String message, Exception e) {
    super(message, e);
  }
  
  public MethodInvocationException(String message) {
    super(message);
  }

}
