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
public class NotifierException extends SparrowException{
  public NotifierException() {
    super();
  }

  public NotifierException(String errorCode, String errorDescription) {
	  super(errorCode,errorDescription);
  }

  public NotifierException(String errorCode, String errorDescription,Exception ex) {
	  super(errorCode,errorDescription,ex);
  }


  public NotifierException(Exception e) {
    super(e);
  }

  public NotifierException(String message, Exception e) {
    super(message, e);
  }

  public NotifierException(String message) {
    super(message);
  }

}
