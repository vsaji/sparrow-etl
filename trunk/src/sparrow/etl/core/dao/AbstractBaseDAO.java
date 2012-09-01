package sparrow.etl.core.dao;

import java.sql.BatchUpdateException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import java.sql.Types;

import sparrow.etl.core.dao.dialect.DBDialect;
import sparrow.etl.core.dao.impl.QueryObject;
import sparrow.etl.core.dao.impl.RecordSet;
import sparrow.etl.core.dao.impl.RecordSetImpl_Connected;
import sparrow.etl.core.dao.impl.RecordSetImpl_Disconnected;
import sparrow.etl.core.dao.util.DBUtil;
import sparrow.etl.core.dao.util.SPParamInfo;
import sparrow.etl.core.exception.DataException;
import sparrow.etl.core.log.SparrowLogger;
import sparrow.etl.core.log.SparrowrLoggerFactory;

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */
public abstract class AbstractBaseDAO {

  /**
   *
   * @param connectionName String
   * @return Connection
   */
  protected abstract Connection openConnection() throws DataException;

  private int retryCount = 0;
  private static final int RETRY_COUNT = 3;
  private static final String QUERY_STR = "?";
  protected static final String KEY_SP_OUT = "sp.out";
  protected static final String VALUE_SP_OUT = "$sp.out$";

  /**
   * Holds references to Logger object
   */
  private static final SparrowLogger logger = SparrowrLoggerFactory.
      getCurrentInstance(
      AbstractBaseDAO.class);

  /**
   *
   * @param sql String
   * @param params Object[]
   * @throws DataException
   * @return int
   */
  protected int insert(String sql, Object[] params) throws DataException {

    if (logger.isDebugEnabled()) {
      logger.log("Entry insert('" + sql.substring(0, sql.length() / 2) + "','" +
                 (params != null ? Arrays.asList(params).toString() : "[]")
                 + "'", logger.DEBUG);
    }

    Connection conn = null;
    int rowsInserted = 0;

    try {

      if (sql == null || "".equals(sql)) {
        IllegalArgumentException sysError = new IllegalArgumentException(
            "Argument sql is null or empty");
        throw sysError;
      }

      if (params == null) {
        IllegalArgumentException sysError = new IllegalArgumentException(
            "Argument params is null or empty");
        throw sysError;
      }

      conn = openConnection();

      if (conn == null) {
        DataException dbException = new DataException("Connection is null");
        throw dbException;
      }

      rowsInserted = this.executeQuery(sql, params, conn);

      if (rowsInserted == 0) {
        DataException dataError = new DataException(
            "No Rows Inserted due to concurrency failure");
        throw dataError;
      }

      if (logger.isDebugEnabled()) {
        logger.log("Insert Method Exiting" + rowsInserted,
                   logger.DEBUG);
      }
    }
    catch (DataException dataExcep) {
      throw dataExcep;
    }
    catch (Exception error) {
      DataException sysError = new DataException(
          "Exception while inserting row(s)", error);
      throw sysError;
    }
    finally {
      if (conn != null) {
        closeConnection(conn);
      }

    }
    return rowsInserted;
  }

  /**
   *
   * @param sql String
   * @param params Object[]
   * @throws DataException
   * @return int
   */
  protected int update(String sql, Object[] params) throws DataException {

    if (logger.isDebugEnabled()) {
      logger.log("Entry update('" + sql.substring(0, sql.length() / 2) + "', '" +
                 (params != null ? Arrays.asList(params).toString() : "[]")
                 + "')", logger.DEBUG);
    }

    Connection conn = null;
    int rowsUpdated = 0;

    try {
      if (sql == null || "".equals(sql)) {
        IllegalArgumentException sysError = new IllegalArgumentException(
            "Argument sql is null or empty");
        throw sysError;
      }

      conn = openConnection();

      if (conn == null) {
        DataException dbException = new DataException("Connection is null");
        throw dbException;
      }

      rowsUpdated = this.executeQuery(sql, params, conn);

    }
    catch (DataException dataExcep) {
      throw dataExcep;
    }
    catch (Exception error) {
      DataException sysError = new DataException(
          "Exception while inserting row(s)", error);
      throw sysError;
    }

    finally {
      if (conn != null) {
        closeConnection(conn);
      }
    }
    return rowsUpdated;
  }

