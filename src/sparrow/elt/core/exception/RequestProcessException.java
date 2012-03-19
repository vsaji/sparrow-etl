package sparrow.elt.core.exception;


public class RequestProcessException
    extends SparrowException {

	/**
	 * 
	 *
	 */
  public RequestProcessException() {
    super();
  }

  /**
   * 
   * @param p0
   */
  public RequestProcessException(String p0) {
    super(p0);
  }

  /**
   * 
   * @param p0
   * @param p1
   */
  public RequestProcessException(String p0, Exception p1) {
    super(p0, p1);
  }
  
	/**
	 * 
	 * @param p0
	 */
  public RequestProcessException(Exception p0) {
    super(p0);
  }

}
