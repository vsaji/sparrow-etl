package sparrow.etl.core.dao.dialect;

import java.sql.Connection;

public class DB2DBDialect
    extends DBDialect {


  public DB2DBDialect() {
  }

  /**
   * handleRowLimit
   *
   * @param sql String
   * @param limit int
   * @param con Connection
   * @return String
   */
  public String handleRowLimit(String sql, int limit, Connection con) {
    return sql + " FETCH FIRST "+limit+" ROWS ONLY";
  }

}
