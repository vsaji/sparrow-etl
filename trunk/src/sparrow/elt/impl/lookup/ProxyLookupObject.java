package sparrow.elt.impl.lookup;

import java.util.Map;

import sparrow.elt.core.config.SparrowDataLookupConfig;
import sparrow.elt.core.context.SparrowContext;
import sparrow.elt.core.dao.impl.RecordSet;
import sparrow.elt.core.dao.impl.ResultRow;
import sparrow.elt.core.dao.provider.DataProvider;
import sparrow.elt.core.dao.provider.DataProviderElementExtn;
import sparrow.elt.core.dao.provider.impl.CacheDataProvider;
import sparrow.elt.core.exception.DataException;
import sparrow.elt.core.lookup.LookupObject;
import sparrow.elt.core.transformer.DriverRowEventListener;


/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */
public class ProxyLookupObject
    implements LookupObject {

  private final LookupObject lookupObj;

  public ProxyLookupObject(SparrowDataLookupConfig config) {

    String dataProviderName = config.getDataProvider();
    SparrowContext context = config.getContext();
    DataProviderElementExtn dpe = (DataProviderElementExtn) context.
        getDataProviderElement(dataProviderName);
    DataProvider dp = dpe.getDataProvider();
///    dpe.close();
    boolean isCached = (dp instanceof CacheDataProvider);
    lookupObj = (isCached) ? (LookupObject)new CacheLookupObject(config) :
        new NonCacheLookupObject(config);
  }

  /**
   * getLookupData
   *
   * @param row ResultRow
   * @param resultMap Map
   * @param eventListener DriverRowEventListener
   * @return RecordSet
   */
  public RecordSet getLookupData(ResultRow row, Map resultMap,
                                 DriverRowEventListener eventListener) throws
      DataException {

    return lookupObj.getLookupData(row, resultMap, eventListener);
  }
}