  /**
   *
   * @param sql String
   * @param params Object[]
   * @throws DataException
   * @return int
   */
  protected int delete(String sql, Object[] params) throws DataException {

    if (logger.isDebugEnabled()) {
      logger.log("Entry delete('" + sql.substring(0, sql.length() / 2) + "', '" +
                 (params != null ? Arrays.asList(params).toString() : "[]")
                 + "')", logger.DEBUG);
    }

    Connection conn = null;
    int rowsDeleted = 0;

    try {
      if (sql == null || "".equals(sql)) {
        IllegalArgumentException sysError = new IllegalArgumentException(
            "Argument sql is null or empty");
        throw sysError;
      }

      if (params == null) {
        IllegalArgumentException sysError = new IllegalArgumentException(
            "Argument params is null or empty");
        throw sysError;
      }

      conn = openConnection();

      if (conn == null) {
        DataException dbException = new DataException("Connection is null");
        throw dbException;
      }

      rowsDeleted = this.executeQuery(sql, params, conn);

      if (rowsDeleted == 0) {
        DataException dataError = new DataException("No rows Deleted");
        throw dataError;
      }

      if (logger.isDebugEnabled()) {
        logger.log("Exiting Delete No of Rows Deleted" + rowsDeleted,
                   logger.DEBUG);
      }
    }
    catch (DataException dataExcep) {
      throw dataExcep;
    }
    catch (Exception excep) {
      DataException sysError = new DataException(
          "Exception while inserting row(s)", excep);
      throw sysError;
    }
    finally {
      if (conn != null) {
        closeConnection(conn);
      }
    }
    return rowsDeleted;
  }

  /**
   *
   * @param sql String
   * @param params Object[]
   * @param fetchSize int
   * @throws DataException
   * @return RecordSet
   */
  protected RecordSet search() throws
      DataException {

    QueryObject qo = getQuery();
    String sql = qo.getTransformedSQL();
    Object[] params = qo.getQueryParamAsArray().toArray();
    int fetchSize = qo.getFetchSize();
    boolean storeProc = qo.isStoreProc();

    if (logger.isDebugEnabled()) {
      logger.log("Entry search('" + sql + "', '" +
                 (params != null ? Arrays.asList(params).toString() : "[]")
                 + "')", logger.DEBUG);
    }

    Connection conn = null;
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    RecordSetImpl_Disconnected result = null;

    try {
      if (sql == null || "".equals(sql)) {
        IllegalArgumentException sysError = new IllegalArgumentException(
            "Argument sql is null or empty");
        throw sysError;
      }

      conn = openConnection();

      //logger.debug("Connection Created : "+conn);

      if (conn == null) {
        DataException dbException = new DataException("Connection is null");
        dbException.getExceptionHandler().setFatalOnly();
      }

      if (fetchSize != 0 && !storeProc) {
        sql = DBDialect.getDBDialect(conn.getMetaData().getURL()).
            handleRowLimit(sql, fetchSize, conn);
      }

      if (qo.getUseDB() != null) {
        DBDialect.getDBDialect(conn.getMetaData().getURL()).useDB(qo.getUseDB(),
            conn);
      }

      pstmt = (storeProc) ?
          conn.prepareCall("{ call " + sql + " }") :
          conn.prepareStatement(sql);

          
      if (params != null && params.length > 0) {
        	pstmt = populateQuery(params, pstmt);
      }

      pstmt.execute();
      rs = pstmt.getResultSet();
      result = new RecordSetImpl_Disconnected();
      result.populate(rs, fetchSize);
    }
    catch (SQLException sqlError) {

      DataException sysError = new DataException(
          "SQLException occured while searching data", sqlError);
      throw sysError;
    }
    catch (Exception ne) {
      DataException sysError = new DataException(
          "Exception while searching data", ne);
      throw sysError;
    }
    finally {
      if (rs != null) {
        try {
          rs.close();
        }
        catch (Exception rsError) {
          logger.error(rsError.getMessage(), rsError);
        }
      }
      if (pstmt != null) {
        try {
          pstmt.close();
        }
        catch (Exception stError) {
          logger.error(stError.toString(), stError);
        }
      }
      if (conn != null) {
        closeConnection(conn);
      }
    }
    return result;
  }

  /**
   *
   * @param sql String
   * @param params Object[]
   * @param fetchSize int
   * @throws DataException
   * @return RecordSet
   */
  protected RecordSet searchDisConnUsingStatement(String sql, int fetchSize) throws
      DataException {

    if (logger.isDebugEnabled()) {
      logger.log("Entry searchDisConnUsingStatement('" +
                 sql.substring(0, sql.length() / 2) + "')",
                 logger.DEBUG);
    }

    Connection conn = null;
    Statement stmt = null;
    ResultSet rs = null;
    RecordSetImpl_Disconnected result = null;

    try {
      if (sql == null || "".equals(sql)) {
        IllegalArgumentException sysError = new IllegalArgumentException(
            "Argument sql is null or empty");
        throw sysError;
      }

      conn = openConnection();

      if (conn == null) {
        DataException dbException = new DataException("Connection is null");
        dbException.getExceptionHandler().setFatalOnly();
      }

      stmt = conn.createStatement();
      rs = stmt.executeQuery(sql);
      result = new RecordSetImpl_Disconnected();
      result.populate(rs, fetchSize);

    }
    catch (SQLException sqlError) {

      DataException sysError = new DataException(
          "SQLException occured while searching data", sqlError);
      throw sysError;
    }
    catch (Exception ne) {
      DataException sysError = new DataException(
          "Exception while searching data", ne);
      throw sysError;
    }
    finally {
      if (rs != null) {
        try {
          rs.close();
        }
        catch (Exception rsError) {
          logger.error(rsError.getMessage(), rsError);
        }
      }
      if (stmt != null) {
        try {
          stmt.close();
        }
        catch (Exception stError) {
          logger.error(stError.toString(), stError);
        }
      }
      if (conn != null) {
        closeConnection(conn);
      }
      retryCount = 0;
    }
    return result;

  }

