package sparrow.elt.core.log;

import org.apache.log4j.Level;

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public class SparrowLevel
    extends Level {
  /**
   *
   * @param level int
   * @param levelStr String
   * @param syslogEquivalent int
   */
  public SparrowLevel(int level, String levelStr, int syslogEquivalent) {
    super(level, levelStr, syslogEquivalent);
  }

  /**
   *
   */
  public static final SparrowLevel REQ_TRACE = new SparrowLevel(SparrowLogger.REQ_TRACE,
      "REQ_TRACE", 5);

  /**
   *
   * @param val int
   * @param defaultLevel Level
   * @return Level
   */
  public static Level toLevel(int val, Level defaultLevel) {
    switch(val){
      case SparrowLogger.REQ_TRACE:
        return SparrowLevel.REQ_TRACE;
      default:
        return Level.toLevel(val, defaultLevel);
    }
  }

  /**
   *
   * @param sArg String
   * @param defaultLevel Level
   * @return Level
   */
  public static Level toLevel(String sArg, Level defaultLevel) {
    if(sArg.toUpperCase().equals("REQ_TRACE")){
      return REQ_TRACE;
    }
    return Level.toLevel(sArg, defaultLevel);
  }

}
