package sparrow.etl.core.config;

import java.util.Map;

import sparrow.etl.core.exception.InitializationException;
import sparrow.etl.core.util.SparrowUtil;
import sparrow.etl.jaxb.EVENTType;


/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public interface EventConfig
    extends ConfigParam {
  abstract String getType();

  abstract String getNotifierName();

  abstract String getClassName();
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
class EventConfigImpl
    extends ConfigParamImpl
    implements EventConfig {

  private static Map TYPE_IDENTIFIER = SparrowUtil.getImplConfig("event");
  private final String type, notifierName, className;

  /**
   *
   * @param event EVENTType
   */
  EventConfigImpl(EVENTType event) {
    super(event.getPARAM());
    this.type = event.getTYPE().toLowerCase();
    this.notifierName = event.getNOTIFIER();

    this.className = (event.getCLASS() != null &&
                          event.getCLASS().trim().equals("")) ?
            event.getCLASS() :
            (String) TYPE_IDENTIFIER.get(type);

    if (className == null) {
      throw new InitializationException("Unrecognized Event type [" + type +
                                        "]");
    }
  }

  /**
   *
   * @return String
   */
  public String getType() {
    return type;
  }

  /**
   *
   * @return String
   */
  public String getNotifierName() {
    return notifierName;
  }

  /**
   * getClassName
   *
   * @return String
   */
  public String getClassName() {
    return className;
  }
}
