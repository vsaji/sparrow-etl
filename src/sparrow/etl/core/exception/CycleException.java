package sparrow.etl.core.exception;

/**
 * 
 * @author Saji Venugopalan
 *
 */
public class CycleException
    extends SparrowException {


  public CycleException(String msg) {
    super(msg);
  }

  public CycleException(String code, String message) {
    super(code, message);
  }

  public CycleException(String msg, Exception original) {
    super(msg, original);
  }

  public CycleException(Exception original) {
    super(original);
  }

}
