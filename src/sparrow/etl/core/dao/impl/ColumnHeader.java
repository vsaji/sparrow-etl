package sparrow.etl.core.dao.impl;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import sparrow.etl.core.dao.dialect.DBDialect;


/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */
public class ColumnHeader {

  private List columnHeaderName = null;
  private int columnCount = 0;
  private int[] columnType = null;
  private int[] columnSize = null;
  private int[] columnScale = null;

  /**
   *
   * @param metaData ResultSetMetaData
   */
  public ColumnHeader(ResultSetMetaData metaData) {
    this.bind(metaData);
  }

  /**
   *
   * @param headers String[]
   */
  public ColumnHeader(String[] headers) {
    this.columnHeaderName = getColumnHeaderInLowerCase(headers);
    this.columnCount = headers.length;
    this.columnType = this.columnSize = this.columnScale = new int[this.
        columnCount];
    Arrays.fill(this.columnType, ColumnTypes.STRING); // Varchar
    Arrays.fill(this.columnSize, -1);
    Arrays.fill(this.columnScale, 0);
  }

  /**
   *
   * @param headers String[]
   */
  public ColumnHeader(String[] headerNames, int[] types) {
    this.columnHeaderName = getColumnHeaderInLowerCase(headerNames);
    this.columnCount = headerNames.length;
    this.columnType = types;
    this.columnSize = this.columnScale = new int[columnType.length];
    Arrays.fill(this.columnSize, -1);
    Arrays.fill(this.columnScale, 0);
  }

  /**
   *
   * @param metaData ResultSetMetaData
   */
  private void bind(ResultSetMetaData metaData) {
    try {
      DBDialect transformer = DBDialect.getDBDialect(metaData);
      columnCount = metaData.getColumnCount() + 1;
      columnHeaderName = new ArrayList(columnCount - 1);
      columnType = new int[columnCount - 1];
      columnSize = new int[columnCount - 1];
      columnScale = new int[columnCount - 1];

      for (int i = 0, j = 1; i < columnCount; i++, j = (i + 1)) {
        if (columnCount == j) {
          break;
        }
        columnHeaderName.add(i, transformer.resolveColumnName(metaData, j));
        columnSize[i] = getPrecision(metaData, (j));
        columnScale[i] = metaData.getScale(j);
        columnType[i] = transformer.resolveSparrowColumnType(metaData.
            getColumnType(j), metaData.getColumnTypeName(j), columnScale[i]);
      }
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }

    columnCount = columnHeaderName.size();

  }

  /**
   *
   * @return int
   */
  public int getColumnCount() {
    return columnCount;
  }



  /**
   *
   * @param fieldName String
   * @return int
   */
  public int getFieldIndex(String fieldName) {
    return columnHeaderName.indexOf(fieldName.toLowerCase());
  }

  /**
   *
   * @param index int
   * @return String
   */
  public String getFieldName(int index) {
    return (String) columnHeaderName.get(index);
  }

  /**
   *
   * @param index int
   * @return int
   */
  public int getFieldType(int index) {
    return columnType[index];
  }

  /**
   *
   * @param fieldName String
   * @return int
   */
  public int getFieldType(String fieldName) {
    return columnType[columnHeaderName.indexOf(fieldName)];
  }

  /**
   *
   * @param index int
   * @return int
   */
  public int getFieldSize(int index) {
    return columnSize[index];
  }

  /**
   *
   * @param fieldName String
   * @return int
   */
  public int getFieldSize(String fieldName) {
    return columnSize[columnHeaderName.indexOf(fieldName)];
  }

  /**
   *
   * @param index int
   * @return int
   */
  public int getFieldScale(int index) {
    return columnScale[index];
  }

  /**
   *
   * @param fieldName String
   * @return int
   */
  public int getFieldScale(String fieldName) {
    return columnScale[columnHeaderName.indexOf(fieldName)];
  }

  /**
   *
   * @param metaData ResultSetMetaData
   * @param index int
   * @throws SQLException
   * @return int
   */
  private int getPrecision(ResultSetMetaData metaData, int index) throws
      SQLException {

    int type = metaData.getColumnType(index);

    switch (type) {
      case Types.BLOB:
      case Types.CLOB:
      case Types.VARBINARY:
      case Types.BINARY:
      case Types.LONGVARBINARY:
        return 0;
      default:
        return metaData.getPrecision(index);
    }
  }


