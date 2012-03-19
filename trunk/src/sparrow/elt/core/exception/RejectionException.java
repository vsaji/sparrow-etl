package sparrow.elt.core.exception;

public class RejectionException
    extends SparrowException {

  public RejectionException() {
	  super();
  }

  public RejectionException(String errorCode, String errorDescription) {
	  super(errorCode,errorDescription);
  }

  public RejectionException(Exception e) {
    super(e);
  }

  public RejectionException(String message, Exception e) {
    super(message, e);
  }

  public RejectionException(String message) {
    super(message);
  }

}
