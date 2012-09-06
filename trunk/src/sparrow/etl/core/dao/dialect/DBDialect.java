package sparrow.etl.core.dao.dialect;

import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import sparrow.etl.core.dao.impl.ColumnTypes;
import sparrow.etl.core.util.SparrowUtil;


/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */
public class DBDialect {

  public static final int DRIVER_CLASS = 1;
  public static final int JDBC_URL = 2;
  public static final int METADATA_CLASS = 3;

  private static final DBDialect DEFAULT = new DBDialect();

  private static final HashMap DIALECT_MAP = new HashMap() {
    {
      Map impls = SparrowUtil.getImplConfig("dbdialect");
      HashMap hm = new HashMap();

      for (Iterator it = impls.keySet().iterator(); it.hasNext(); ) {
        String key = (String) it.next();
        String className = (String) impls.get(key);
        if (!hm.containsKey(className)) {
          hm.put(className, SparrowUtil.createObject(className));
          put(key, hm.get(className));
        }
        else {
          put(key, hm.get(className));
        }
      }
      hm.clear();
      hm = null;
      impls = null;
    }
  };

  
  /**
   * 	
   * @param rmd
   * @param i
   * @return
 * @throws SQLException 
   */
  public String resolveColumnName(ResultSetMetaData rmd,int i) throws SQLException{
	  return rmd.getColumnLabel(i).toLowerCase();	  
  }
  
  /**
   *
   * @param dbType String
   * @return DBDialect
   */
  public static final DBDialect getDBDialect(String dbType) {
    return getDBDialect(dbType, JDBC_URL);
  }

  /**
   *
   * @param dbType String
   * @return DBDialect
   */
  public static final DBDialect getDBDialect(String value, int type) {
    String key = null;

    switch (type) {
      case METADATA_CLASS:
      case DRIVER_CLASS:
        key = getType(value, ".");
        break;
      case JDBC_URL:
        key = getType(value, ":");
        break;
    }
    DBDialect dialect = (DBDialect) DIALECT_MAP.get(key);
    return (dialect != null) ? dialect : DEFAULT;
  }

  /**
   *
   * @param dbType String
   * @return DBDialect
   */
  public static final DBDialect getDBDialect(ResultSetMetaData metaData) {
    return getDBDialect(metaData.getClass().getName(), METADATA_CLASS);
  }

  /**
   *
   * @param dbInfo String
   * @return String
   */
  private static final String getType(String dbInfo, String seperator) {
    String[] tokens = dbInfo.split("[" + seperator + "]");
    String var = tokens[0] + "." + tokens[1] +
        ( (tokens.length > 2 && !tokens[2].startsWith("/")) ? "." + tokens[2] : "");
    return var;
  }

  public String handleRowLimit(String sql, int limit, Connection con) {
    return sql;
  }

  /**
   *
   * @param dbName
   * @param con
   */
  public void useDB(String dbName,Connection con){}


  /**
   *
   * @param colType int
   * @param colScale int
   * @return int
   */
  public int resolveSparrowColumnType(int colType, String colTypeName,
                                    int colScale) {
    switch (colType) {
      case Types.VARCHAR:
      case Types.CHAR:
      case Types.OTHER:
      case ColumnTypes.STRING:
        return ColumnTypes.STRING;
      case Types.DATE:
      case Types.TIMESTAMP:
      case ColumnTypes.DATE:
        return ColumnTypes.DATE;
      case Types.INTEGER:
      case Types.NUMERIC:
        if (colScale > 0) {
          return ColumnTypes.DOUBLE;
        }
      case Types.SMALLINT:
      case Types.TINYINT:
      case ColumnTypes.INTEGER:
        return ColumnTypes.INTEGER;
      case Types.BLOB:
      case Types.VARBINARY:
      case Types.BINARY:
      case Types.LONGVARBINARY:
      case ColumnTypes.BLOB:
        return ColumnTypes.BLOB;
      case Types.CLOB:
        return ColumnTypes.CLOB;
      case Types.BIGINT:
      case ColumnTypes.LONG:
        return ColumnTypes.LONG;
      case Types.DOUBLE:
      case ColumnTypes.DOUBLE:
        return ColumnTypes.DOUBLE;
      case Types.DECIMAL:
      case Types.FLOAT:
      case ColumnTypes.FLOAT:
        return ColumnTypes.FLOAT;
      case Types.JAVA_OBJECT:
      case ColumnTypes.JAVA_OBJECT:
        return ColumnTypes.JAVA_OBJECT;
      default:
        return ColumnTypes.STRING;
    }
  }


}
