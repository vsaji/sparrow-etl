package sparrow.elt.core.exception;


/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */
public class SemaphoreException
    extends SparrowException {


  /**
   *
   * @param msg String
   */
  public SemaphoreException(String msg) {
    super(msg);
  }

  /**
   *
   * @param code String
   * @param message String
   */
  public SemaphoreException(String code, String message) {
    super(code, message);
  }

  /**
   *
   * @param msg String
   * @param original Exception
   */
  public SemaphoreException(String msg, Exception original) {
    super(msg, original);
  }

  /**
   *
   * @param original Exception
   */
  public SemaphoreException(Exception original) {
    super(original);
  }
}
