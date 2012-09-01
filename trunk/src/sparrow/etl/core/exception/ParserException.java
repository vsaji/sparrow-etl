/**
 * 
 */
package sparrow.etl.core.exception;

/**
 * @author Saji
 *
 */
public class ParserException extends SparrowException {

	/**
	 * 
	 */
	public ParserException() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param errorCode
	 * @param errorDescription
	 */
	public ParserException(String errorCode, String errorDescription) {
		super(errorCode, errorDescription);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param errorCode
	 * @param errorDescription
	 * @param e
	 */
	public ParserException(String errorCode, String errorDescription,
			Exception e) {
		super(errorCode, errorDescription, e);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param e
	 */
	public ParserException(Exception e) {
		super(e);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param e
	 */
	public ParserException(String message, Exception e) {
		super(message, e);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public ParserException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

}
