package sparrow.etl.core.config;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import sparrow.etl.jaxb.RESOURCESType;
import sparrow.etl.jaxb.RESOURCEType;


/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */
public interface ResourcesConfig {
  abstract List getResources();
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
class ResourcesConfigImpl
    implements ResourcesConfig {

  private List resrces = null;

  /**
   *
   * @param datasources DATASOURCESType
   */
  ResourcesConfigImpl(RESOURCESType resources) {
    resrces = new ArrayList();
    this.bind(resources);
  }

  /**
   *
   * @return List
   */
  public List getResources() {
    return resrces;
  }

  /**
   *
   * @param datasource DatasourceConfig
   */
  private void addResource(ResourceConfig resource) {
    resrces.add(resource);
  }

  /**
   *
   */
  private void bind(RESOURCESType resources) {
    if (resources != null) {
      List resrcs = resources.getRESOURCE();
      if (!resrcs.isEmpty()) {
        for (Iterator iter = resrcs.iterator(); iter.hasNext(); ) {
          RESOURCEType item = (RESOURCEType) iter.next();
          addResource(new ResourceConfigImpl(item));
        }
      }
    }
  }

}
