package sparrow.elt.core.config;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import sparrow.elt.core.util.SparrowUtil;


/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */
public class ResourceType {

  public static final HashMap TYPE_IDENTIFIER = new HashMap() {
    {
      Map impls = SparrowUtil.getImplConfig("resources");
      for (Iterator it = impls.keySet().iterator(); it.hasNext(); ) {
        String key = (String) it.next();
        put(key, new ResourceType(key, (String) impls.get(key)));
      }
    }
  };

  public static final ResourceType OTHER = getResourceType("OTHER");

  /**
   *
   * @param type String
   * @return ResourceType
   */
  public static final ResourceType getResourceType(String type) {
    return (ResourceType) TYPE_IDENTIFIER.get(type);
  }

  private String resourceTypeInString;
  private String initializerClass;

  public String getResourceType() {
    return resourceTypeInString;
  }

  public String getResourceInitializerClass() {
    return initializerClass;
  }

  public ResourceType(String resourceTypeInString, String initializerClass) {
    this.resourceTypeInString = resourceTypeInString;
    this.initializerClass = initializerClass;
  }

  public boolean equals(Object o) {
    ResourceType r = (ResourceType) o;
    return this.resourceTypeInString.equals(r.resourceTypeInString);
  }

  /**
   *
   */
  private static final void loadResourceTypes() {
    Map impls = SparrowUtil.getImplConfig("resources");
    for (Iterator it = impls.keySet().iterator(); it.hasNext(); ) {
      String key = (String) it.next();
      TYPE_IDENTIFIER.put(key, new ResourceType(key, (String) impls.get(key)));
    }
  }

}
