package sparrow.elt.core.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import sparrow.elt.jaxb.DATAPROVIDERSType;
import sparrow.elt.jaxb.DATAPROVIDERType;


/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */
public interface DataProvidersConfig {

  abstract List getProviders();

  abstract DataProviderConfig getProvider(String providerName);

  abstract boolean isProviderExists(String providerName);
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
class DataProvidersConfigImpl
    implements DataProvidersConfig {

  private List providers = null;
  private Map namedProviders = null;

  /**
   *
   * @param queries QUERIESType
   */
  DataProvidersConfigImpl(DATAPROVIDERSType dataloader) {
    providers = new ArrayList();
    namedProviders = new HashMap();
    if (dataloader != null) {
      this.bind(dataloader);
    }
  }

  /**
   *
   * @return List
   */
  public List getProviders() {
    return providers;
  }

  /**
   *
   * @param subQuery SubQuery
   */
  private void addProvider(DataProviderConfig provider) {
    providers.add(provider);
    namedProviders.put(provider.getName(), provider);
  }

  /**
   *
   */
  private void bind(DATAPROVIDERSType dataprovider) {

    List hndlrs = dataprovider.getDATAPROVIDER();
    if (!hndlrs.isEmpty()) {
      for (Iterator iter = hndlrs.iterator(); iter.hasNext(); ) {
        DATAPROVIDERType item = (DATAPROVIDERType) iter.next();
        addProvider(new DataProviderConfigImpl(item));
      }
    }
  }

  /**
   * getProvider
   *
   * @return DataProviderConfig
   */
  public DataProviderConfig getProvider(String providerName) {
    return (DataProviderConfig) namedProviders.get(providerName);
  }

  /**
   * isProviderExists
   *
   * @param providerName String
   * @return boolean
   */
  public boolean isProviderExists(String providerName) {
    return namedProviders.containsKey(providerName);
  }

}
