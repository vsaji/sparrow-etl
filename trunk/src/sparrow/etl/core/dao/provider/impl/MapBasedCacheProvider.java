/**
 *
 */
package sparrow.etl.core.dao.provider.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import sparrow.etl.core.config.SparrowDataProviderConfig;
import sparrow.etl.core.dao.impl.ColumnHeader;
import sparrow.etl.core.dao.impl.QueryObject;
import sparrow.etl.core.dao.impl.RecordSet;
import sparrow.etl.core.dao.impl.RecordSetImpl_Disconnected;
import sparrow.etl.core.dao.impl.ResultRow;
import sparrow.etl.core.dao.impl.RowIterator;
import sparrow.etl.core.dao.provider.DataProvider;
import sparrow.etl.core.dao.util.QueryExecutionStrategy;
import sparrow.etl.core.exception.DataException;
import sparrow.etl.core.util.CaseInSensitiveMap;
import sparrow.etl.core.util.ConfigKeyConstants;


/**
 * @author Saji Venugopalan
 *
 */
public class MapBasedCacheProvider extends CacheDataProvider {

  private Map cache;

  private final String[] lKeys;

  /**
   * @param provider
   * @param config
   */
  public MapBasedCacheProvider(DataProvider provider,
      SparrowDataProviderConfig config) {
    super(provider, config);
    String lookupKeys = config.getInitParameter().getParameterValue(
        ConfigKeyConstants.PARAM_LOOKUP_KEYS);
    lKeys = lookupKeys.toLowerCase().split("[,]");
    cache = new CaseInSensitiveMap();
  }

  /**
   *
   */
  public RecordSet applyFilter(String whereCondition, List param,
      String columns) {
    try {
      queryObject.setFilter(whereCondition);
      queryObject.getQueryParamAsArray().addAll(param);
      queryObject.setColumns(columns);
      return getData();
    } catch (DataException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   *
   */
  public RecordSet applyFilter(String whereCondition, Map param,
      String columns) {
    try {
      queryObject.setFilter(whereCondition);
      queryObject.getQueryParamAsMap().putAll(param);
      queryObject.setColumns(columns);
      return getData();
    } catch (DataException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   *
   */
  public Object clone() throws CloneNotSupportedException {
    // return super.clone();
    MapBasedCacheProvider mcp = (MapBasedCacheProvider) super.clone();
    mcp.queryObject = new QueryObject();
    mcp.provider = (DataProvider) provider.clone();
    mcp.cache = this.cache;
    // logger.info("["+getName()+"]["+mcp.hashCode()+"]["+cache.hashCode()+"]");
    return mcp;
  }

  /**
   *
   */
  public void destory() {
    cache.clear();
    cache = null;
    provider.destory();
  }

  /**
   *
   */
  public RecordSet getData() throws DataException {

    RecordSetImpl_Disconnected rs = new RecordSetImpl_Disconnected();
    try {
      if (queryObject.getFilter() != null
          && (!queryObject.getFilter().trim().equals(""))) {
        String key = getKey();
        Object o = cache.get(key);

        if (o != null) {
          rs.addRow((ResultRow) o);
        }
         logger.debug("["+getName()+"]: Key=["+key+"], Found=["+(o!=null)+"]");
      } else {
        rs.setResult(new ArrayList(cache.values()));
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      resetQueryObject();
    }
    return rs;
  }

  /**
   *
   * @return
   */
  private String getKey() {
    String filter = queryObject.getFilter();
    queryObject.setSQL(filter, false);
    QueryExecutionStrategy qes = QueryExecutionStrategy.getStrategy(
        getName() + "_CH", filter);
    qes.implementStrategy(queryObject);
    String transFilter = queryObject.getTransformedSQL();
    String[] splitFiler = transFilter.toLowerCase().split("and");

    String[] values = new String[lKeys.length];

    for (int i = 0; i < splitFiler.length; i++) {
      String[] furtherSplit = splitFiler[i].split("[=]");
      for (int j = 0; j < lKeys.length; j++) {
        if (lKeys[j].equals(furtherSplit[0].trim())) {
          Object o = (queryObject.getQueryParamAsArray().size() > 0) ? queryObject
              .getQueryParamAsArray().get(i): furtherSplit[1];
          values[j] = (o != null) ? o.toString() : null;
          values[j] = (values[j] != null) ? values[j].trim() : null;
          break;
        }

      }
    }

    StringBuffer sb = new StringBuffer();

    for (int i = 0; i < values.length; i++) {
      sb.append(values[i]).append("~");
    }
    sb.deleteCharAt(sb.length() - 1);
    String val = sb.toString();
    val = (val!=null) ? val.replaceAll("'",""): val;
    return val;
  }

  /**
   *
   */
  public void createCache(RecordSet rcSt) throws DataException {
    if (rcSt != null && rcSt.getRowCount() > 0) {
      for (RowIterator ri = rcSt.iterator(); ri.hasNext();) {
        ResultRow rr = ri.next();
        String hashKey = rr.getValues(lKeys, "~");
        cache.put(hashKey, rr);
      }
    }
    // logger.info("["+getName()+"]["+this.hashCode()+"]["+cache.hashCode()+"]");
  }

  /**
   *
   */
  void createTable(ColumnHeader ch) throws SQLException {
  }

  /**
   *
   */
  void truncateTable() throws DataException {
    if (logger.isDebugEnabled()) {
      logger.debug("Cache cleared for [" + getName() + "]");
    }
    cache.clear();
  }

}
