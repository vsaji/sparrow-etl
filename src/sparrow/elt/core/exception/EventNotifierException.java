package sparrow.elt.core.exception;


public class EventNotifierException
    extends SparrowException {


  public EventNotifierException(String msg) {
    super(msg);
  }

  public EventNotifierException(String code, String message) {
    super(code, message);
  }

  public EventNotifierException(String msg, Exception original) {
    super(msg, original);
  }

  public EventNotifierException(Exception original) {
    super(original);
  }
}
