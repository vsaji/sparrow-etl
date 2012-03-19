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
public class DataProviderType {

  public static final String BASE_PROVIDER_CLASS =
      "sparrow.elt.core.dao.provider.BaseDataProviderElement";
  public static final String PROVIDER_OTHER = "OTHER";

  public static final HashMap TYPE_IDENTIFIER = new HashMap() {
    {
      Map impls = SparrowUtil.getImplConfig("provider");
      for (Iterator it = impls.keySet().iterator(); it.hasNext(); ) {
        String key = (String) it.next();
        String[] values = impls.get(key).toString().split("[,]");
        put(key, new DataProviderType(key, values[0], values[1]));
      }
    }
  };

  private final String providerType;
  private final String providerElementClass;
  private final String providerClass;

  public String getProviderElementClass() {
    return providerElementClass;
  }

  public String getProviderType() {
    return providerType;
  }

  public String getProviderClass() {
    return providerClass;
  }

  public DataProviderType(String providerType, String providerElementClass,
                          String providerClass) {
    this.providerType = providerType;
    this.providerElementClass = providerElementClass;
    this.providerClass = providerClass;
  }

}
