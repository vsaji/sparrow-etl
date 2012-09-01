package sparrow.etl.core.exception;


/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */
public class EnrichDataException
    extends SparrowException {


  public EnrichDataException() {
    super();
  }

  public EnrichDataException(Exception e) {
    super(e);
   }

  public EnrichDataException(String message, Exception e) {
    super(message, e);
  }

  public EnrichDataException(String message) {
    super(message);
  }

  public EnrichDataException(Exception e, boolean isFatal) {
    super(e);
  }

  public EnrichDataException(String message, Exception e, boolean isFatal) {
    super(message, e);
  }

  public EnrichDataException(String message, boolean isFatal) {
    super(message);
  }

}
