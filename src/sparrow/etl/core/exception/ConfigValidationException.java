package sparrow.etl.core.exception;

public class ConfigValidationException
    extends SparrowRuntimeException {

  public ConfigValidationException(String msg) {
    super(msg);
  }

  public ConfigValidationException(String msg, Exception original) {
    super(msg, original);
  }

  public ConfigValidationException(Exception original) {
    super(original);
  }

}
