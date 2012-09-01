package sparrow.etl.core.dao.provider;

import java.util.HashMap;

import sparrow.etl.core.config.DataProviderConfig;
import sparrow.etl.core.dao.cache.CacheStorage;
import sparrow.etl.core.exception.SparrowRuntimeException;


public class DataProviderElementResolver {

  private CacheStorage cs;
  private HashMap dataProviderObjectMap;
  private HashMap resolverMap;

  public DataProviderElementResolver() {
  }

  /**
   *
   * @param cs CacheStorage
   */
  public void setCacheStorage(CacheStorage cs) {
    this.cs = cs;
  }

  /**
   *
   * @param dhpMap HashMap
   */
  public void setDataProviderObjectMap(HashMap dhpMap) {
    dataProviderObjectMap = dhpMap;
  }

  /**
   *
   * @param resolverMap HashMap
   */
  public void setResolver(HashMap resolverMap) {
    this.resolverMap = resolverMap;
  }

  /**
   *
   * @param name String
   * @return DataProviderElement
   */
  public synchronized DataProviderElement getDataProviderElement(
      DataProviderConfig dHandler) {

    String dHandlerName = dHandler.getName();
    String resolverKey = resolverMap.get(dHandlerName).toString();
    DataProviderElementExtn dhc = null;
    DataProviderElementExtn rntObj = null;

    /** if (resolverKey.equals(Constants.CACHE_RESOLVER_KEY)) {
     rntObj = (DataProviderElementExtn) cs.getDataProviderElement(dHandlerName);
     }
     else {
       //     DataProviderElementPoolManager dhcp = (DataProviderElementPoolManager)
       //          dataProviderPoolMap.
      //          get(resolverKey);

       //  dhc = (DataProviderElementExtn) dhcp.getDataProviderElement();
     **/
    dhc = (DataProviderElementExtn) dataProviderObjectMap.
        get(dHandlerName);
    try {
      rntObj = (DataProviderElementExtn) dhc.clone();
      // rntObj.getDataProvider().setDataProviderConfig(dHandler);
      rntObj.initialize();
    }
    catch (CloneNotSupportedException ex) {
      throw new SparrowRuntimeException(
          "CloneNotSupportedException while getting cloned object [" +
          resolverKey + "] ");
    }

    // }
    return rntObj;
  }

}
