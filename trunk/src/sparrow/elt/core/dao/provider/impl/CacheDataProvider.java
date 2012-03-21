package sparrow.elt.core.dao.provider.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbcp.BasicDataSource;

import sparrow.elt.core.config.SparrowDataProviderConfig;
import sparrow.elt.core.dao.impl.ColumnHeader;
import sparrow.elt.core.dao.impl.QueryObject;
import sparrow.elt.core.dao.impl.RecordSet;
import sparrow.elt.core.dao.impl.RecordSetImpl_Disconnected;
import sparrow.elt.core.dao.impl.RowIterator;
import sparrow.elt.core.dao.provider.DataProvider;
import sparrow.elt.core.dao.util.ConnectionProvider;
import sparrow.elt.core.dao.util.QueryExecutionStrategy;
import sparrow.elt.core.exception.DataException;
import sparrow.elt.core.log.SparrowLogger;
import sparrow.elt.core.log.SparrowrLoggerFactory;
import sparrow.elt.core.util.ConfigKeyConstants;
import sparrow.elt.core.util.Constants;
import sparrow.elt.core.util.ContextParam;
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
public class CacheDataProvider
    implements DBDataProvider {

  protected DataProvider provider;

  protected final SparrowDataProviderConfig config;

  protected static final SparrowLogger logger = SparrowrLoggerFactory.
      getCurrentInstance(
      CacheDataProvider.class);

  private static final CacheDataSource cds = new CacheDataSource();

  protected boolean tableCreated = false;

  private String[] indexColumns = null;

  QueryObject queryObject = null;

  /**
   *
   * @param context SparrowContext
   */
  public CacheDataProvider(DataProvider provider,
                           SparrowDataProviderConfig config) {
    this.provider = provider;
    this.config = config;
    this.queryObject = new QueryObject();
    init();
  }

  /**
   * getData
   *
   * @return Object
   */
  public RecordSet getData() throws DataException {

    String columns = (queryObject.getColumns() != null) ?
        queryObject.getColumns() : "*";
    String sql = "select " + columns + " from " + getName();

    if (queryObject.getFilter() != null) {
      sql += " where " + queryObject.getFilter();
    }

   
    try {
      if (isParamOrFuncExists(sql)) {
        queryObject.setSQL(sql, false);
        QueryExecutionStrategy qes = QueryExecutionStrategy.getStrategy(getName() +
            "_CH", sql);
        qes.implementStrategy(queryObject);
        return queryWithParam(queryObject.getTransformedSQL(),
                              queryObject.getQueryParamAsArray());
      }
      else {
        return queryWithOutParam(sql);
      }

    }
    catch (Exception ex) {
      ex.printStackTrace();
      throw new DataException(ex);
    }
    finally {
      resetQueryObject();
    }
  }

  /**
   *
   */
  protected void resetQueryObject(){
    queryObject.reset();
  }

  /**
   *
   * @return DataProvider
   */
  public DataProvider getRootDataProvider() {
    return provider;
  }

  /**
   *
   * @return Object
   */
  public RecordSet getData(String columns, Map params) throws DataException {

    queryObject.setColumns(columns);
    queryObject.getQueryParamAsMap().putAll(params);
    return getData();


//    RecordSetImpl_Disconnected recSt = null;
//    columns = (columns != null) ? columns : "*";
//    Statement stmt = null;
//    ResultSet rslt = null;
//    Connection h2con = cds.getConnection();
//    try {
//      stmt = h2con.createStatement();
//      rslt = stmt.executeQuery("select " + columns + " from " +
//                               getName());
//      recSt = new RecordSetImpl_Disconnected();
//      recSt.populate(rslt, 0);
//    }
//    catch (SQLException ex) {
//      throw new DataException("SQLException occured while querying cache:" +
//                              getName(), ex);
//    }
//    catch (IOException ex) {
//      throw new DataException("IOException occured while querying cache:" +
//                              getName(), ex);
//    }
//    finally {
//      closeSQLResource(h2con, stmt, rslt);
//    }

    //return recSt;
  }

  /**
   *
   * @param con Connection
   */
  protected void closeSQLResource(Connection con, Statement stmt, ResultSet rs) {
    try {
      if (rs != null) {
        rs.close();
        rs = null;
      }
      if (stmt != null) {
        stmt.close();
        stmt = null;
      }

      if (con != null) {
        con.close();
        con = null;
      }
    }
    catch (SQLException ex) {
      logger.error("SQLException occured while closing SQL Resources", ex);

      if (con != null) {
        try {
          con.close();
        }
        catch (SQLException ex1) {
        }
        con = null;
      }

    }

  }

  /**
   * getName
   *
   * @return String
   */
  public String getName() {
    return provider.getName();
  }

  /**
   * loadData
   */
  public void loadData() throws DataException {

    RecordSet rs = provider.getData();

    try {
      if (!tableCreated) {
        createTable(rs.getColumnHeaders());
        tableCreated = true;
      }
      else {
        truncateTable();
      }
      createCache(rs);
      logger.info("Cached ["+rs.getRowCount()+"] row(s) for Data Provider ["+getName()+"]");
      rs.close();
      rs = null;
      
    }
    catch (SQLException ex) {
      throw new DataException(
          "SQLException occured while loading data into cache:" +
          getName(), ex);
    }
  }

  /**
   *
   * @throws SQLException
   */
  void truncateTable() throws DataException {
    Connection h2con = cds.getConnection();
    Statement s = null;
    try {
      s = h2con.createStatement();
      s.executeUpdate("TRUNCATE TABLE " + getName());
      s.close();
    }
    catch (SQLException ex) {
      throw new DataException("SQLException occured while truncating table:" +
                              getName(), ex);
    }
    finally {
      closeSQLResource(h2con, s, null);
    }

  }

  /**
   *
   * @param whereCondition String
   * @param param Map
   * @param columns String
   * @return RecordSet
   */
  public RecordSet applyFilter(String whereCondition, Map param, String columns) {
    RecordSetImpl_Disconnected rs = null;
    //   testCacheFile();
    whereCondition = (whereCondition != null) ? " where " + whereCondition : "";
    columns = (columns != null) ? columns : "*";

    String sql = "select " + columns + " from " + provider.getName() +
        whereCondition;

    try {

      //testCache();

      if (isParamOrFuncExists(sql)) {
        return queryWithParam(sql, param);
      }
      else {
        return queryWithOutParam(sql);
      }
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
    return rs;

  }

  /**
   * 
   * @param string
   * @return
   */
  protected boolean isParamOrFuncExists(String string){
	  return (string.indexOf("?") != -1 || string.indexOf(Constants.VARIABLE_IDENTIFIER) != -1 || string.indexOf(Constants.FUNCTION_TOKEN)!=-1);
  }
  
  /**
   *
   * @param whereCondition String
   * @param param List
   * @param columns String
   * @return RecordSet
   */
  public RecordSet applyFilter(String whereCondition, List param,
                               String columns) {
    RecordSetImpl_Disconnected rs = null;
    //   testCacheFile();
    whereCondition = (whereCondition != null) ? " where " + whereCondition : "";
    columns = (columns != null) ? columns : "*";

    String sql = "select " + columns + " from " + provider.getName() +
        whereCondition;

    try {

      //testCache();

      if (isParamOrFuncExists(sql)) {
        return queryWithParam(sql, param);
      }
      else {
        return queryWithOutParam(sql);
      }
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
    return rs;

  }

  /**
   *
   * @throws Exception
   */
  private void testCacheFile() {
    String sql = "select rowid from " + provider.getName();

    try {
      RecordSet rs = queryWithOutParam(sql);
      for (RowIterator ri = rs.iterator(); ri.hasNext(); ) {
        System.out.println(ri.next());
      }

      System.out.println(rs.getRowCount());
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * 
   * @param sql
   * @param param
   * @return
   * @throws Exception
   */
  protected RecordSet queryWithOutParam(String sql) throws Exception {
		 
	Connection h2con = cds.getConnection();
    PreparedStatement stmt = null;
    ResultSet rslt = null;
    try {
      stmt = h2con.prepareStatement(sql);
      rslt = stmt.executeQuery();
      RecordSetImpl_Disconnected rs = new RecordSetImpl_Disconnected();
      rs.populate(rslt, 0);
      return rs;
    }
    finally {
      closeSQLResource(h2con, stmt, rslt);
    }
  }

  /**
   *
   * @param q QueryObject
   * @throws Exception
   * @return RecordSet
   */
  protected RecordSet queryWithParam(String sql, Map param) throws Exception {
    QueryObject qo = new QueryObject(provider.getName());
    qo.getQueryParamAsMap().putAll(param);
    QueryExecutionStrategy qes = QueryExecutionStrategy.getStrategy(qo.getName() +
        "_CH", sql);
    qes.implementStrategy(qo);
    return queryWithParam(qo.getTransformedSQL(), qo.getQueryParamAsArray());
  }

  /**
   *
   * @param q QueryObject
   * @throws Exception
   * @return RecordSet
   */
  protected RecordSet queryWithParam(String sql, List param) throws Exception {
    Connection h2con = cds.getConnection();
    PreparedStatement stmt = null;
    ResultSet rslt = null;

    try {
      stmt = h2con.prepareStatement(sql);
      Object[] o = param.toArray();

      for (int i = 0; i < o.length; i++) {
        stmt.setObject(i + 1, o[i]);
      }

      rslt = stmt.executeQuery();
      RecordSetImpl_Disconnected rs = new RecordSetImpl_Disconnected();
      rs.populate(rslt, 0);
      return rs;
    }
    finally {
      closeSQLResource(h2con, stmt, rslt);
    }
  }

  /**
   *
   * @param col ColumnHeader
   */
  void createTable(ColumnHeader ch) throws SQLException {
    String createSQL = SparrowUtil.constructCREATE_TABLE(ch, getName());
    Connection h2con = cds.getConnection();
    Statement stmt = null;

    try {
      stmt = h2con.createStatement();
      stmt.executeUpdate("SET IGNORECASE TRUE");
      stmt.executeUpdate(createSQL);
      createIndex();
      if (logger.isDebugEnabled()) {
        logger.log("Cache Table [" + getName() + "] created.[" + createSQL +
                   "]",
                   SparrowLogger.DEBUG);
      }
    }
    finally {
      closeSQLResource(h2con, stmt, null);
    }
  }

  /**
   *
   * @throws SQLException
   */
  private void createIndex() throws SQLException {
    String createSQL = SparrowUtil.constructCREATE_INDEX(getName(), indexColumns);

    Connection h2con = cds.getConnection();
    Statement stmt = null;

    try {
      if (!createSQL.equals("")) {
        stmt = h2con.createStatement();
        stmt.executeUpdate(createSQL);
      }
    }
    finally {
      closeSQLResource(h2con, stmt, null);
    }
  }

  /**
   *
   */
  public void createCache(RecordSet rcSt) throws DataException {
    String insterSQL = SparrowUtil.constructINSERT(getName(),
                                                 rcSt.getColumnHeaders());

    if (logger.isDebugEnabled()) {
      logger.log("Adding ["+rcSt.getRowCount()+"] record(s) into Cache Table [" + getName() + "]", SparrowLogger.DEBUG);
    }

    Connection h2con = cds.getConnection();
    PreparedStatement stmt = null;
    try {
      stmt = h2con.prepareStatement(insterSQL);
      for (RowIterator ri = rcSt.iterator(); ri.hasNext(); ) {
        Object[] row = ri.next().getValues();
        for (int i = 0; i < row.length; i++) {
          stmt.setObject(i + 1, row[i]);
        }
        stmt.addBatch();
      }
      int result[] = stmt.executeBatch();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    finally {
      closeSQLResource(h2con, stmt, null);
    }

    /*    System.out.print("Result:" + result.length);
        RecordSet rs = getData();

        for (RowIterator ri = rs.iterator(); ri.hasNext(); ) {
          System.out.println(ri.next().getChunk());
        }
        System.out.println("Done");**/

  }

  /**
   *
   */
  public void destory() {
    provider.destory();
    cds.close();
  }

  /**
   * clone
   *
   * @return Object
   */
  public Object clone() throws CloneNotSupportedException {
    CacheDataProvider cp = (CacheDataProvider)super.clone();
    cp.queryObject = new QueryObject();
    cp.provider = (DataProvider) provider.clone();
    return cp;
  }

  /**
   * initialize
   */
  public void initialize() {
    provider.initialize();
    /**
         try {
      h2con = cds.getConnection();
         }
         catch (SQLException ex) {
      throw new InitializationException(
     "SQLException occured while initializing SPARROW_CACHE connection", ex);
         }
     **/
  }

  /**
   *
   */
  private void init() {
    if (provider instanceof DBDataProvider) {
      ( (DBDataProvider) provider).getQuery().setRsWrapType(
          Constants.RESULT_WRAP_DISCONNECTED);
    }
    String indxCols = config.getInitParameter().getParameterValue(
        ConfigKeyConstants.
        PARAM_CACHE_INDEX);
    indexColumns = (indxCols != null) ? indxCols.split("[,]") : new String[0];

  }

  /**
   * closeConnection
   */
  public void closeConnection() throws DataException {
    throw new DataException("Unsupported operation");
  }

  /**
   * executeBatch
   *
   * @param sql String
   * @param param List
   * @return int[]
   */
  public int[] executeBatch(String sql, List param) throws DataException {
    throw new DataException("Unsupported operation");
  }

  /**
   * executeBatch
   *
   * @param param List
   * @return int[]
   */
  public int[] executeBatch(List param) throws DataException {
    throw new DataException("Unsupported operation");
  }

  /**
   * executeQuery
   *
   * @return int
   */
  public int executeQuery() throws DataException {
    throw new DataException("Unsupported operation");
  }

  /**
   * getConnection
   *
   * @return Connection
   */
  public Connection getConnection() throws DataException {
    throw new DataException("Unsupported operation");
  }

  /**
   * getQuery
   *
   * @return QueryObject
   */
  public QueryObject getQuery() {
    return queryObject;
  }

  /**
   * setQuery
   *
   * @param query QueryObject
   */
  public void setQuery(QueryObject query) {
    this.queryObject = query;
  }

  /**
   *
   * @return DBDataProvider
   */
  private DBDataProvider getDBDataProvider() {
    return (DBDataProvider) provider;
  }

  /**
   * isCacheable
   *
   * @return boolean
   */
  public boolean isCacheable() {
    return true;
  }

  /**
   * flushData
   */
  public void flushData() throws DataException {
    truncateTable();
  }

  /**
   * executeSQLBatch
   *
   * @param param List
   * @return int[]
   */
  public int[] executeSQLBatch(List param) throws DataException {
    throw new DataException("Unsupported operation");
  }

  /**
   * setConnectionProvider
   *
   * @param connectionProvider ConnectionProvider
   */
  public void setConnectionProvider(ConnectionProvider connectionProvider) {
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
  private static class CacheDataSource {

    private BasicDataSource ds;
    private boolean closed = false;
    private static String poolSize = ContextParam.getContextParamValue(
        "sparrow.cache.pool.size");

    private static final int H2_POOL_SIZE = (poolSize == null ||
                                             Integer.parseInt(poolSize) < 1) ?
        25 :
        Integer.parseInt(poolSize);

    /**
     *
     */
    CacheDataSource() {
      init();
    }

    /**
     *
     */
    private void init() {
      BasicDataSource bds = new BasicDataSource();
      bds.setDriverClassName("org.h2.Driver");
      bds.setUrl("jdbc:h2:mem:sparrow;DB_CLOSE_ON_EXIT=FALSE");
//      bds.setUrl("jdbc:h2:tcp://localhost/~/test");
//      bds.setUsername("sparrow");
//      bds.setPassword("sparrow");
      bds.setMaxActive(H2_POOL_SIZE);
      bds.setMaxWait(3000);
      bds.setDefaultAutoCommit(true);
      this.ds = bds;
      Connection c = getConnection();
      try {
        logger.info("Initializing CACHE ENGINE on [" +
                    c.getMetaData().getDatabaseProductName() +
                    " Version:" + c.getMetaData().getDatabaseProductVersion() +
                    "]");
        c.close();
        c = null;
      }
      catch (SQLException ex) {
      }
    }

    /**
     *
     */
    void close() {
      try {
        if (!closed) {
          ds.close();
          closed = true;
        }

      }
      catch (SQLException ex) {
        ex.printStackTrace();
      }
    }

    /**
     *
     * @throws SQLException
     * @return Connection
     */
    Connection getConnection() {

      if (!closed) {
        try {
          return ds.getConnection();
        }
        catch (SQLException ex) {
          return null;
        }
      }
      return null;
    }
  }

}
