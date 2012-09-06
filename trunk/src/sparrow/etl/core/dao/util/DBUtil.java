package sparrow.etl.core.dao.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

import java.lang.reflect.Field;
import java.sql.Types;
import java.sql.Clob;
import java.io.Reader;
import java.io.StringWriter;

import sparrow.etl.core.dao.impl.ColumnTypes;
import sparrow.etl.core.dao.impl.QueryObject;
import sparrow.etl.core.util.Constants;
import sparrow.etl.core.util.SparrowUtil;
import sparrow.etl.core.util.TokenResolver;

public final class DBUtil {

  private DBUtil() {
  }

  /**
   *
   * @param blob Blob
   * @throws SQLException
   * @throws IOException
   * @return byte[]
   */
  public static byte[] readBytes(Blob blob) {
    byte[] retVal = new byte[1024];

    InputStream inStream = null;
    ByteArrayOutputStream outStream = new ByteArrayOutputStream();
    try {
      inStream = blob.getBinaryStream();
      int c = -1;
      while ( (c = inStream.read(retVal)) != -1) {
        outStream.write(retVal, 0, c);
      }
      outStream.flush();
      retVal = outStream.toByteArray();

    }
    catch (Exception e) {
      e.printStackTrace();
      return null;
    }

    finally {
      if (inStream != null) {
        try {
          inStream.close();
        }
        catch (IOException ex) {
        }
      }
      if (outStream != null) {
        try {
          outStream.close();
        }
        catch (IOException ex1) {
        }
      }
    }
    return retVal;
  }

  /**
   *
   * @param blob Blob
   * @throws SQLException
   * @throws IOException
   * @return byte[]
   */
  public static String readBytes(Clob clob) {
    String rtnVal = null;
    try {
      Reader reader = clob.getCharacterStream();
      StringWriter w = new StringWriter();
      char[] buffer = new char[1];
      while (reader.read(buffer) > 0) {
        w.write(buffer);
      }
      w.close();
      rtnVal = w.toString();
      w = null;
    }
    catch (Exception e) {
      e.printStackTrace();
      return null;
    }
    return rtnVal;
  }

  /**
   *
   * @param sql String
   * @param con Connection
   * @return String
   */
  public static final String handleRowLimit(String sql, Connection con) {
    return getDBType(con);
  }

  /**
   *
   * @param sql SubQuery
   * @param loadedResult Map
   * @param driverResult ResultRow
   * @return String
   */
  public static String parseQuery(String sqlString, Map values) {

    if (sqlString.indexOf(Constants.VARIABLE_IDENTIFIER) != -1) {
      return SparrowUtil.replaceTokens(sqlString, values, Constants.TOKEN_START,
                                     Constants.TOKEN_END);
    }
    else {
      return sqlString;
    }
  }

  private static boolean isNamedParamExists(String sql) {
    return (sql.indexOf(Constants.VARIABLE_IDENTIFIER) != -1);
  }

  public static final String getDBType(Connection con) {
    try {
      return con.getMetaData().getDatabaseProductName();
    }
    catch (SQLException ex) {
      return null;
    }
  }

  /**
   *
   * @param sql String
   * @return ArrayList
   */
  public static final ArrayList getTokenParamAsList(QueryObject query) {
    final ArrayList tokens = new ArrayList();

    String sql = SparrowUtil.replaceTokens(query.getSQL(), new TokenResolver() {
      public String getTokenValue(String token) {
        tokens.add(token);
        return "?";
      }
    });
    sql = SparrowUtil.replace(sql, "\'?\'", "?");
    query.setSQL(sql);
    return tokens;
  }
  
  
  /**
  *
  * @param sql String
  * @return ArrayList
  */
 public static final ArrayList getReplaceTokenParamAsList(QueryObject query) {
   final ArrayList tokens = new ArrayList();
   SparrowUtil.replaceTokens(query.getSQL(),Constants.REPLACE_TOKEN_START,Constants.TOKEN_END, new TokenResolver() {
	     public String getTokenValue(String token) {
	         tokens.add(token);
	         return "@{"+token+"}";
	       }
	     });
   return tokens;
 }
  

  /**
   *
   * @param type String
   * @throws NoSuchFieldException
   * @return int
   */
  public static final int getColumnType(Class claz, String type) {
    int value = -1;
    try {
      Field f = claz.getDeclaredField(type.toUpperCase());
      value = f.getInt(claz);
    }
    catch (NoSuchFieldException ex) {
      ex.printStackTrace();
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
    return value;
  }

  /**
   *
   * @param colType int
   * @param colTypeName String
   * @param colScale int
   * @return int
   */
  public static final int resolveSparrowColumnType(int colType, int colScale) {
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
