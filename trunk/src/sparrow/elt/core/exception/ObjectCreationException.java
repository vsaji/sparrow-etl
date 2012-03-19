package sparrow.elt.core.exception;

public class ObjectCreationException extends SparrowRuntimeException {

	public ObjectCreationException(Exception e) {
		super(e);
	}

	public ObjectCreationException(String message, Exception e) {
		super(message, e);
	}

	public ObjectCreationException(String message) {
		super(message);
	}

	public ObjectCreationException(String code, String message) {
		super(code, message);
	}

}
