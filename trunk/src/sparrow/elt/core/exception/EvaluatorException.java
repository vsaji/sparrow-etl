package sparrow.elt.core.exception;

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public class EvaluatorException extends SparrowException{
	
  public EvaluatorException() {
    super();
  }

  public EvaluatorException(String errorCode, String errorDescription) {
	  super(errorCode,errorDescription);
  }

  public EvaluatorException(String errorCode, String errorDescription,Exception ex) {
	  super(errorCode,errorDescription,ex);
  }


  public EvaluatorException(Exception e) {
    super(e);
  }

  public EvaluatorException(String message, Exception e) {
    super(message, e);
  }

  public EvaluatorException(String message) {
    super(message);
  }

}
