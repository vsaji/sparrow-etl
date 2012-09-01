package sparrow.etl.core.dao.provider.impl;

import java.sql.Connection;
import java.util.List;

import sparrow.etl.core.dao.impl.QueryObject;
import sparrow.etl.core.dao.provider.DataProvider;
import sparrow.etl.core.dao.util.ConnectionProvider;
import sparrow.etl.core.exception.DataException;


/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public interface DBDataProvider
    extends DataProvider {

  public void setQuery(QueryObject query);

  public int executeQuery() throws DataException;

  public int[] executeBatch(List param) throws DataException;

  public int[] executeSQLBatch(List param) throws DataException;

  public int[] executeBatch(String sql, List param) throws DataException;

  public void closeConnection() throws DataException;

  public Connection getConnection() throws DataException;

  public void setConnectionProvider(ConnectionProvider connectionProvider);

}
