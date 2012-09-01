package sparrow.etl.core.dao.provider.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import sparrow.etl.core.config.QueryConfig;
import sparrow.etl.core.config.QueryConfigImpl;
import sparrow.etl.core.config.SparrowConfig;
import sparrow.etl.core.config.SparrowDataExtractorConfig;
import sparrow.etl.core.config.SparrowDataProviderConfig;
import sparrow.etl.core.config.SparrowDataWriterConfig;
import sparrow.etl.core.context.SparrowContext;
import sparrow.etl.core.dao.AbstractBaseDAO;
import sparrow.etl.core.dao.impl.QueryObject;
import sparrow.etl.core.dao.impl.RecordSet;
import sparrow.etl.core.dao.impl.RecordSetImpl_Disconnected;
import sparrow.etl.core.dao.util.ConnectionProvider;
import sparrow.etl.core.dao.util.DBUtil;
import sparrow.etl.core.dao.util.QueryExecutionStrategy;
import sparrow.etl.core.dao.util.SPParamInfo;
import sparrow.etl.core.exception.DataException;
import sparrow.etl.core.log.SparrowLogger;
import sparrow.etl.core.log.SparrowrLoggerFactory;
import sparrow.etl.core.util.ConfigKeyConstants;
import sparrow.etl.core.util.Constants;
import sparrow.etl.core.util.SparrowUtil;


/**
 *
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * <p>
 * Company:
 * </p>
 *
 * @author Saji Venugopalan
 * @version 1.0
 */
