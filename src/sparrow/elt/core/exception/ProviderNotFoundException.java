package sparrow.elt.core.exception;

import sparrow.elt.core.log.SparrowLogger;
import sparrow.elt.core.log.SparrowrLoggerFactory;

public class ProviderNotFoundException extends SparrowRuntimeException {

	public ProviderNotFoundException(Exception e) {
		super(e);
	}

	public ProviderNotFoundException(String message, Exception e) {
		super(message, e);
	}

	public ProviderNotFoundException(String message) {
		super(message);
	}

	public ProviderNotFoundException(String code, String message) {
		super(code, message);
	}

}
