package sparrow.etl.core.config;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import sparrow.etl.jaxb.SERVICESType;
import sparrow.etl.jaxb.SERVICEType;


public interface ServicesConfig {
  abstract List getServices();
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
class ServicesConfigImpl
    implements ServicesConfig {

  private List servcs = null;

  /**
   *
   * @param services SERVICESType
   */
  ServicesConfigImpl(SERVICESType services) {
    servcs = new ArrayList();
    this.bind(services);
  }

  /**
   *
   * @return List
   */
  public List getServices() {
    return servcs;
  }

  /**
   *
   * @param service ServiceConfig
   */
  private void addDataSource(ServiceConfig service) {
    servcs.add(service);
  }

  /**
   *
   */
  private void bind(SERVICESType services) {
    if (services != null) {
      List servis = services.getSERVICE();
      if (!servis.isEmpty()) {
        for (Iterator iter = servis.iterator(); iter.hasNext(); ) {
          SERVICEType item = (SERVICEType) iter.next();
          addDataSource(new ServiceConfigImpl(item));
        }
      }
    }
  }

}
