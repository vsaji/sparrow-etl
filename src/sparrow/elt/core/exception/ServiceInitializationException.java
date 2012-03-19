package sparrow.elt.core.exception;

public class ServiceInitializationException
    extends InitializationException {

  public ServiceInitializationException(Exception e) {
    super(e);
  }

  public ServiceInitializationException(String message, Exception e) {
    super(message, e);
  }

  public ServiceInitializationException(String message) {
    super(message);
  }

}
