package sparrow.elt.impl.lookup;

import java.util.Map;

import sparrow.elt.core.config.SparrowDataLookupConfig;
import sparrow.elt.core.dao.impl.RecordSet;
import sparrow.elt.core.dao.impl.RecordSetImpl_Disconnected;
import sparrow.elt.core.dao.impl.ResultRow;
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
public class CacheLookupObject
    implements LookupObject {

  private final String dataProviderName;
  private final CacheDataProvider cp;
  private final String columns;
  private final String lookupName;
  private final String filter;

  public CacheLookupObject(SparrowDataLookupConfig config) {
    dataProviderName = config.getDataProvider();
    filter = config.getFilter();
    columns = config.getColumns();
    lookupName = config.getName();
    DataProviderElementExtn dbdpe = (DataProviderElementExtn) config.getContext().
        getDataProviderElement(
        dataProviderName);
    cp = (CacheDataProvider) dbdpe.getDataProvider();
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

    RecordSet rs = getCachedLookupData(resultMap, eventListener);

    return rs;
  }

  /**
   *
   * @param resultMap Map
   * @param listener DriverRowEventListener
   * @throws DataException
   * @return RecordSet
   */
  private RecordSet getCachedLookupData(Map resultMap,
                                        DriverRowEventListener listener) throws
      DataException {

    RecordSet rs = null;

    boolean proceed = listener.preLookUp(lookupName, null);

    if (proceed) {

      String fltr = listener.preFilter(lookupName, filter);
      rs = (fltr != null) ? cp.applyFilter(fltr, resultMap, columns) :
          cp.getData(columns,resultMap);
    }
    else {
      rs = new RecordSetImpl_Disconnected();
    }
    listener.postLookUp(lookupName, rs);
    return rs;
  }

}
