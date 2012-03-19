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
 * @author not attributable
 * @version 1.0
 */
public class ServiceType {


  public static final String OTHER = "OTHER";
  /**
   *
   */
  public static final HashMap TYPE_IDENTIFIER = new HashMap() {
    {
      Map impls = SparrowUtil.getImplConfig("service");
      for (Iterator it = impls.keySet().iterator(); it.hasNext(); ) {
        String key = (String) it.next();
        String value = impls.get(key).toString();
        put(key, new ServiceType(key, value));
      }
    }
  };

  private final String serviceType;
  private final String serviceClass;

  /**
   *
   * @return String
   */
  public String getServiceClass() {
    return serviceClass;
  }

  /**
   *
   * @return String
   */
  public String getServiceType() {
    return serviceType;
  }

  /**
   *
   * @param transformerType String
   * @param transformerClass String
   */
  public ServiceType(String serviceType, String serviceClass) {
    this.serviceType = serviceType;
    this.serviceClass = serviceClass;
  }

}
