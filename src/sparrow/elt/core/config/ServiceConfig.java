package sparrow.elt.core.config;

import sparrow.elt.core.exception.SparrowRuntimeException;
import sparrow.elt.jaxb.SERVICEType;

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public interface ServiceConfig
    extends ConfigParam, DependentIndexingSupport {

  abstract ServiceType getType();

  abstract boolean isCycleNotificatinonRequired();

  abstract boolean isAppNotificatinonRequired();

}

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */
class ServiceConfigImpl
    extends ConfigParamImpl
    implements ServiceConfig {

  private String name = null;
  private String depends = null;
  private boolean cycleNotificatinonRequired = false;
  private boolean appNotificatinonRequired = false;
  private ServiceType type = null;

  /**
   *
   * @param service SERVICEType
   */
  ServiceConfigImpl(SERVICEType service) {
    super(service.getPARAM());

    this.name = service.getNAME();
    String className = service.getCLASS();
    this.cycleNotificatinonRequired = service.isCYCLENOTIFICATION();
    this.appNotificatinonRequired = service.isAPPNOTIFICATION();
    this.depends = service.getDEPENDS();

    if (className == null || className.trim().equals("")) {

      String identifierKey = service.getTYPE().trim().toUpperCase();

      this.type = (ServiceType) ServiceType.TYPE_IDENTIFIER.get(
          identifierKey);

      if (type == null) {
        throw new SparrowRuntimeException("Unrecognized TYPE [" +
                                        service.getTYPE() + "] : Key [" +
                                        identifierKey + "]");
      }
    }
    else {
      this.type = new ServiceType(ServiceType.OTHER,
                                          className);
    }
  }

  /**
   *
   * @return String
   */
  public String getName() {
    return this.name;
  }

  /**
   *
   * @return String
   */
  public String getDepends() {
    return this.depends;
  }

  /**
   *
   * @return boolean
   */
  public boolean isCycleNotificatinonRequired() {
    return this.cycleNotificatinonRequired;
  }

  /**
   *
   * @return boolean
   */
  public boolean isAppNotificatinonRequired() {
    return this.appNotificatinonRequired;
  }

  /**
   * getType
   *
   * @return ServiceType
   */
  public ServiceType getType() {
    return this.type;
  }
}
