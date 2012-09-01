package sparrow.etl.core.exception;

public class DataTransformerException extends SparrowException {

	/**
	 * 
	 */
	public DataTransformerException() {
		super();
	}

	/**
	 * 
	 * @param e
	 *            Exception
	 */
	public DataTransformerException(Exception e) {
		super(e);
	}

	/**
	 * 
	 * @param message
	 *            String
	 * @param e
	 *            Exception
	 */
	public DataTransformerException(String message, Exception e) {
		super(message, e);
	}

	/**
	 * 
	 * @param message
	 *            String
	 */
	public DataTransformerException(String message) {
		super(message);
	}

	/**
	 * 
	 * @param code
	 * @param erroDescription
	 */
	public DataTransformerException(String code,String erroDescription) {
		super(code,erroDescription);
	}
	
}
