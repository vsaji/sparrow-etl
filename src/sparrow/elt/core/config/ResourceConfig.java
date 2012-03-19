package sparrow.elt.core.config;

import sparrow.elt.jaxb.RESOURCEType;

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */
public interface ResourceConfig
    extends ConfigParam {

  abstract String getName();

  abstract String getClassName();

  abstract int getLoadPriority();

  abstract int getUnLoadPriority();

  abstract ResourceType getType();
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
class ResourceConfigImpl
    extends ConfigParamImpl
    implements ResourceConfig {

  private String name = null;
  private String className = null;
  private int loadPriority, unloadPriority = 0;

  private ResourceType type = null;

  ResourceConfigImpl(RESOURCEType resource) {
    super(resource.getPARAM());
    this.name = resource.getNAME();
    this.loadPriority = resource.getLOADPRIORITY();
    this.unloadPriority = resource.getUNLOADPRIORITY();
    this.className = resource.getCLASS();
    this.type = (resource.getTYPE().equalsIgnoreCase(ResourceType.OTHER.getResourceType())) ?
        new ResourceType("OTHER", className) :
        (ResourceType) ResourceType.TYPE_IDENTIFIER.get(resource.
        getTYPE().trim());
  }

  public String getName() {
    return this.name;
  }

  public String getClassName() {
    return this.className;
  }

  /**
   * getType
   *
   * @return String
   */
  public ResourceType getType() {
    return this.type;
  }

  /**
   * getLoadPriority
   *
   * @return int
   */
  public int getLoadPriority() {
    return this.loadPriority;
  }

  /**
   * getUnLoadPriority
   *
   * @return int
   */
  public int getUnLoadPriority() {
    return this.unloadPriority;
  }

}