public class DBDataProviderImpl
    extends AbstractBaseDAO
    implements
    DBDataProvider {

  protected final SparrowContext context;
  protected QueryObject query = null;
  protected QueryExecutionStrategy qes = null;

  private String name = null;
  private boolean suppressNoResultDBCall = false;

  private QueryObject copy = null;
  private QueryExecutor qh = null;
  private ConnectionProvider connectionProvider = null;
  private ConnectionProvider defaultCPRef = new DefaultConnectionProvider();
  private SPParamInfo spParam = null;
  private HashSet suppressEntries = null;

  private static final SparrowLogger logger = SparrowrLoggerFactory
      .getCurrentInstance(DBDataProviderImpl.class);

  /**
   *
   * @param config
   *            SparrowDataProviderConfig
   */
  public DBDataProviderImpl(SparrowDataProviderConfig config) {
    this( (SparrowConfig) config);
  }

  /**
   *
   * @param config
   *            SparrowDataExtractorConfig
   */
  public DBDataProviderImpl(SparrowDataExtractorConfig config) {
    this( (SparrowConfig) config);
  }

  /**
   *
   * @param config
   *            SparrowDataWriterConfig
   */
  public DBDataProviderImpl(SparrowDataWriterConfig config) {
    this( (SparrowConfig) config);
  }

  /**
   *
   * @param context
   *            SparrowContext
   * @param config
   *            SparrowConfig
   */
  public DBDataProviderImpl(SparrowConfig config) {
    this.context = config.getContext();
    this.name = config.getName();
    createQueryObject(new QueryConfigImpl(config.getInitParameter()));

    if (query.getSPOutParam() == null) {
      qh = new DefaultQueryExecutor();
    }
    else {
      spParam = new SPParamInfo(query.getSPOutParam());
      qh = new SPQueryExecutor();
    }
    this.connectionProvider = defaultCPRef;
  }

  /**
   *
   * @param qConfig
   *            QueryConfig
   */
  private void createQueryObject(QueryConfig qConfig) {
    this.query = new QueryObject(name);
    query.setConnectionName(qConfig.getDBSource());
    query.setSQL(qConfig.getSQL());
    query.setFetchSize(qConfig.getFetchSize());
    query.setRsWrapType(qConfig.getResultWrapType());
    qes = QueryExecutionStrategy.getStrategy(name, query.getSQL());
    query.setTransformedSQL(qes.getSQL());
    query.setSPOutParam(qConfig.getSPOutParamter());
    query.setUseDB(qConfig.getParameter().getParameterValue(
        ConfigKeyConstants.PARAM_USE_DB));
    this.copy = this.query.getCopy();

    if (SparrowUtil.performTernary(qConfig.getParameter(),
                                 ConfigKeyConstants.PARAM_SUP_NORSLT_DBCALL, false)) {
      checkAndUpdateSupNoResltDBClCriteria();
    }
  }

  /**
   *
   */
  private void checkAndUpdateSupNoResltDBClCriteria() {
    if ( (!query.isStoreProc()) && (!qes.getSQL().equals(query.getSQL()))) {

      suppressNoResultDBCall = true;
      suppressEntries = new HashSet();
      logger.info("Data Provider [" + this.name +
                  "] Suppression of DB Call enabled for non-result query argument.");
    }
    else {
      logger.warn("Data Provider [" + this.name + "] PARAM[" +
                  ConfigKeyConstants.PARAM_SUP_NORSLT_DBCALL +
                  "] does not support Store Procedure and Non-Dynamic Prepared Statement queries.");
    }
  }

  /**
   *
   * @return QueryExecutor
   */
  private QueryExecutor getQueryExecutor() {
    qh = (query.getSPOutParam() == null) ?
        (QueryExecutor)new DefaultQueryExecutor()
        : new SPQueryExecutor();
    return qh;
  }

  /**
   *
   * @return QueryExecutionStrategy
   */
  protected final QueryExecutionStrategy getQueryStrategy() {
    return qes;
  }

  /**
   *
   * @param query
   *            QueryObject
   */
  public void setQuery(QueryObject query) {
    this.query = query;
    this.copy = this.query.getCopy();
  }

  /**
   *
   * @param connectionProvider
   *            ConnectionProvider
   */
  public void setConnectionProvider(ConnectionProvider connectionProvider) {
    this.connectionProvider = (connectionProvider == null) ? defaultCPRef
        : connectionProvider;
  }

  /**
   *
   * @return QueryObject
   */
  public QueryObject getQuery() {
    return query;
  }

  /**
   * getData
   *
   * @return Object
   */
  public RecordSet getData() throws DataException {
    // System.out.println(this.hashCode() + ":" + query.hashCode());
    return qh.getData();
    /***********************************************************************
     * RecordSet rs = null;
     *
     * try {
     *
     * qes.implementStrategy(query);
     *  // System.out.println("QES HASHCODE:"+qes.hashCode());
     *
     * rs = (query.getRsWrapType().equals(Constants. RESULT_WRAP_CONNECTED) ?
     * super.searchForRSWrap(query.getTransformedSQL(),
     * query.getQueryParamAsArray().toArray(), query.getFetchSize(),
     * query.isStoreProc()) : super.search(query.getTransformedSQL(),
     * query.getQueryParamAsArray().toArray(), query.getFetchSize(),
     * query.isStoreProc()));
     *
     * /** if (query.getSQL().indexOf("?") != -1) { rs =
      * (query.getRsWrapType().equals(Constants. RESULT_WRAP_CONNECTED) ?
      * super.searchForRSWrap(query.getSQL(),
      * query.getQueryParamAsArray().toArray(), query.getFetchSize()) :
      * super.search(query.getSQL(), query.getQueryParamAsArray().toArray(),
      * query.getFetchSize())); } else { rs =
      * (query.getRsWrapType().equals(Constants. RESULT_WRAP_CONNECTED) ?
      * super.searchForRSWrap(parseSQL(query.getSQL(),
      * query.getQueryParamAsMap()), null, query.getFetchSize()) :
      * super.search(parseSQL(query.getSQL(), query.getQueryParamAsMap()),
      * null, query.getFetchSize())); } } finally { resetQueryObject(); }
      * return rs;
      **********************************************************************/
  }

  /**
   * loadData
   */
  public void loadData() throws DataException {
    throw new DataException("Operation not supported");
  }

  /**
   *
   * @return int
   */
  public int executeQuery() throws DataException {

    return qh.executeQuery();
    /**
     * String sql = query.getSQL();
     *
     * try {
     *
     * boolean isNotSelectStmt = (sql.toLowerCase().indexOf("update") != -1 ||
     * sql.toLowerCase().indexOf("insert") != -1 ||
     * sql.toLowerCase().indexOf("delete") != -1); int updateCnt = 0; if
     * (isNotSelectStmt || query.isStoreProc()) { // qes =
     * QueryExecutionStrategy.getStrategy(query.getSQL());
     * qes.implementStrategy(query); updateCnt =
     * super.executeQry(query.getTransformedSQL(),
     * query.getQueryParamAsArray().toArray(), openConnection(),
     * query.isStoreProc());
     *
     * /** if (query.getSQL().indexOf("?") != -1) { updateCnt =
      * super.executeQry(parseSQL(query.getSQL(),
      * query.getQueryParamAsMap()), query.getQueryParamAsArray().toArray(),
      * openConnection()); } else { updateCnt =
      * super.executeQry(parseSQL(query.getSQL(),
      * query.getQueryParamAsMap()), null, openConnection()); }
      */
     /***********************************************************************
      * return updateCnt; } else { throw new DataException("SQL \"" + sql +
      * "\" is not valid for executeQuery operation"); } } finally {
      * resetQueryObject(); }
      **********************************************************************/

  }

  /**
   * openConnection
   *
   * @return Connection
   */
  protected Connection openConnection() throws DataException {
    try {
      return connectionProvider.getConnection(query.getConnectionName());
    }
    catch (SQLException ex) {
      throw new DataException("SQLException while obtaining connection",
                              ex);
    }
  }

  /**
   * getName
   *
   * @return String
   */
  public String getName() {
    return name;
  }

  /**
   * destory
   */
  public void destory() {
    query = null;
  }

  /**
   * initialize
   */
  public void initialize() {
    this.copy = this.query.getCopy();
  }

  /**
   *
   * @param sql
   *            String
   * @param params
   *            Map
   * @return String
   */
  String parseSQL(String sql, Map params) {
    return DBUtil.parseQuery(query.getSQL(), params);
  }

  /**
   * closeConnection
   */
  public void closeConnection() throws DataException {
    throw new DataException("Operation not supported");
  }

  /**
   * getConnection
   *
   * @return Connection
   */
  public Connection getConnection() throws DataException {
    throw new DataException("Operation not supported");
  }

  /**
   *
   */
  protected final void resetQueryObject() {
    this.query.reset(copy);
  }

  /**
   *
   * @throws CloneNotSupportedException
   * @return Object
   */
  public Object clone() throws CloneNotSupportedException {
    DBDataProviderImpl dbdp = (DBDataProviderImpl)super.clone();
    dbdp.setQuery(copy.getCopy());
    dbdp.qh = dbdp.getQueryExecutor();
    return dbdp;
  }

  /**
   * executeBatch
   *
   * @param param
   *            List
   * @return int
   */
  public int[] executeSQLBatch(List param) throws DataException {
    return super.executeBatch(param);
  }

  /**
   *
   * @param sql
   *            String
   * @param param
   *            List
   * @throws DataException
   * @return int[]
   */
  public int[] executeBatch(String sql, List param) throws DataException {
    return super.executeBatch(sql, param, query.isStoreProc());
  }

  /**
   *
   * @param sql
   *            String
   * @param param
   *            List
   * @throws DataException
   * @return int[]
   */
  public int[] executeBatch(List param) throws DataException {
    int[] rslt = null;
    try {
      if (param != null && param.size() > 0) {
        // qes.implementStrategy(query);
        rslt = super.executeBatch(query.getTransformedSQL(), param,
                                  query.isStoreProc());
      }
      else {
        throw new DataException("Param is null");
      }
    }
    finally {
      resetQueryObject();
    }
    return rslt;
  }

  /**
   *
   * <p>
   * Title:
   * </p>
   * <p>
   * Description:
   * </p>
   * <p>
   * Copyright: Copyright (c) 2004
   * </p>
   * <p>
   * Company:
   * </p>
   *
   * @author not attributable
   * @version 1.0
   */
  interface QueryExecutor {
    RecordSet getData() throws DataException;

    int executeQuery() throws DataException;
  }

  /**
   *
   * <p>
   * Title:
   * </p>
   * <p>
   * Description:
   * </p>
   * <p>
   * Copyright: Copyright (c) 2004
   * </p>
   * <p>
   * Company:
   * </p>
   *
   * @author not attributable
   * @version 1.0
   */
  private class DefaultQueryExecutor
      implements QueryExecutor {
    /**
     * getData
     *
     * @return RecordSet
     */
    public RecordSet getData() throws DataException {
      RecordSet rs = null;

      try {
        /**
         * System.out.println(DBDataProviderImpl.this.hashCode() + ":" +
         * DBDataProviderImpl.this.query.hashCode() + ":" +
         * this.hashCode() + ":" + query.hashCode());
         */
        qes.implementStrategy(query);

        //-----------------------------------------------
        if (suppressNoResultDBCall && query.getQueryParamAsArray().size() > 0) {
          if (suppressEntries.contains(new Integer(query.getQueryParamAsArray().
              hashCode()))) {
        	  if(logger.isDebugEnabled()){
        		  logger.debug("Data Provider [" + getName() +
                        "]: ** DB CALL SUPPRESSED ["+query.getQueryParamAsArray()+"] **");
        	  }
            return RecordSet.EMPTY_RECORDSET;
          }
        }
        //-----------------------------------------------

        rs = (query.getRsWrapType().equals(
            Constants.RESULT_WRAP_CONNECTED) ? searchForRSWrap() : search());

        int recCount = rs.getRowCount();
        if (logger.isDebugEnabled()) {
          logger.debug("Data Provider [" + getName() +
                       "]: Record Count [" + recCount + "]");
        }
        //-----------------------------------------------
        if (suppressNoResultDBCall && recCount == 0) {
          suppressEntries.add(new Integer(query.getQueryParamAsArray().hashCode()));
          logger.warn("Data Provider [" + getName() +
                      "]: ** DB CALL SUPPRESS ENTRY ADDED [" +
                      query.getQueryParamAsArray() + "] **");
        }
        //-----------------------------------------------
      }
      finally {
        resetQueryObject();
      }
      return rs;
    }

    /**
     * executeQuery
     *
     * @return int
     */
    public int executeQuery() throws DataException {
      String sql = query.getSQL().toLowerCase();

      try {

        boolean isNotSelectStmt = (sql.toLowerCase().indexOf("update") != -1 || sql.toLowerCase().indexOf("truncate") != -1
                                   || sql.toLowerCase().indexOf("insert") != -1 || sql.toLowerCase().indexOf("delete") != -1
                                   || sql.toLowerCase().indexOf("drop") != -1 || sql.toLowerCase().indexOf("create") != -1
                                   || sql.toLowerCase().indexOf("alter") != -1);
        int updateCnt = 0;

        if (isNotSelectStmt || query.isStoreProc()) {
          qes.implementStrategy(query);
          updateCnt = executeQry(query.getTransformedSQL(), query
                                 .getQueryParamAsArray().toArray(),
                                 openConnection(), query.isStoreProc());

          if (logger.isDebugEnabled()) {
            logger.debug("Data Writer [" + getName() +
                         "]: Affected record count [" + updateCnt + "]");
          }

          return updateCnt;
        }
        else {
          throw new DataException("SQL \"" + query.getSQL()
                                  +
                                  "\" is not valid for executeQuery operation");
        }
      }
      finally {
        resetQueryObject();
      }
    }

  }

  /**
   *
   * <p>
   * Title:
   * </p>
   * <p>
   * Description:
   * </p>
   * <p>
   * Copyright: Copyright (c) 2004
   * </p>
   * <p>
   * Company:
   * </p>
   *
   * @author not attributable
   * @version 1.0
   */
  private class SPQueryExecutor
      implements QueryExecutor {

    String sql;

    final boolean isSQLFunction;

    /**
     *
     * @param outParam
     *            String
     */
    SPQueryExecutor() {
      isSQLFunction = (qes.getSQL().toLowerCase().indexOf(" call ") != -1);
    }

    /**
     * getData
     *
     * @return RecordSet
     */
    public RecordSet getData() throws DataException {
      /**
       * System.out.println(DBDataProviderImpl.this.hashCode() + ":" +
       * DBDataProviderImpl.this.query.hashCode() + ":" + this.hashCode() +
       * ":" + query.hashCode());
       */
      RecordSet rs = new RecordSetImpl_Disconnected();
      try {
        query.getQueryParamAsMap().put(KEY_SP_OUT, VALUE_SP_OUT);
        qes.implementStrategy(query);
        rs = executeStoredProc(spParam, isSQLFunction);

        if (logger.isDebugEnabled()) {
          logger.debug("Data Provider [" + getName() +
                       "]: Record Count [" + rs.getRowCount() + "]");
        }

      }
      finally {
        resetQueryObject();
      }
      return rs;
    }


    /**
     * executeQuery
     *
     * @return int
     */
    public int executeQuery() throws DataException {
      throw new DataException("Unsupported operation");
    }

  }

  /**
   *
   * <p>
   * Title:
   * </p>
   * <p>
   * Description:
   * </p>
   * <p>
   * Copyright: Copyright (c) 2004
   * </p>
   * <p>
   * Company:
   * </p>
   *
   * @author not attributable
   * @version 1.0
   */
  private class DefaultConnectionProvider
      implements ConnectionProvider {
    /**
     * getConnection
     *
     * @return Connection
     */
    public Connection getConnection(String conName) throws SQLException {
      return context.getDBConnection(conName);
    }

  }

}
