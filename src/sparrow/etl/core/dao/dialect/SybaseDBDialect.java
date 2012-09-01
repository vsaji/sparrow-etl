package sparrow.etl.core.dao.dialect;

import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
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
public class SybaseDBDialect
    extends DBDialect {

  public SybaseDBDialect() {
  }

  /**
   * handleRowLimit
   *
   * @param sql String
   * @param con Connection
   * @return String
   */
  public String handleRowLimit(String sql, int limit, Connection con) {

    if (sql.toLowerCase().indexOf(" top ") != -1) {
      return sql;
    }

    try {
      con.createStatement().execute("set rowcount " + limit);
    }
    catch (SQLException ex) {
      ex.printStackTrace();
    }
    return sql;
    /**
         int start = sql.toLowerCase().indexOf("select");
         int afterSelect = start + 7;
         String select = sql.substring(start, afterSelect);
         String aftSelect = sql.substring(afterSelect);
         return select + " TOP " + limit + " " + aftSelect;
     **/
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
        if (colTypeName.toLowerCase().equals("numeric")) {

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

  
  /**
   * 
   */
  public String resolveColumnName(ResultSetMetaData rmd,int i) throws SQLException{
	  return rmd.getColumnLabel(i).toLowerCase();	  
  }
  
  /**
   *
   */
  public void useDB(String dbName,Connection con){
	  try {
	      con.createStatement().execute("use " + dbName);
	    }
	    catch (SQLException ex) {
	      ex.printStackTrace();
	    }
  }

}
