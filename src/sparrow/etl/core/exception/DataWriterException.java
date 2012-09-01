package sparrow.etl.core.exception;


public class DataWriterException
    extends SparrowException {

  public DataWriterException() {
    super();
  }

  public DataWriterException(Exception e) {
    super(e);
  }

  public DataWriterException(String message, Exception e) {
    super(message, e);
  }
  
  public DataWriterException(String code, String description) {
	    super(code, description);
	  }

  public DataWriterException(String message) {
    super(message);
  }
}
