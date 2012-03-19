package sparrow.elt.core.exception;


public class FIFOException
    extends SparrowRuntimeException {
  // Constructors
  public FIFOException(String msg) {
    super(msg);
  }

  public FIFOException(String msg, Exception original) {
    super(msg, original);
  }

  public FIFOException(Exception original) {
    super(original);
  }

}
