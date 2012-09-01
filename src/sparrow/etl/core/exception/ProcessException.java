package sparrow.etl.core.exception;


public class ProcessException
    extends SparrowException {

  public ProcessException() {
    super();
  }

  public ProcessException(Exception e) {
    super(e);
  }

  public ProcessException(String message, Exception e) {
    super(message, e);
  }

  public ProcessException(String message) {
    super(message);
  }

  public ProcessException(String errorCode, String errorDescription) {
    super(errorCode, errorDescription);
  }

}
