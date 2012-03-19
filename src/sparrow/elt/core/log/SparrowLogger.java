package sparrow.elt.core.log;

public interface SparrowLogger {

  /** This constant indicates logging at DEBUG level */
  public final static int DEBUG = 10000;

  /** This constant indicates to logging at INFO level */
  public final static int REQ_TRACE = 15000;

  /** This constant indicates to logging at INFO level */
  public final static int INFO = 20000;

  /** This constant indicates to logging at WARNING level */
  public final static int WARN = 30000;

  /** This constant indicates to logging at ERROR level */
  public final static int ERROR = 40000;

  /** This constant indicates to logging at FATAL level */
  public final static int FATAL = 50000;

  /**
   * This method is used to log message with a given log level.
   *
   * @param message Object representing the message object
   * @param level Log level - DEBUG, INFO, WARNING, ERROR OR FATAL
   */
  public void log(Object message, int level);

  /**
   * This method is used to log message with a given log level.
   *
   * @param message Object representing the message object
   * @param level Log level - DEBUG, INFO, WARNING, ERROR OR FATAL
   * @param thowable Object of type Throwable
   */
  public void log(Object message, int level, Throwable thowable);

  /**
   * This method returns boolean flag which indicates whether Debug level is
   * Enabled for the Logger instance
   *
   * @return boolean flag which indicates whether Debug level is Enabled
   */
  public boolean isDebugEnabled();

  /**
   * This method returns boolean flag which indicates whether Info level is
   * Enabled for the Logger instance
   *
   * @return boolean flag which indicates whether Info level is Enabled
   */
  public boolean isInfoEnabled();

  public boolean isReqTraceEnabled();

  public void debug(Object message);

  public void debug(Object message, Throwable throwable);

  public void info(Object message);

  public void info(Object message, Throwable throwable);

  public void warn(Object message);

  public void warn(Object message, Throwable throwable);

  public void error(Object message);

  public void error(Object message, Throwable throwable);

  public void changeLevel(int level);

}
