package sparrow.etl.core.exception;

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public class SecurityException
    extends SparrowException {

  public SecurityException() {
    super();
  }

  public SecurityException(String errorCode, String errorDescription) {
	  super(errorCode, errorDescription);
  }

  public SecurityException(String errorCode, String errorDescription,
                           Exception ex) {
	  super(errorCode, errorDescription,ex);
  }

  public SecurityException(Exception e) {
    super(e);
  }

  public SecurityException(String message, Exception e) {
    super(message, e);
  }

  public SecurityException(String message) {
    super(message);
  }

}
