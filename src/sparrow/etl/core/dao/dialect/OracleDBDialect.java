package sparrow.etl.core.dao.dialect;

import java.sql.Connection;
import java.sql.Types;

import sparrow.etl.core.dao.impl.ColumnTypes;


/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */
public class OracleDBDialect
    extends DBDialect {

  public OracleDBDialect() {
  }

  /**
   * handleRowLimit
   *
   * @param sql String
   * @param con Connection
   * @return String
   */
  public String handleRowLimit(String sql, int limit, Connection con) {

    String temp = sql.toLowerCase();
    String sqlPart1, sqlPart2, changedSql = null;
    limit = (limit + 1);
    int wherePos = temp.indexOf("where");
    int rowNumPos = temp.indexOf("rownum");

    if (wherePos != -1) {
      if (rowNumPos == -1) {
        sqlPart1 = sql.substring(0, wherePos + 5);
        sqlPart2 = sql.substring(wherePos + 5, sql.length());
        changedSql = sqlPart1 + " rownum < " + limit + " and " + sqlPart2;
      }
      else {
        changedSql = sql;
      }
    }
    else {
      changedSql = sql + " where rownum < " + limit;
    }

    return changedSql;
  }

  /**
   * resolveSpearColumnType
   *
   * @param colType int
   * @param colScale int
   * @return int
   */
  public int resolveSpearColumnType(int colType, String colTypeName,
                                    int colScale) {
    switch (colType) {
      case Types.NUMERIC:
        if (colTypeName.toLowerCase().equals("number")) {

          /**if(colScale!=0){
            return ColumnTypes.DOUBLE;
                      }
           **/
          return ColumnTypes.NUMBER;
        }
      default:
        return super.resolveSpearColumnType(colType, colTypeName, colScale);
    }
  }

}
