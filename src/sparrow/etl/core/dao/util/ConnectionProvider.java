package sparrow.etl.core.dao.util;

import java.sql.SQLException;
import java.sql.Connection;

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public interface ConnectionProvider {

  public abstract Connection getConnection(String conName) throws SQLException;

}
