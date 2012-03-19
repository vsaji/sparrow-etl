package sparrow.elt.core.transaction;

import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Savepoint;
import java.sql.Statement;
import java.util.Map;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;

import org.apache.log4j.Logger;

/**
 * a 'transaction-aware' wrapper for a JDBC connection.  This is used to ensure
 * that a connection is only commited, rolled back or closed until required
 * by a transaction
 *
 */
public class TransConnection
    implements Connection, XAResourceWrapper, Serializable {
  private static final Logger log = Logger.getLogger(TransConnection.class.
      getName());

  private String name;
  private Connection conn;
  private boolean close = false;
  private boolean inTrans = false;

  private long tempTime;
  private java.util.ArrayList statements = null;

  /**
   * wrap a JDBC connection with the specified name
   */
  public TransConnection(String name, Connection conn, boolean inTrans) throws
      SQLException {
    this.name = name;
    this.conn = conn;
    this.inTrans = inTrans;
    this.conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

    statements = new java.util.ArrayList();
    if (log.isDebugEnabled()) {
      tempTime = System.currentTimeMillis();
      log.debug("created new transaction connection for jdbc conn " + conn);
    }
  }

  /**
   * get the name of this connection
   */
  public String getName() {
    return name;
  }

  /**
   * @see java.sql.Connection#clearWarnings
   */
  public void clearWarnings() throws SQLException {
    conn.clearWarnings();
  }

  /**
   * if not in a transaction, this closes the connection immediately,
   * otherwise just flags that a close is required
   * @see java.sql.Connection#close
   */
  public void close() throws SQLException {
    if (!inTrans) {
      doClose();
    }
    else {
      close = true;
    }
  }

  /**
   * check if close has been flagged on this connection
   */
  public boolean isCloseFlagged() {
    return close;
  }

  /**
   * performs the actual close on the underlying connection
   */
  public void doClose() throws SQLException {
    if (log.isDebugEnabled()) {
      log.debug("closing connection " + conn);
      tempTime = System.currentTimeMillis() - tempTime;
      if (tempTime > 15000) {
        log.debug("time between opening and closing conn " + conn + " was " +
                  tempTime + " after performing " + statements.toString());
      }
    }
    conn.close();
  }

  /**
   * if not in a transaction, a commit is performed immediately,
   * otherwise nothing happens
   * @see java.sql.Connection#commit
   */
  public void commit() throws SQLException {
    if (!inTrans) {
      doCommit();
    }
  }

  /**
   * performs the actual commit on the underlying connection
   */
  public void doCommit() throws SQLException {
    conn.commit();
    inTrans = false;
  }

  /**
   * @see java.sql.Connection#createStatement
   */
  public Statement createStatement() throws SQLException {
    return conn.createStatement();
  }

  /**
   * @see java.sql.Connection#createStatement
   */
  public Statement createStatement(int param, int param1) throws SQLException {
    return conn.createStatement(param, param1);
  }

  /**
   * @see java.sql.Connection#createStatement
   */
  public Statement createStatement(int param, int param1, int param2) throws
      SQLException {
    return conn.createStatement(param, param1, param2);
  }

  /**
   * @see java.sql.Connection#getAutoCommit
   */
  public boolean getAutoCommit() throws SQLException {
    return conn.getAutoCommit();
  }

  /**
   * @see java.sql.Connection#getCatalog
   */
  public String getCatalog() throws SQLException {
    return conn.getCatalog();
  }

  /**
   * @see java.sql.Connection#getHoldability
   */
  public int getHoldability() throws SQLException {
    return conn.getHoldability();
  }

  /**
   * @see java.sql.Connection#getMetaData
   */
  public DatabaseMetaData getMetaData() throws SQLException {
    return conn.getMetaData();
  }

  /**
   * @see java.sql.Connection#getTransactionIsolation
   */
  public int getTransactionIsolation() throws SQLException {
    return conn.getTransactionIsolation();
  }

  /**
   * @see java.sql.Connection#getTypeMap
   */
  public Map getTypeMap() throws SQLException {
    return conn.getTypeMap();
  }

  /**
   * @see java.sql.Connection#getWarnings
   */
  public SQLWarning getWarnings() throws SQLException {
    return conn.getWarnings();
  }

  /**
   * @see java.sql.Connection#isClosed
   */
  public boolean isClosed() throws SQLException {
    return conn.isClosed();
  }

  /**
   * @see java.sql.Connection#isReadOnly
   */
  public boolean isReadOnly() throws SQLException {
    return conn.isReadOnly();
  }

  /**
   * @see java.sql.Connection#nativeSQL
   */
  public String nativeSQL(String str) throws SQLException {
    return conn.nativeSQL(str);
  }

  /**
   * @see java.sql.Connection#prepareCall
   */
  public CallableStatement prepareCall(String str) throws SQLException {
    return conn.prepareCall(str);
  }

  /**
   * @see java.sql.Connection#prepareCall
   */
  public CallableStatement prepareCall(String str, int param, int param2) throws
      SQLException {
    return conn.prepareCall(str, param, param2);
  }

  /**
   * @see java.sql.Connection#prepareCall
   */
  public CallableStatement prepareCall(String str, int param, int param2,
                                       int param3) throws SQLException {
    return conn.prepareCall(str, param, param2, param3);
  }

  /**
   * @see java.sql.Connection#prepareStatement
   */
  public PreparedStatement prepareStatement(String str) throws SQLException {
    statements.add(str);
    return conn.prepareStatement(str);
  }

  /**
   * @see java.sql.Connection#prepareStatement
   */
  public PreparedStatement prepareStatement(String str, int param) throws
      SQLException {
    return conn.prepareStatement(str, param);
  }

  /**
   * @see java.sql.Connection#prepareStatement
   */
  public PreparedStatement prepareStatement(String str, int[] values) throws
      SQLException {
    return conn.prepareStatement(str, values);
  }

  /**
   * @see java.sql.Connection#prepareStatement
   */
  public PreparedStatement prepareStatement(String str, String[] str1) throws
      SQLException {
    return conn.prepareStatement(str, str1);
  }

  /**
   * @see java.sql.Connection#prepareStatement
   */
  public PreparedStatement prepareStatement(String str, int param, int param2) throws
      SQLException {
    return conn.prepareStatement(str, param, param2);
  }

  /**
   * @see java.sql.Connection#prepareStatement
   */
  public PreparedStatement prepareStatement(String str, int param, int param2,
                                            int param3) throws SQLException {
    return conn.prepareStatement(str, param, param2, param3);
  }

  /**
   * @see java.sql.Connection#releaseSavepoint
   */
  public void releaseSavepoint(Savepoint savepoint) throws SQLException {
    conn.releaseSavepoint(savepoint);
  }

  /**
   * if not in a transaction, this rolls back the underlying connection immediately,
   * otherwise nothing is done
   * @see java.sql.Connection#rollback
   */
  public void rollback() throws SQLException {
    if (!inTrans) {
      doRollback(null);
    }
  }

  /**
   * if not in a transaction, this rolls back the underlying connection immediately,
   * otherwise nothing is done
   * @see java.sql.Connection#rollback
   */
  public void rollback(Savepoint savepoint) throws SQLException {
    if (!inTrans) {
      doRollback(savepoint);
    }
  }

  /**
   * performs the actual rollback on the underlying Connection
   * @see java.sql.Connection#rollback
   */
  public void doRollback(Savepoint savepoint) throws SQLException {
    if (savepoint == null) {
      conn.rollback();
    }
    else {
      conn.rollback(savepoint);
    }
  }

  /**
   * @see java.sql.Connection#setAutoCommit
   */
  public void setAutoCommit(boolean param) throws SQLException {
    conn.setAutoCommit(param);
  }

  /**
   * @see java.sql.Connection#setCatalog
   */
  public void setCatalog(String str) throws SQLException {
    conn.setCatalog(str);
  }

  /**
   * @see java.sql.Connection#setHoldability
   */
  public void setHoldability(int param) throws SQLException {
    conn.setHoldability(param);
  }

  /**
   * @see java.sql.Connection#setReadOnly
   */
  public void setReadOnly(boolean param) throws SQLException {
    conn.setReadOnly(param);
  }

  /**
   * @see java.sql.Connection#setSavepoint
   */
  public Savepoint setSavepoint() throws SQLException {
    return conn.setSavepoint();
  }

  /**
   * @see java.sql.Connection#setSavepoint
   */
  public Savepoint setSavepoint(String str) throws SQLException {
    return conn.setSavepoint(str);
  }

  /**
   * @see java.sql.Connection#setTransactionIsolation
   */
  public void setTransactionIsolation(int param) throws SQLException {
    conn.setTransactionIsolation(param);
  }

  /**
   * @see java.sql.Connection#setTypeMap
   */
  public void setTypeMap(Map map) throws SQLException {
    conn.setTypeMap(map);
  }

  /**  these methods need implementation **/

  public void commit(javax.transaction.xa.Xid xid, boolean param) throws
      XAException {
    try {
      doCommit();
    }
    catch (SQLException ex) {
      throw new XAException(ex.getMessage());
    }
//    throw new UnsupportedOperationException();
  }

  public void end(javax.transaction.xa.Xid xid, int param) throws XAException {
//    throw new UnsupportedOperationException();
  }

  public void forget(javax.transaction.xa.Xid xid) throws XAException {
//    throw new UnsupportedOperationException();
  }

  public int getTransactionTimeout() throws XAException {
    return 0;
    //   throw new UnsupportedOperationException();
  }

  public boolean isSameRM(javax.transaction.xa.XAResource xAResource) throws
      XAException {
    return xAResource.equals(this);
//    throw new UnsupportedOperationException();
  }

  public int prepare(javax.transaction.xa.Xid xid) throws XAException {
    return XAResource.XA_OK;
  }

  public javax.transaction.xa.Xid[] recover(int param) throws XAException {
    return null;
    //  throw new UnsupportedOperationException();
  }

  public void rollback(javax.transaction.xa.Xid xid) throws XAException {
    try {
      doRollback(null);
    }
    catch (SQLException se) {
      throw new XAException(se.getMessage());
    }
  }

  public boolean setTransactionTimeout(int param) throws XAException {
    return true;
    //   throw new UnsupportedOperationException();
  }

  public void start(javax.transaction.xa.Xid xid, int param) throws XAException {

//    throw new UnsupportedOperationException();
  }

  /**
   * doRollback
   */
  public void doRollback() throws Exception {
    doRollback(null);
  }

  public String toString() {
    return "[" + getName() + ":" + conn.toString() + "]";
  }

}
