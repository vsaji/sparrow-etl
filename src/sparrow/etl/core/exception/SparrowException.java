package sparrow.etl.core.exception;

import sparrow.etl.core.log.SparrowLogger;
import sparrow.etl.core.log.SparrowrLoggerFactory;

/**
 * 
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author Saji Venugopalan
 * @version 1.0
 */
public abstract class SparrowException extends Exception {

	private static final SparrowLogger logger = SparrowrLoggerFactory
			.getCurrentInstance(SparrowException.class);

	protected Throwable throwable = null;

	private String errorCode = null;

	private String errorDescription = null;

	/**
	 * 
	 * @return
	 */
	public String getErrorCode() {
		return errorCode;
	}

	/**
	 * 
	 * @param errorDescription
	 */
	public void setErrorDescription(String errorDescription) {
		this.errorDescription = errorDescription;
	}

	/**
	 * 
	 * @param errorCode
	 */
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	/**
	 * 
	 * @return
	 */
	public String getErrorDescription() {
		return errorDescription;

	}
	/**
	 * 
	 *
	 */
	protected SparrowException() {
		this.throwable = this;
		logger.error("["+this.getClass().getName()+"] occured [Empty Constructor called]", this);
		checkExceptionAgainstFatal();
	}

	/**
	 * 
	 * @param errorCode
	 * @param errorDescription
	 */
	public SparrowException(String errorCode, String errorDescription) {
		setErrorCode(errorCode);
		setErrorDescription(errorDescription);
		this.throwable = this;
		logger.error("Spear Exception["+this.getClass().getName()+"] occured [" + errorCode + "-"
				+ errorDescription + "]", this);
		checkExceptionAgainstFatal();
	}

	/**
	 * 
	 * @param errorCode
	 * @param errorDescription
	 */
	public SparrowException(String errorCode, String errorDescription,Exception e) {
		setErrorCode(errorCode);
		setErrorDescription(errorDescription);
		this.throwable = this;
		logger.error("Root Exception["+e.getClass().getName()+"]-Spear Exception["+this.getClass().getName()+"] occured [" + errorCode + "-"
				+ errorDescription + "]", this);
		checkExceptionAgainstFatal();
	}
	
	/**
	 * 
	 * @param e
	 */
	protected SparrowException(Exception e) {
		super(e);
		setErrorCode(e.getClass().getName());
		setErrorDescription(e.getMessage());
		this.throwable = e;
		logger.error("Root Exception["+e.getClass().getName()+"]-Spear Exception["+this.getClass().getName()+"] occured [" + errorCode + "-"
				+ errorDescription + "]", this.throwable);
		checkExceptionAgainstFatal();
	}
	/**
	 * 
	 * @param message
	 * @param e
	 */
	protected SparrowException(String message, Exception e) {
		super(message, e);
		this.throwable = (ExceptionHandler.isRegistered(e)) ? e : this;
		setErrorCode(e.getClass().getName());
		setErrorDescription(e.getMessage());
		logger.error("Root Exception["+e.getClass().getName()+"]-Spear Exception["+this.getClass().getName()+"] occured [" + errorCode + "-"
				+ errorDescription + "] Message["+message+"]", this.throwable);
		checkExceptionAgainstFatal();
	}

	/**
	 * 
	 * @param message
	 */
	protected SparrowException(String message) {
		super(message);
		setErrorCode(this.getClass().getName());
		setErrorDescription(message);
		this.throwable = this;
		logger.error("Spear Exception["+this.getClass().getName()+"] Message["+message+"]", this.throwable);		
		checkExceptionAgainstFatal();
	}

	/**
	 * 
	 * @return
	 */
	public final ExceptionHandler getExceptionHandler() {
		return ExceptionHandler.getHandler(throwable);
	}

	/**
	 * 
	 *
	 */
	protected final void checkExceptionAgainstFatal() {
		ExceptionHandler handler = getExceptionHandler();
		if (handler.isFatal()) {
			handler.setFatalOnly();
		}
	}
	/**
	 * 
	 * @param e
	 */
	protected final void checkExceptionAgainstFatal(Exception e) {
		ExceptionHandler handler = ExceptionHandler.getHandler(e);
		if (handler.isFatal()) {
			handler.setFatalOnly();
		}
	}
	/**
	 * 
	 * @param fatal
	 */
	protected final void checkExceptionAgainstFatal(boolean fatal) {
		if (fatal) {
			ExceptionHandler handler = getExceptionHandler();
			handler.setFatalOnly();
		}
	}
	/**
	 * 
	 */
	public String getMessage() {
		return "[" + errorCode + "][" + errorDescription + "]";
	}

	
	/**
	 * 
	 * @param code
	 * @param description
	 */
	public static final void throwException(final String code,final String description){
		new SparrowException.SpearExceptionExtn(code,description);
	}

	/**
	 * 
	 * @author Saji
	 *
	 */
	private static class SpearExceptionExtn extends SparrowException{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public SpearExceptionExtn(final String code,final String description){
			super(code,description);
		}
	}
	
}
