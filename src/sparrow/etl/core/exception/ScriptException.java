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
public class ScriptException extends SparrowException{
  public ScriptException() {
    super();
  }

  public ScriptException(String errorCode, String errorDescription) {
	  super(errorCode,errorDescription);
  }

  public ScriptException(String errorCode, String errorDescription,Exception ex) {
	  super(errorCode,errorDescription,ex);
  }


  public ScriptException(Exception e) {
    super(e);
  }

  public ScriptException(String message, Exception e) {
    super(message, e);
  }

  public ScriptException(String message) {
    super(message);
  }

}
