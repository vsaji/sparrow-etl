package sparrow.elt.core.transaction;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import javax.naming.Reference;
import javax.naming.Referenceable;
import javax.naming.StringRefAddr;
import javax.sql.DataSource;

import org.apache.log4j.Logger;

public class SparrowDataSource
    implements DataSource, Referenceable {
  private static final Logger log = Logger.getLogger(SparrowDataSource.class.
      getName());
  private static Map dataSources = new HashMap();
  private static SparrowTransactionManager stm = null;

  private String name;
  private DataSource source;

  private static final String REQUIRED = "Required";
  private static final String REQUIRES_NEW = "RequiresNew";
  private static final String SUPPORTS = "Supports";

  public SparrowDataSource(String name, DataSource source) throws Exception {
    this.name = name;
    this.source = source;
    dataSources.put(name, this);

    if (stm == null) {
      synchronized (SparrowDataSource.class) {
        stm = new SparrowTransactionManager();
      }
    }
  }

  /**
   * helper method for the object factory associated with this class
   */
  protected static final SparrowDataSource getSpearDataSource(String name) {
    return (SparrowDataSource) dataSources.get(name);
  }

  /**
   * get a connection (see the next getConnection method for more detail)
   */
  public Connection getConnection() throws SQLException {
    return getConnection(null, null);
  }

  /**
   * get a jdbc connection -- if the calling thread is in a transaction, then
   * this connection will be wrapped by a TransConnection object, otherwise
   * the base connection is returned
   *
   * NOTE:  RequiresNew is -NOT- handled properly at the moment
   */
  public Connection getConnection(String str, String str1) throws SQLException {
    String methodTrans = REQUIRED;
    if (stm.isInTransaction()
        && (methodTrans != null && (methodTrans.equals(REQUIRED)
                                    || methodTrans.equals(SUPPORTS)
                                    || methodTrans.equals(REQUIRES_NEW)))) {
      SparrowTransaction trans = (SparrowTransaction) stm.getTransaction();
      Object tmp = trans.getResource(name);
      TransConnection tc;
      if (tmp != null) {
        tc = (TransConnection) tmp;
        return tc;
      }
      else if (methodTrans.equals(REQUIRED) || methodTrans.equals(REQUIRES_NEW)) {
        tc = new TransConnection(name, getConn(str, str1), true);
        tc.setAutoCommit(false);
        trans.enlistResource(tc);
        return tc;
      }
    }

    Connection conn = getConn(str, str1);
    //  conn.setAutoCommit(true);
    return conn;
  }

  /**
   * call getConnection on the underlying data
   */
  private final Connection getConn(String str, String str1) throws SQLException {
    long time = System.currentTimeMillis();
    try {
      if (str == null && str1 == null) {
        return source.getConnection();
      }
      else {
        return source.getConnection(str, str1);
      }
    }
    catch (SQLException e) {
      time = System.currentTimeMillis() - time;
      if (log.isDebugEnabled()) {
        log.debug("time taken at SQL error point was " + time + "ms");
      }
      e.printStackTrace();
      throw e;
    }
  }

  /**
   * @see javax.sql.DataSource#setLogWriter
   */
  public void setLogWriter(PrintWriter pw) throws SQLException {
    source.setLogWriter(pw);
  }

  /**
   * @see javax.sql.DataSource#getLogWriter
   */
  public PrintWriter getLogWriter() throws SQLException {
    return source.getLogWriter();
  }

  /**
   * @see javax.sql.DataSource#setLoginTimeout
   */
  public void setLoginTimeout(int timeout) throws SQLException {
    source.setLoginTimeout(timeout);
  }

  /**
   * @see javax.sql.DataSource#getLoginTimeout
   */
  public int getLoginTimeout() throws SQLException {
    return source.getLoginTimeout();
  }

  /**
   * reference method required so this object can be loaded into a registry
   */
  public Reference getReference() {
    Reference ref = new Reference(getClass().getName(),
                                  SparrowDataSourceObjectFactory.class.getName(), null);
    ref.add(new StringRefAddr("name", name));
    return ref;
  }
}
