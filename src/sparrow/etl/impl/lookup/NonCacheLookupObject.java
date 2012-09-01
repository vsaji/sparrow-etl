package sparrow.etl.impl.lookup;

import java.util.Map;

import sparrow.etl.core.config.SparrowDataLookupConfig;
import sparrow.etl.core.dao.impl.QueryObject;
import sparrow.etl.core.dao.impl.RecordSet;
import sparrow.etl.core.dao.impl.RecordSetImpl_Disconnected;
import sparrow.etl.core.dao.impl.ResultRow;
import sparrow.etl.core.dao.provider.DataProviderElement;
import sparrow.etl.core.dao.provider.impl.DBDataProviderElement;
import sparrow.etl.core.exception.DataException;
import sparrow.etl.core.exception.InitializationException;
import sparrow.etl.core.log.SparrowLogger;
import sparrow.etl.core.log.SparrowrLoggerFactory;
import sparrow.etl.core.lookup.LookupObject;
import sparrow.etl.core.transformer.DriverRowEventListener;
import sparrow.etl.core.util.Constants;


/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */
public class NonCacheLookupObject
    implements LookupObject {

  private final DataProviderElement dpe;
  private final String lookupName;
  private final boolean isDBLookUp;
  private final DBDataProviderElement dbdpe;

  /**
   *
   */
  private static final SparrowLogger logger = SparrowrLoggerFactory.
      getCurrentInstance(
      NonCacheLookupObject.class);

  public NonCacheLookupObject(SparrowDataLookupConfig config) {

    String dataProviderName = config.getDataProvider();
    String filter = config.getFilter();
    String columns = config.getColumns();

    if (filter != null || columns != null) {
      logger.warn("[" + config.getName() + "] : FILTER and COLUMN elements are not effective if a non-cache data provider is used. DATA-PROVIDER[" +
                  dataProviderName + "]");
    }

    dpe = config.getContext().getDataProviderElement(dataProviderName);
    isDBLookUp = (dpe instanceof DBDataProviderElement);
    dbdpe = (isDBLookUp) ? (DBDataProviderElement) dpe : null;
    lookupName = config.getName();
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

    RecordSet rs = (isDBLookUp) ? getDBLookupData(resultMap, eventListener) :
        getNonDBLookupData(resultMap, eventListener);

    return rs;
  }

  /**
   *
   * @param row ResultRow
   * @param resultMap Map
   * @throws DataException
   * @return RecordSet
   */
  private RecordSet getNonDBLookupData(Map resultMap,
                                       DriverRowEventListener listener
                                       ) throws DataException {

    RecordSet rs = null;
    boolean proceed = listener.preLookUp(lookupName, null);
    rs = (proceed) ? dpe.getData() : new RecordSetImpl_Disconnected();
    listener.postLookUp(lookupName, rs);
    return rs;
  }

  /**
   *
   * @param resultMap Map
   * @param listener DriverRowEventListener
   * @throws DataException
   * @return RecordSet
   */
  private RecordSet getDBLookupData(Map resultMap,
                                    DriverRowEventListener listener) throws
      DataException {

    QueryObject q1 = dbdpe.getQuery();
    RecordSet rs = null;

    boolean proceed = listener.preLookUp(lookupName, q1);
    rs = (proceed) ? getData(q1, resultMap) :
        new RecordSetImpl_Disconnected();
    listener.postLookUp(lookupName, rs);

    return rs;
  }

  /**
   *
   * @param query QueryObject
   * @param resultMap Map
   * @throws DataException
   * @return RecordSet
   */
  private RecordSet getData(QueryObject query, Map resultMap) throws
      DataException {
    if (query != null) {
      query.getQueryParamAsMap().putAll(resultMap);
      query.setRsWrapType(Constants.RESULT_WRAP_DISCONNECTED);
      return dbdpe.getData();
    }
    else {
      return null;
    }
  }

}
