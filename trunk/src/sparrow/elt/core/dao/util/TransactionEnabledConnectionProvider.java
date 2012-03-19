package sparrow.elt.core.dao.util;

import java.sql.Connection;
import java.sql.SQLException;

import sparrow.elt.core.context.SparrowContext;

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public class TransactionEnabledConnectionProvider implements ConnectionProvider{

  private static SparrowContext ctx;

  private static final ConnectionProvider instance = new TransactionEnabledConnectionProvider();


  /**
   *
   */
  private TransactionEnabledConnectionProvider() {
  }

  /**
   *
   * @return ConnectionProvider
   */
  public static final ConnectionProvider getInstance(){
    return instance;
  }

  /**
   *
   * @param context SparrowContext
   */
  public static final void setContext(SparrowContext context){
    ctx = context;
  }

  /**
   * getConnection
   *
   * @param conName String
   * @return Connection
   */
  public Connection getConnection(String conName) throws SQLException {
    return ctx.getTransactionEnabledDBConnection(conName);
  }

}
