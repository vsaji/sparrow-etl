package sparrow.elt.core.dao.cache;

import java.util.Hashtable;

import sparrow.elt.core.dao.provider.DataProviderElement;


/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */
public class CacheStorage {

  private Hashtable cacheStore = null;

  /**
   *
   */
  public CacheStorage() {
    cacheStore = new Hashtable();
  }

  public void addDataProviderElement(DataProviderElement dhContext) {
    cacheStore.put(dhContext.getName(), dhContext);
  }

  public DataProviderElement getDataProviderElement(String name) {
    return (DataProviderElement) cacheStore.get(name);
  }

}
