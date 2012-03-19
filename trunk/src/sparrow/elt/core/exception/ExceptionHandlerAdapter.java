package sparrow.elt.core.exception;

public abstract class ExceptionHandlerAdapter
    extends ExceptionHandler {

  private String errorCode = null;
  private String errorDesc = null;
  private ErrorConfig config = null;

  protected ExceptionHandlerAdapter(ErrorConfig config) {
    this.config = config;
  }

  /**
   * isFatal
   *
   * @return boolean
   */
  public boolean isFatal() {
    return (config.getErrorTypeByCode(this.errorCode).equals("fatal") ||
            config.getErrorTypeByDescription(this.errorDesc).equals("fatal"));
  }

  /**
   * isRetriable
   *
   * @return boolean
   */
  public boolean isRetriable() {
    return (config.getErrorTypeByCode(this.errorCode).equals("retry") ||
            (config.getErrorTypeByDescription(this.errorDesc)).equals("retry"));
  }

  /**
   * isIgnorable
   *
   * @return boolean
   */
  public boolean isIgnorable() {
    return (config.getErrorTypeByCode(this.errorCode).equals("ignore") ||
            (config.getErrorTypeByDescription(this.errorDesc)).equals("ignore"));
  }

  /**
   *
   * @param errorCode String
   */
  protected void setErrorCode(String errorCode) {
    this.errorCode = errorCode;
  }

  /**
   *
   * @param errorDesc String
   */
  protected void setErrorDesc(String errorDesc) {
    this.errorDesc = errorDesc;
  }
}
