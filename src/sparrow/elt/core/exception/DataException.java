package sparrow.elt.core.exception;


public class DataException
    extends SparrowException {

  public DataException() {
    super();
  }

  public DataException(Exception e) {
    super(e);
  }

  public DataException(String message, Exception e) {
    super(message, e);
  }

  public DataException(String message) {
    super(message);
  }

  public DataException(String errorCode, String errorDescription) {
    super(errorCode, errorDescription);
  }

}
