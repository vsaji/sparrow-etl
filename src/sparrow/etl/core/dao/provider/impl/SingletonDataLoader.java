package sparrow.etl.core.dao.provider.impl;

import java.util.Map;

import sparrow.etl.core.dao.impl.QueryObject;
import sparrow.etl.core.dao.impl.RecordSet;
import sparrow.etl.core.exception.DataException;


/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */
public class SingletonDataLoader {

  /**
   *
   *
   */
  public SingletonDataLoader() {
  }

  /**
   *
   * @param cp IncrementalCacheDataProvider
   * @param filter String
   * @param param Map
   * @param columns String
   * @throws DataException
   * @return RecordSet
   */
  public synchronized final RecordSet loadFromDB(
		  IncrementalCacheProvider cp,
      String filter, Map param, String columns) throws DataException {

    //The below statment will help in avoiding a query being fired for a same
    //data from different thread in a concurrency senario. The first request
    //will load the result from DB and copy to cache,
    //so when the second thread is given a chance to execute this method it will
    //directly return the result from cache.
    RecordSet rs = cp.loadFromCache(filter, param, columns);

    try {
      if (rs.getRowCount() == 0) {
        rs = loadFromDB_(cp, param);

        //Some cases the query to DB and filter on cache may vary. So the below
        //condition will make sure that re-lookup on cache will happen only if
        //the filter is configured
        if(filter!=null){
          rs = cp.loadFromCache(filter, param, columns);
        }

      }
    }
    catch (DataException ex) {
      throw ex;
    }

    return rs;
  }

  /**
   *
   * @param cp IncrementalCacheDataProvider
   * @param param Map
   * @throws DataException
   * @return RecordSet
   */
  public synchronized final RecordSet loadFromDB(
      IncrementalCacheDataProvider cp,String columnNames,
      Map param) throws DataException, Exception {

    RecordSet rs = cp.loadFromCache(columnNames,param);

    try {
      if (rs.getRowCount() == 0) {
        rs = loadFromDB_(cp, param);

        if(rs.getRowCount()>0 && columnNames!=null){
          rs = cp.loadFromCache(columnNames,param);
        }
      }
    }
    catch (DataException ex) {
      throw ex;
    }
    return rs;
  }



  /**
     *
     * @param cp IncrementalCacheDataProvider
     * @param param Map
     * @throws DataException
     * @return RecordSet
     */
    public synchronized final RecordSet loadFromDB(
        IncrementalCacheProvider cp,QueryObject query) throws DataException, Exception {

      RecordSet rs = cp.superGetData();

      try {
        if (rs.getRowCount() == 0) {
          rs = loadFromDB_(cp, query);

          if(rs.getRowCount()>0){
            rs = cp.superGetData();
          }
        }
      }
      catch (DataException ex) {
        throw ex;
      }
      return rs;
    }


    /**
       *
       * @param cp CacheDataProvider
       * @param param Map
       * @throws DataException
       * @return RecordSet
       */
      private static final RecordSet loadFromDB_(
    		  IncrementalCacheProvider cp,
          QueryObject query) throws DataException {

        RecordSet rs = null;

        try {

          if(query.getQueryParamAsArray().size() > 0){
            cp.getDataProvider().getQuery().getQueryParamAsArray().addAll(query.getQueryParamAsArray());
          }

          if(query.getQueryParamAsMap().size() > 0){
            cp.getDataProvider().getQuery().getQueryParamAsMap().putAll(query.getQueryParamAsMap());
          }


          rs = cp.getDataProvider().getData();

          if (rs.getRowCount() > 0) {
            cp.createCache(rs);
          }
        }
        catch (DataException ex) {
          throw ex;
        }
        return rs;
      }



  /**
   *
   * @param cp CacheDataProvider
   * @param param Map
   * @throws DataException
   * @return RecordSet
   */
  private static final RecordSet loadFromDB_(
		  IncrementalCacheProvider cp,
      Map param) throws DataException {

    RecordSet rs = null;

    try {

      int paramS = param.size();
      int qParamS = cp.getDataProvider().getQuery().getQueryParamAsMap().size();

      if (paramS != qParamS) {
        cp.getDataProvider().getQuery().getQueryParamAsMap().putAll(param);
      }

      rs = cp.getDataProvider().getData();

      if (rs.getRowCount() > 0) {
        cp.createCache(rs);
      }
    }
    catch (DataException ex) {
      throw ex;
    }
    return rs;
  }

}