  /**
   *
   * @param headerNames String[]
   * @return List
   */
  private List getColumnHeaderInLowerCase(String[] headerNames){
    List l = new ArrayList(headerNames.length);
    for(int i=0;i<headerNames.length;i++){
      l.add(headerNames[i].toLowerCase());
    }
    return l;
  }

  /**
   *
   * @param type int
   * @param scale int
   * @return int
   */
  private int getSparrowColumnType(int type, int scale) {
    switch (type) {
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
        if (scale > 0) {
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

  /**
   *
   * @return String
   */
  public String getFieldTypeName(int index) {

    return ColumnTypes.getH2ColumnTypeAsString(columnType[index]);
  }

  /**
   *
   * @return String
   */
  public String toString() {
    StringBuffer sb = new StringBuffer();
    for (Iterator it = columnHeaderName.iterator(); it.hasNext(); ) {
      sb.append(it.next().toString()).append(",");
    }
    sb.deleteCharAt(sb.length() - 1);
    return sb.toString();
  }

  /**
   *
   * @param separator String
   * @return String
   */
  public String toString(String separator) {
    StringBuffer sb = new StringBuffer();
    for (Iterator it = columnHeaderName.iterator(); it.hasNext(); ) {
      sb.append(it.next().toString()).append(separator);
    }
    sb.deleteCharAt(sb.length() - 1);
    return sb.toString();
  }

  /**
   *
   * @param fieldName String
   * @return boolean
   */
  public boolean isString(String fieldName) {
    return isString(columnHeaderName.indexOf(fieldName));
  }

  /**
   *
   * @param index int
   * @return boolean
   */
  public boolean isString(int index) {
    return ColumnTypes.STRING == columnType[index];
  }

  /**
   *
   * @param columnName String
   * @return boolean
   */
  public boolean isDate(String fieldName) {
    return isDate(columnHeaderName.indexOf(fieldName));
  }

  /**
   *
   * @param columnName String
   * @return boolean
   */
  public boolean isDate(int index) {
    return ColumnTypes.DATE == columnType[index];
  }

  /**
   *
   * @param columnName String
   * @return boolean
   */
  public boolean isInt(String fieldName) {
    return isInt(columnHeaderName.indexOf(fieldName));
  }

  /**
   *
   * @param columnName String
   * @return boolean
   */
  public boolean isInt(int index) {
    return ColumnTypes.INTEGER == columnType[index];
  }

  /**
   *
   * @param columnName String
   * @return boolean
   */
  public boolean isBLOB(String fieldName) {
    return isBLOB(columnHeaderName.indexOf(fieldName));
  }

  /**
   *
   * @param index int
   * @return boolean
   */
  public boolean isBLOB(int index) {
    return ColumnTypes.BLOB == columnType[index];
  }

  /**
   *
   * @param columnName String
   * @return boolean
   */
  public boolean isLong(int index) {
    return ColumnTypes.LONG == columnType[index];
  }

  /**
   *
   * @param columnName String
   * @return boolean
   */
  public boolean isLong(String fieldName) {
    return isLong(columnHeaderName.indexOf(fieldName));
  }

  /**
   *
   * @param columnName String
   * @return boolean
   */
  public boolean isDouble(int index) {
    return ColumnTypes.DOUBLE == columnType[index];
  }

  /**
   *
   * @param columnName String
   * @return boolean
   */
  public boolean isDouble(String fieldName) {
    return isDouble(columnHeaderName.indexOf(fieldName));
  }

  /**
   *
   * @param columnName String
   * @return boolean
   */
  public boolean isFloat(int index) {
    return ColumnTypes.FLOAT == columnType[index];
  }

  /**
   *
   * @param columnName String
   * @return boolean
   */
  public boolean isFloat(String fieldName) {
    return isFloat(columnHeaderName.indexOf(fieldName));
  }

  /**
   *
   * @param index int
   * @return boolean
   */
  public boolean isJavaObject(int index) {
    return ColumnTypes.JAVA_OBJECT == columnType[index];
  }

  /**
   *
   * @param columnName String
   * @return boolean
   */
  public boolean isJavaObject(String fieldName) {
    return isJavaObject(columnHeaderName.indexOf(fieldName));
  }

  /**
   *
   */
  public void destroy(){
    if(columnHeaderName!=null){
      columnHeaderName.clear();
      columnHeaderName = null;
      columnType = null;
      columnScale = null;
      columnSize = null;
    }
  }

}
