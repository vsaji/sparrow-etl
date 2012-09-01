package sparrow.etl.core.config;

import java.util.List;
import java.util.Map;

import sparrow.etl.core.exception.InitializationException;
import sparrow.etl.core.util.SparrowUtil;
import sparrow.etl.jaxb.NOTIFIERType;

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public interface NotifierConfig
    extends ConfigParam {

  abstract String getType();

  abstract String getClassName();

  abstract String getName();

}

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
class NotifierConfigImpl
    extends ConfigParamImpl
    implements NotifierConfig {

  private static Map TYPE_IDENTIFIER = SparrowUtil.getImplConfig("notifier");

  private final String type, className, name;

  /**
   *
   * @param notifier NOTIFIERType
   */
  NotifierConfigImpl(NOTIFIERType notifier) {
    super(notifier.getPARAM());
    this.type = notifier.getTYPE();
    this.name = notifier.getNAME();
    this.className = (notifier.getCLASS() != null &&
                      notifier.getCLASS().trim().equals("")) ?
        notifier.getCLASS() :
        (String) TYPE_IDENTIFIER.get(type);
    if (className == null) {
      throw new InitializationException("Notifier [" + name +
                                        "] could not be initialized. Either [" +
                                        type +
                                        "] could be a invalid type or class [" +
                                        className + "] could not be located");
    }

  }


  /**
   * getType
   *
   * @return String
   */
  public String getType() {
    return type;
  }

  /**
   * getClassName
   *
   * @return String
   */
  public String getClassName() {
    return className;
  }

  /**
   * getName
   *
   * @return String
   */
  public String getName() {
    return name;
  }

}