  /**
   *
   * @param sql String
   * @param params Object[]
   * @param fetchSize int
   * @throws DataException
   * @return RecordSet
   */
  protected RecordSet searchConnectedUsingStatement(String sql, int fetchSize) throws
      DataException {

    if (logger.isDebugEnabled()) {
      logger.log("Entry searchUsingStatement('" +
                 sql.substring(0, sql.length() / 2) + "')", logger.DEBUG);
    }

    Connection conn = null;
    Statement stmt = null;
    ResultSet rs = null;
    RecordSetImpl_Connected result = null;

    try {
      if (sql == null || "".equals(sql)) {
        IllegalArgumentException sysError = new IllegalArgumentException(
            "Argument sql is null or empty");
        throw sysError;
      }

      conn = openConnection();

      if (conn == null) {
        DataException dbException = new DataException("Connection is null");
        dbException.getExceptionHandler().setFatalOnly();
      }

      stmt = conn.createStatement();
      rs = stmt.executeQuery(sql);
      result = new RecordSetImpl_Connected(rs, stmt, conn, fetchSize);

    }
    catch (SQLException sqlError) {

      DataException sysError = new DataException(
          "SQLException occured while searching data", sqlError);
      throw sysError;
    }
    catch (Exception ne) {
      DataException sysError = new DataException(
          "Exception while searching data", ne);
      throw sysError;
    }
    return result;

  }

