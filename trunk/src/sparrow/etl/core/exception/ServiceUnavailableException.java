package sparrow.etl.core.exception;


public class ServiceUnavailableException extends SparrowException {

	public ServiceUnavailableException() {
		super();
	}

	public ServiceUnavailableException(String message, Exception e) {
		super(message, e);
	}

	public ServiceUnavailableException(String code, String des) {
		super(code, des);
	}

	public ServiceUnavailableException(String message) {
		super(message);
	}

}
