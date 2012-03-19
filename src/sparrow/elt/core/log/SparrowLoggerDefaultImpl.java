package sparrow.elt.core.log;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public class SparrowLoggerDefaultImpl
    implements SparrowLogger {

  /**
   * Holds reference to Log4J Logger instance.
   */
  private Logger logger;
  private static final Level REQ_LEVEL = SparrowLevel.REQ_TRACE;

  /**
   * LoggerImpl No arg constructor
   */
  SparrowLoggerDefaultImpl() {
    this( (String)null);
  }

  /**
   * LoggerImpl constructor which returns an instance of Log4J Logger for
   * the Class specified.
   */
  SparrowLoggerDefaultImpl(Class className) {
    this(className.getName());
  }

  /**
   * LoggerImpl constructor which returns an instance of Log4J Logger for
   * the Class specified.
   */
  SparrowLoggerDefaultImpl(String className) {
    logger = (className != null) ? Logger.getLogger(className) :
        Logger.getRootLogger();

    String customLevel = System.getProperty("logger.level");
    if (customLevel != null) {
      logger.setLevel(Level.toLevel(customLevel.toUpperCase()));
    }

  }

  /**
   * This method is used to log message with a given log level.
   *
   * @param message Object representing the message object
   * @param level Log level - DEBUG, INFO, WARNING, ERROR OR FATAL
   */
  public void log(Object message, int level) {

    /* log message at the specified log level using log4j logger */
    logger.log(SparrowLevel.toLevel(level, Level.DEBUG), message);
  }

  /**
   *
   * @param message Object
   */
  public void debug(Object message) {
    if (isDebugEnabled()) {
      logger.log(Level.DEBUG, message);
    }
  }

  /**
   *
   * @param message Object
   * @param throwable Throwable
   */
  public void debug(Object message, Throwable throwable) {
    if (isDebugEnabled()) {
      logger.log(Level.DEBUG, message, throwable);
    }
  }

  /**
   *
   * @param message Object
   */
  public void info(Object message) {
    if (isInfoEnabled()) {
      logger.log(Level.INFO, message);
    }
  }

  /**
   *
   * @param message Object
   * @param throwable Throwable
   */
  public void info(Object message, Throwable throwable) {
    if (isInfoEnabled()) {
      logger.log(Level.INFO, message, throwable);
    }
  }

  /**
   *
   * @param message Object
   */
  public void warn(Object message) {
    logger.log(Level.WARN, message);
  }

  /**
   *
   * @param message Object
   * @param throwable Throwable
   */
  public void warn(Object message, Throwable throwable) {
    logger.log(Level.WARN, message, throwable);
  }

  /**
   *
   * @param message Object
   */
  public void error(Object message) {
    logger.log(Level.ERROR, message);
  }

  /**
   *
   * @param message Object
   * @param throwable Throwable
   */
  public void error(Object message, Throwable throwable) {
    logger.log(Level.ERROR, message, throwable);
  }

  /**
   * This method is used to log message with a given log level.
   *
   * @param message Object representing the message object
   * @param level Log level - DEBUG, INFO, WARNING, ERROR OR FATAL
   * @param thowable Object of type Throwable
   */
  public void log(Object message, int level, Throwable throwable) {
    /* log message along with the exception trace at the specified log level
     * using log4j logger */
    logger.log(Level.toLevel(level, Level.DEBUG), message, throwable);
  }

  /**
   * This method returns boolean flag which indicates whether Debug level is
   * Enabled for the Logger instance
   *
   * @return boolean flag which indicates whether Debug level is Enabled
   */
  public boolean isDebugEnabled() {
    return logger.isDebugEnabled();
  }

  /**
   * This method returns boolean flag which indicates whether Info level is
   * Enabled for the Logger instance
   *
   * @return boolean flag which indicates whether Info level is Enabled
   */
  public boolean isInfoEnabled() {
    return logger.isInfoEnabled();
  }

  /**
   * isReqTraceEnabled
   *
   * @return boolean
   */
  public boolean isReqTraceEnabled() {
    return logger.isEnabledFor(REQ_LEVEL);
  }

  /**
   * changeLevel
   *
   * @param level int
   */
  public void changeLevel(int level) {
    switch (level) {
      case SparrowLogger.INFO:
        logger.setLevel(Level.INFO);
        break;
      case SparrowLogger.WARN:
        logger.setLevel(Level.WARN);
        break;
      case SparrowLogger.ERROR:
        logger.setLevel(Level.ERROR);
        break;
      case SparrowLogger.FATAL:
        logger.setLevel(Level.FATAL);
        break;
      case SparrowLogger.REQ_TRACE:
        logger.setLevel(REQ_LEVEL);
        break;
      default:
        logger.setLevel(Level.DEBUG);
        break;
    }
  }

}
