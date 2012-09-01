package sparrow.etl.core.exception;


public class ResourceException
    extends SparrowException {

  public ResourceException(String msg) {
    super(msg);
  }

  public ResourceException(String code, String message) {
    super(code, message);
  }

  public ResourceException(String msg, Exception original) {
    super(msg, original);
  }

  public ResourceException(Exception original) {
    super(original);
  }

}
