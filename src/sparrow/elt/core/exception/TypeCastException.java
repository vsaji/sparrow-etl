package sparrow.elt.core.exception;

public class TypeCastException
    extends SparrowException {

  public TypeCastException() {
    super();
  }

  public TypeCastException(String errCod, String errDis) {
    super(errCod, errDis);
  }

  public TypeCastException(Exception e) {
    super(e);
  }

  public TypeCastException(String message, Exception e) {
    super(message, e);
  }

  public TypeCastException(String message) {
    super(message);
  }

  public TypeCastException(Exception e, boolean isFatal) {
    super(e);
  }

  public TypeCastException(String message, Exception e, boolean isFatal) {
    super(message, e);
  }

  public TypeCastException(String message, boolean isFatal) {
    super(message);
  }

}
