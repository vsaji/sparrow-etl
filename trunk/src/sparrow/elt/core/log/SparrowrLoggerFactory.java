package sparrow.elt.core.log;

public class SparrowrLoggerFactory {

  /** Constructor defined private so as to restrict instance creation */
  private SparrowrLoggerFactory() {
  }

  /**
   * This method returns an instance of InspireLogger.
   * Parameter passed is the name of class which requires logging. This method
   * creates an instance of InspireLogger and returns it to the caller.
   *
   * <p><b>The class would be used as follows:</b>
   * <p>Get instance of Logger object:
   * <p><code>private static InspireLogger logger = LoggerFactory
   * .getCurrentInstance(YourClassName.class);<code>
   *
   * @param className class which requires logging.
   * @return Instance of InspireLogger.
   */
  public static SparrowLogger getCurrentInstance(Class klass) {
    return new SparrowLoggerDefaultImpl(klass);
  }

  public static SparrowLogger getCurrentInstance() {
    return new SparrowLoggerDefaultImpl();
  }

  public static SparrowLogger getCurrentInstance(String className) {
    return new SparrowLoggerDefaultImpl(className);
  }
}
