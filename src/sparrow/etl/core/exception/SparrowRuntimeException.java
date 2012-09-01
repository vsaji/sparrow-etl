package sparrow.etl.core.exception;

import sparrow.etl.core.log.SparrowLogger;
import sparrow.etl.core.log.SparrowrLoggerFactory;

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */
public class SparrowRuntimeException
    extends RuntimeException {

  private static final SparrowLogger logger = SparrowrLoggerFactory.
      getCurrentInstance(
      SparrowRuntimeException.class);

  protected Throwable throwable = null;

  private String errorCode = null;
  private String errorDescription = null;

  private static final int RETRY_LIMIT = 3;

  protected String getErrorCode() {
    return errorCode;
  }

  protected void setErrorDescription(String errorDescription) {
    this.errorDescription = errorDescription;
  }

  protected void setErrorCode(String errorCode) {
    this.errorCode = errorCode;
  }

  protected String getErrorDescription() {
    return errorDescription;
  }

  public SparrowRuntimeException() {
    this.throwable = this;
	logger.error("["+this.getClass().getName()+"] occured [Empty Constructor called]", this);
    checkExceptionAgainstFatal();
  }

  public SparrowRuntimeException(String errorCode, String errorDescription) {
    setErrorCode(errorCode);
    setErrorDescription(errorDescription);
    this.throwable = this;
	logger.error("Spear Exception["+this.getClass().getName()+"] occured [" + errorCode + "-"
			+ errorDescription + "]", this);
    checkExceptionAgainstFatal();
  }

  public SparrowRuntimeException(Exception e) {
    super(e);
    setErrorCode(e.getClass().getName());
    setErrorDescription(e.getMessage());
    this.throwable = this;
	logger.error("Root Exception["+e.getClass().getName()+"]-Spear Exception["+this.getClass().getName()+"] occured [" + errorCode + "-"
			+ errorDescription + "]", this.throwable);
    checkExceptionAgainstFatal();
  }

  public SparrowRuntimeException(String message, Exception e) {
    super(message, e);
    setErrorCode(message);
    setErrorDescription(e.getMessage());
    this.throwable = this;
	logger.error("Root Exception["+e.getClass().getName()+"]-Spear Exception["+this.getClass().getName()+"] occured [" + errorCode + "-"
			+ errorDescription + "] Message["+message+"]", this.throwable);
    checkExceptionAgainstFatal();
  }

  public SparrowRuntimeException(String message) {
    super(message);
    setErrorCode(this.getClass().getName());
    setErrorDescription(message);
    this.throwable = this;
	logger.error("Spear Exception["+this.getClass().getName()+"] Message["+message+"]", this.throwable);
    checkExceptionAgainstFatal();
  }

  public final ExceptionHandler getExceptionHandler() {
    return ExceptionHandler.getHandler(throwable);
  }

  protected final void checkExceptionAgainstFatal() {
    ExceptionHandler handler = getExceptionHandler();
    if (handler.isFatal()) {
      handler.setFatalOnly();
    }
  }

  protected final void checkExceptionAgainstFatal(Exception e) {
    ExceptionHandler handler = ExceptionHandler.getHandler(e);
    if (handler.isFatal()) {
      handler.setFatalOnly();
    }
  }

  protected final void checkExceptionAgainstFatal(boolean fatal) {
    if (fatal) {
      ExceptionHandler handler = getExceptionHandler();
      handler.setFatalOnly();
    }
  }

}