  /**
   *
   * @param sql String
   * @param params Object[]
   * @param fetchSize int
   * @throws DataException
   * @return RecordSet
   */
  protected RecordSet searchForRSWrap() throws
      DataException {

    QueryObject qo = getQuery();
    String sql = qo.getTransformedSQL();
    Object[] params = qo.getQueryParamAsArray().toArray();
    int fetchSize = qo.getFetchSize();
    boolean storeProc = qo.isStoreProc();

    if (logger.isDebugEnabled()) {
      logger.log("Entry searchForRSWrap('" + sql.substring(0, sql.length() / 2) +
                 "', '" +
                 (params != null ? Arrays.asList(params).toString() : "[]")
                 + "')", logger.DEBUG);
    }

    Connection conn = null;
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    RecordSetImpl_Connected result = null;

    try {
      if (sql == null || "".equals(sql)) {
        IllegalArgumentException sysError = new IllegalArgumentException(
            "Argument sql is null or empty");
        throw sysError;
      }

      conn = openConnection();
      //logger.debug("Connected : "+conn);
      if (conn == null) {
        DataException dbException = new DataException("Connection is null");
        dbException.getExceptionHandler().setFatalOnly();
      }

//      pstmt = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE,
//                                    ResultSet.CONCUR_READ_ONLY);

      if (fetchSize != 0 && !storeProc) {
        sql = DBDialect.getDBDialect(conn.getMetaData().getURL()).
            handleRowLimit(sql, fetchSize, conn);
      }

      pstmt = (storeProc) ?
          conn.prepareCall("{ call " + sql + " }",
                           ResultSet.TYPE_SCROLL_SENSITIVE,
                           ResultSet.CONCUR_READ_ONLY) :
          conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE,
                                ResultSet.CONCUR_READ_ONLY);

      if (sql.indexOf(QUERY_STR) != -1) {
        if (params == null || params.length < 1) {
          IllegalArgumentException sysError = new IllegalArgumentException(
              "Argument params is null or empty");
          throw sysError;
        }
        pstmt = populateQuery(params, pstmt);
      }

      //logger.debug("Before Execute : "+pstmt);
      //pstmt.setFetchSize(5000);
      pstmt.execute();
      rs = pstmt.getResultSet();
      result = new RecordSetImpl_Connected(rs, pstmt, conn, fetchSize);
      logger.debug("After Execute : "+pstmt);
    }
    catch (SQLException sqlError) {

      DataException sysError = new DataException(
          "SQLException occured while searching data", sqlError);
      throw sysError;
    }
    catch (Exception ne) {
      DataException sysError = new DataException(
          "Exception while searching data", ne);
      throw sysError;
    }
    return result;

  }

  /**
   *
   * @param sql String
   * @param params Object[][]
   * @throws DataException
   * @return int[]
   */
  protected int[] executeBatch(String sql, List params, boolean storeProc) throws
      DataException {

    int[] batchUpdatedResults = null;
    Connection conn = null;
    PreparedStatement prepStmt = null;

    try {

      if (sql == null || "".equals(sql)) {
        IllegalArgumentException sysError = new IllegalArgumentException(
            "Argument sql is null or empty");
        throw sysError;
      }

      conn = openConnection();

      if (conn == null) {
        DataException dbException = new DataException("Connection is null");
        throw dbException;
      }

      prepStmt = (storeProc) ? conn.prepareCall("{ call " + sql + " }") :
          conn.prepareStatement(sql);

      for (Iterator iter = params.iterator(); iter.hasNext(); ) {
        ArrayList item = (ArrayList) iter.next();
        Object[] sqlParams = item.toArray();
        this.populateQuery(sqlParams, prepStmt);
        prepStmt.addBatch();
      }

      batchUpdatedResults = prepStmt.executeBatch();

      Arrays.sort(batchUpdatedResults);
      int i = Arrays.binarySearch(batchUpdatedResults,
                                  PreparedStatement.EXECUTE_FAILED);

      prepStmt.clearBatch();

      if (i >= 0) {
        DataException dataError = new DataException("No row(s) updated");
        throw dataError;
      }

    }
    catch (DataException de) {
      throw de;
    }
    catch (BatchUpdateException batchErr) {
      DataException sysError = new DataException(
          "BatchUpdateException occured while updating data", batchErr);
      try {
        prepStmt.clearBatch();
      }
      catch (Exception sqlErr) {
        logger.error(
            "BatchUpdateException:Exception occured while clearing bacth " +
            sqlErr.getMessage(), sqlErr);
      }
      throw sysError;
    }
    catch (SQLException sqlError) {
      DataException sysError = new DataException(
          "SQLException occured while updating data", sqlError);
      try {
        prepStmt.clearBatch();
      }
      catch (Exception sqlErr) {
        logger.error(sqlErr.getMessage(), sqlErr);
      }
      throw sysError;
    }
    catch (Exception error) {
      DataException sysError = new DataException(
          "SQLException:Exception: occured while clearing batch", error);
      try {
        prepStmt.clearBatch();
      }
      catch (Exception sqlErr) {
        logger.error(sqlErr.getMessage(), sqlErr);
      }
      throw sysError;
    }
    finally {
      try {
        if (prepStmt != null) {
          prepStmt.close();
        }
        if (conn != null) {
          this.closeConnection(conn);
        }
      }
      catch (Exception sysErr) {
        logger.error(sysErr.toString(), sysErr);
      }
    }

    if (logger.isDebugEnabled()) {
      logger.log("UpdateBatch Exiting " + batchUpdatedResults.toString(),
                 logger.DEBUG);
    }
    return batchUpdatedResults;
  }

  /**
   *
   * @param sql String
   * @param params Object[][]
   * @throws DataException
   * @return int[]
   */
  protected int[] executeBatch(List sqls) throws
      DataException {

    int[] batchUpdatedResults = null;
    Connection conn = null;
    Statement stmt = null;

    try {

      conn = openConnection();

      if (conn == null) {
        DataException dbException = new DataException("Connection is null");
        throw dbException;
      }

      stmt = conn.createStatement();

      String[] sqlArr = (String[]) sqls.toArray(new String[sqls.size()]);

      for (int i = 0; i < sqlArr.length; i++) {
        stmt.addBatch(sqlArr[i]);
      }

      batchUpdatedResults = stmt.executeBatch();

      Arrays.sort(batchUpdatedResults);
      int i = Arrays.binarySearch(batchUpdatedResults,
                                  PreparedStatement.EXECUTE_FAILED);

      stmt.clearBatch();

      if (i >= 0) {
        DataException dataError = new DataException("No row(s) updated");
        throw dataError;
      }

    }
    catch (DataException de) {
      throw de;
    }
    catch (BatchUpdateException batchErr) {
      DataException sysError = new DataException(
          "BatchUpdateException occured while updating data", batchErr);
      try {
        stmt.clearBatch();
      }
      catch (Exception sqlErr) {
        logger.error(
            "BatchUpdateException:Exception occured while clearing bacth " +
            sqlErr.getMessage(), sqlErr);
      }
      throw sysError;
    }
    catch (SQLException sqlError) {
      DataException sysError = new DataException(
          "SQLException occured while updating data", sqlError);
      try {
        stmt.clearBatch();
      }
      catch (Exception sqlErr) {
        logger.error(sqlErr.getMessage(), sqlErr);
      }
      throw sysError;
    }
    catch (Exception error) {
      DataException sysError = new DataException(
          "SQLException:Exception: occured while clearing batch", error);
      try {
        stmt.clearBatch();
      }
      catch (Exception sqlErr) {
        logger.error(sqlErr.getMessage(), sqlErr);
      }
      throw sysError;
    }
    finally {
      try {
        if (stmt != null) {
          stmt.close();
        }
        if (conn != null) {
          this.closeConnection(conn);
        }
      }
      catch (Exception sysErr) {
        logger.error(sysErr.toString(), sysErr);
      }
    }

    if (logger.isDebugEnabled()) {
      logger.log("UpdateBatch Exiting " + batchUpdatedResults.toString(),
                 logger.DEBUG);
    }
    return batchUpdatedResults;
  }

  /**
   * Purpose: Tries to open a connection to an existing pre-defined datasource
   *  and returns the connection if able to open.
   * If unable to open, throws a SystemException.
   * At no stage this method returns a null connection object.
   *
   * Alternate Paths:
   * 1. If unable to find the JNDI lookup a SystemException is thrown.
   * 2. If unable to create the connection successfully, a SystemException
   *  is thrown.
   *
   * @throws SystemException
   * @return Connection

     protected Connection openConnection() throws SystemException {

    if (logger.isDebugEnabled()) {
      logger.log("Entry openConnection", logger.DEBUG);
    }

    Connection conn = null;

    try {

   DataSource ds = (DataSource) ServiceLocator.getInstance().getDataSource(
          inspConfig.getProperty("common.jndi.database.dataSourceName"));

      if (ds != null) {
        conn = ds.getConnection();
        // DO NOT MAKE THESE AS INFO : IN PROD : PERFORMANCE PROB.
        if (logger.isDebugEnabled()) {
          logger.log("After getting Connection", logger.DEBUG);
        }
        conn.setAutoCommit(false);
      }
    }
    catch (SQLException sqle) {
      SystemException sysError = new SystemException(
          "common.exceptions.SQL_EXCEPTION_OPEN_CONN", sqle);

      boolean isFatal = isExceptionFatal(sqle.getErrorCode());

      if (isFatal) {
        sysError.setFatal(isFatal);
      }
      throw sysError;
    }
    catch (SystemException sysException) {
      throw sysException;
    }
    catch (Exception e) {
      SystemException sysError = new SystemException(
          "common.exceptions.EXCEPTION_OPEN_CONN", e);
      throw sysError;
    }
    if (logger.isDebugEnabled()) {
      logger.log("Exiting Method OpenConnection " + conn, logger.DEBUG);
    }
    return conn;
     }
   */

  /**
   * Purpose: This private method attempts to close an existing connection
   * to a datasource.
   * This method does NOT attempt to close related objects like prepared
   * statement, or statement.
   *
   * Alternate Paths:
   * 1. if input connection is null, throws a new SystemException.
   * 2. if connection close has a failure during closing, method throws a
   * new SystemException.
   * 3. if connection is NOT null, and can be closed successfully, nothing
   * is returned.
   * @param conn Connection
   * @throws SystemException
   */
  protected void closeConnection(Connection conn) throws
      DataException {

    if (logger.isDebugEnabled()) {
      logger.log("Closing DB connection", logger.DEBUG);
    }

    if (conn != null) {
      try {
        conn.close();
      }
      catch (Exception exception) {
        DataException sysError = new DataException(
            "Exception occured while closing connection:" + conn, exception);
        throw sysError;
      }
    }
  }

  /**
   * Purpose: To populate a prepared statement with required params with order
   * according to
   * array objects.
   * (first param replaced by first element in array, second param replaced
   * by second element in array).
   * Since the goal of this method is to populate a prepared statement, it
   *  validates whether the SQL is NOT null and
   * not empty.
   *
   * It also checks whether params array is empty or NULL. If empty or null,
   * it throws back an exception to caller.
   *
   * @param sql String
   * @param params Object[] If this is null or empty, then a DataException
   * would be thrown to the caller.
   * @param pstmt PreparedStatement: Cannot be null.
   * @throws SQLException
   * @throws DataException
   * @return PreparedStatement: Guaranteed to be a valid reference ( NOT null).
   * Independent validations
   * 1. If sql is NULL or empty:- throw a systemexception indicating reqd.
   * input param null.
   * 2. if params is NULL or empty:- throw a systemexception indicating reqd.
   * input param null.
   * 3. if pstmt is NULL or empty:- throw a systemexception indicating reqd.
   * input param null.
   *
   * Joint validations
   * None as each param is required
   */
  protected PreparedStatement populateQuery(Object[] params,
                                            PreparedStatement pstmt) throws

      DataException {

	  /**
	   * 
	   */
    if (pstmt == null) {
      logger.warn("["+getQuery().getName()+"] PreparedStatement argument array is null");
      return pstmt;
    }

    int paramsLength = (params != null) ? params.length : 0;

    try {
      for (int i = 0; i < paramsLength; i++) {
        if (params[i] == null) {
          pstmt.setNull(i + 1, Types.VARCHAR);
          continue;
        }

        if (params[i] instanceof java.util.Date) {
          params[i] = new java.sql.Date( ( (java.util.Date) params[i]).getTime());
        }

        pstmt.setObject(i + 1, params[i]);
      }
    }
    catch (SQLException ae) {
      DataException sysError = new DataException(
          "SQLException occured while populating query", ae);
      throw sysError;
    }
    catch (Exception excep) {
      DataException sysError = new DataException(
          "Exception occured while populating query", excep);
      throw sysError;
    }
    return pstmt;
  }

  /**
   * Executes a stored procedure given its name and parameters.
   * @param spName: Stored Procedure Name.
   * @param params: Parameters as two-dimensional arrays detailing the
   * @throws SystemException, DataException
   * @return nothing.
   * This method expects a stored procedure name and a list of params.
   * Note that the size of params array indicates the number of params fired
   * against the DB.
   * So, if the array size is 10, then the callableStatement.setObject(n,obj)
   * will be called
   * 10 times.
   * Caller should make sure that the specified procedure name expects the
   * specified number of in params.
   *
   * if the stored procedure takes no inputs, then caller should send in an
   * EMPTY array.
   * Sending a null for array will result in an exception being thrown before
   * execution is attempted.
   * Assumptions:
   * 1. Will not return any output (no resultset, no out params)
   * 2. Will not identify Types.xyz etc.
   * 3. Caller needs to make sure that the spName contains '?' which
   * exactly match the size of array.
   * For instance if spName contains 3 '?', then the params array size
   * should be 3.
   * 4. This method would NOT attempt to identify the type of param.
   * It will do a setObject.
   * 5. No element in the params array is null.
   */
  protected RecordSet executeStoredProc(SPParamInfo spParam,
                                        boolean isSQLFunction) throws
      DataException {

    RecordSetImpl_Disconnected rs = new RecordSetImpl_Disconnected();
    int len = spParam.getParamLen();
    Object[] row = new Object[len];
    Connection con = null;
    CallableStatement stmt = null;
    QueryObject query = getQuery();
    String sql = "";
    try {

      if (logger.isDebugEnabled()) {
        logger.log("Entry getData('"
                   + query.getTransformedSQL()
                   + "', '"
                   + (query.getQueryParamAsArray() != null ? query
                      .getQueryParamAsArray().toString() : "[]")
                   + "')", logger.DEBUG);
      }

      sql = (isSQLFunction) ? query.getTransformedSQL() : " call "
          + query.getTransformedSQL();

      con = openConnection();
      stmt = con.prepareCall("{ " + sql + " }");
      populateQuery(query.getQueryParamAsArray().toArray(), stmt);

      int[] pos = spParam.getPos();
      int[] type = spParam.getType();

      /***************************************************************
       * Register out parameter
       */

      for (int i = 0; i < len; i++) {
        stmt.registerOutParameter(pos[i], type[i]);
      }

      stmt.execute();

      for (int i = 0; i < len; i++) {
        row[i] = getValue(type[i], pos[i], stmt);
      }

      rs.addRow(spParam.getColumnHeader(), row);

    }
    catch (Exception ex) {
      ex.printStackTrace();
      throw new DataException(
          "executeStoredProc:Exception occured while executing StoreProcedure[" +
          query.getSQL() + "]", ex);
    }
    finally {
      try {
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
        logger.error(
            "SQLException occured while closing stmt and con",
            ex);
      }
    }
    return rs;
  }

  /**
   *
   * @param params
   *            Object[]
   * @param pstmt
   *            PreparedStatement
   * @throws DataException
   * @return PreparedStatement
   */
  private void populateQuery(Object[] params, CallableStatement pstmt) throws

      DataException {

    if (pstmt == null) {
      DataException sysError = new DataException(
          "Argument PreparedStatement-pstmt is null");
      throw sysError;
    }

    int paramsLength = (params != null) ? params.length : 0;

    try {
      for (int i = 0; i < paramsLength; i++) {

        if (params[i] == null) {
          pstmt.setNull(i + 1, Types.VARCHAR);
          continue;
        }
        if (VALUE_SP_OUT.equals(params[i])) {
          continue;
        }
        pstmt.setObject(i + 1, params[i]);
      }
    }
    catch (SQLException ae) {
      DataException sysError = new DataException(
          "SQLException occured while populating query", ae);
      throw sysError;
    }
    catch (Exception excep) {
      DataException sysError = new DataException(
          "Exception occured while populating query", excep);
      throw sysError;
    }

  }

  /**
   * Generates an unique sequence and this is used for maintaining
   * integrity of a database.
   * This unique number is another way of maintaining concurrency.
   * @throws SystemException
   * @throws DataException
   * @return SequenceNumber long.
   */
  protected long generateSequence(Connection conn, String seqName) throws
      DataException {

    if (logger.isDebugEnabled()) {
      logger.log("generateSequence Entry " + seqName, logger.DEBUG);
    }
    if (seqName == null || seqName == "") {
      DataException sysError = new DataException(
          "Argument seqName is null");
      throw sysError;
    }

    Statement stmt = null;
    ResultSet rs = null;
    long seqNumber = 0000;

    try {

      stmt = conn.createStatement();
      rs = stmt.executeQuery("Select " + seqName +
                             ".nextval seqId from dual ");
      rs.next();

      if (rs.getString("seqId") != null) {
        seqNumber = Long.parseLong(rs.getString("seqId"));
      }

    }
    catch (SQLException se) {
      DataException sysError = new DataException(
          "SQLException occured while getting Sequence number:" + seqName, se);
      throw sysError;
    }
    catch (Exception e) {
      DataException sysError = new DataException(
          "Exception occured while getting Sequence number:" + seqName, e);
      throw sysError;
    }
    finally {
      if (rs != null) {
        try {
          rs.close();

        }
        catch (Exception e) {
          logger.log(e.toString(), logger.WARN, e);
        }
      }
      if (stmt != null) {

        try {
          stmt.close();

        }
        catch (Exception e) {
          logger.log(e.toString(), logger.WARN, e);
        }

      }
    }
    if (logger.isDebugEnabled()) {
      logger.log("generateSequence Exiting " + seqNumber, logger.DEBUG);
    }
    return seqNumber;
  }

  /**
   * Generates an unique sequence and this is used for maintaining
   * integrity of a database.
   * This unique number is another way of maintaining concurrency.
   * @throws SystemException
   * @throws DataException
   * @return SequenceNumber long.
   */
  protected long generateSequence(String seqName) throws DataException {

    if (logger.isDebugEnabled()) {
      logger.log("generateSequence Entry " + seqName, logger.DEBUG);
    }
    if (seqName == null || seqName == "") {
      DataException sysError = new DataException(
          "Argument seqName is null");
      throw sysError;
    }
    Connection conn = null;
    Statement stmt = null;
    ResultSet rs = null;
    long seqNumber = 0000;

    try {
      conn = this.openConnection();
      stmt = conn.createStatement();
      rs = stmt.executeQuery("Select " + seqName +
                             ".nextval seqId from dual ");
      rs.next();

      if (rs.getString("seqId") != null) {
        seqNumber = Long.parseLong(rs.getString("seqId"));
      }

    }
    catch (SQLException se) {
      DataException sysError = new DataException(
          "SQLException occured while getting Sequence number:" + seqName, se);
      throw sysError;
    }
    catch (Exception e) {
      DataException sysError = new DataException(
          "Exception occured while getting Sequence number:" + seqName, e);
      throw sysError;
    }

    finally {
      if (rs != null) {
        try {
          rs.close();

        }
        catch (Exception e) {
          logger.log(e.toString(), logger.WARN, e);
        }
      }
      if (stmt != null) {

        try {
          stmt.close();

        }
        catch (Exception e) {
          logger.log(e.toString(), logger.WARN, e);
        }

      }
      if (conn != null) {
        closeConnection(conn);
      }

    }
    if (logger.isDebugEnabled()) {
      logger.log("generateSequence Exiting " + seqNumber, logger.DEBUG);
    }
    return seqNumber;
  }

  /**
   * This private will merge the SQL and Object array param and return
   * the fully
   * prepared SQL for logging purposes.
   * @param String Query - SQL
   * @param Object[] Parameters
   * @return String.
   */
  protected String mergeQueryAndParameters(String query, Object[] parameters) {

    if (logger.isDebugEnabled()) {
      logger.log("mergeQueryAndParameters Entry ", logger.DEBUG);
    }

    StringBuffer strMergedPlaceHolder = new StringBuffer(query);
    int count = 0;
    for (int i = 0; i < strMergedPlaceHolder.length(); i++) {
      if (strMergedPlaceHolder.charAt(i) == '?') {
        String strParameter = "";
        int j = count++;
        if (parameters[j] != null) {
          strParameter = parameters[j].toString();
        }
        if (parameters[j] == null) {
          strParameter = "null";
        }
        if (strParameter instanceof java.lang.String &&
            strParameter != "null") {
          strParameter = "\'" + strParameter + "\'";
        }
        strMergedPlaceHolder.replace(i, i + 1, strParameter);
      }
    }

    String sql = strMergedPlaceHolder.toString();

    if (logger.isDebugEnabled()) {
      logger.log("mergeQueryAndParmeters Exiting " + sql, logger.DEBUG);
    }
    return sql;
  }

  /**
   * <P>Description:
   * This method is used to call the common code for insert,delete and update
   * methods.
   * </p>
   * @param query String Parameterised  sql
   * @param parameters Object[] Object Array of Place holder parameters.
   * @param conn Connection
   * @return  the <code>int</code>  result of execution of query.
   * @throws SystemException .
   */
  private int executeQuery(String sql, Object[] params, Connection conn) throws
      DataException {

    int result = 0;
    PreparedStatement pstmt = null;

    try {

      pstmt = conn.prepareStatement(sql);

      pstmt = populateQuery(params, pstmt);

      result = pstmt.executeUpdate();

    }
    catch (SQLException sqle) {
      DataException sysError = new DataException(
          "SQLException occured while executing Query", sqle);
      throw sysError;
    }
    catch (Exception e) {
      DataException sysError = new DataException(
          "Exception occured while executing Query", e);
      throw sysError;
    }
    finally {
      try {
        if (pstmt != null) {
          pstmt.close();
        }
      }
      catch (Exception ex) {
        logger.error("executeQuery method The statement object is not closed",
                     ex);
      }
    }
    return result;
  }

  /**
   * <P>Description:
   * This method is used to call the common code for insert,delete and update
   * methods.
   * </p>
   * @param query String Parameterised  sql
   * @param parameters Object[] Object Array of Place holder parameters.
   * @param conn Connection
   * @return  the <code>int</code>  result of execution of query.
   * @throws SystemException .
   */
  protected int executeQry(String sql, Object[] params, Connection conn,
                           boolean isStoreProc) throws
      DataException {

    if (logger.isDebugEnabled()) {
      logger.log("Entry executeQry('" + sql.substring(0, sql.length() / 2) +
                 "...', '" +
                 (params != null ? Arrays.asList(params).toString() : "[]")
                 + "')", logger.DEBUG);
    }

    int result = 0;
    PreparedStatement pstmt = null;

    try {

      pstmt = (isStoreProc) ? conn.prepareCall("{ call " + sql + " }") :
          conn.prepareStatement(sql);

      pstmt = populateQuery(params, pstmt);

      result = pstmt.executeUpdate();

    }
    catch (SQLException sqle) {
      DataException sysError = new DataException(
          "SQLException occured while executing Query", sqle);
      throw sysError;
    }
    catch (Exception e) {
      DataException sysError = new DataException(
          "Exception occured while executing Query", e);
      throw sysError;
    }
    finally {
      try {
        if (pstmt != null) {
          pstmt.close();
        }
        if (conn != null) {
          closeConnection(conn);
        }

      }
      catch (Exception ex) {
        logger.error("executeQuery method The statement object is not closed",
                     ex);
      }
    }
    return result;
  }

  /**
   *
   * @return
   */
  public abstract QueryObject getQuery();

  /**
   *
   * @param dataType
   *            int
   * @param stmt
   *            CallableStatement
   * @return Object
   */
  private Object getValue(int dataType, int pos, CallableStatement stmt) throws
      SQLException {

    switch (dataType) {
      case Types.VARCHAR:
      case Types.CHAR:
      case Types.OTHER:
        return stmt.getString(pos);
      case Types.DATE:
      case Types.TIMESTAMP:
        return stmt.getTimestamp(pos);
      case Types.INTEGER:
      case Types.NUMERIC:
      case Types.SMALLINT:
      case Types.TINYINT:
        return new Integer(stmt.getInt(pos));
      case Types.BIGINT:
        return new Long(stmt.getLong(pos));
      case Types.DOUBLE:
        return new Double(stmt.getDouble(pos));
      case Types.DECIMAL:
      case Types.FLOAT:
        return new Float(stmt.getFloat(pos));
      case Types.JAVA_OBJECT:
        return stmt.getObject(pos);
      case Types.BLOB:
      case Types.VARBINARY:
      case Types.BINARY:
      case Types.LONGVARBINARY:
        return DBUtil.readBytes(stmt.getBlob(pos));
      case Types.CLOB:
        return DBUtil.readBytes(stmt.getClob(pos));
      default:
        return stmt.getString(pos);
    }

  }
}
