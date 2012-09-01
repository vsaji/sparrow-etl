package sparrow.etl.core.exception;

/**
 * 
 * @author Saji Venugopalan
 *
 */
public class ValidatorException
    extends SparrowException {

  
  public ValidatorException() {
    super();
  }

  public ValidatorException(Exception e) {
    super(e);
  }

  public ValidatorException(String message, Exception e) {
    super(message, e);
  }

  public ValidatorException(String message) {
    super(message);
  }

  public ValidatorException(String errorCode, String errorDescription) {
    super(errorCode, errorDescription);
  }

}
