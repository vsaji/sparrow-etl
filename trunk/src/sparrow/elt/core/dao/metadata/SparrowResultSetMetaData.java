package sparrow.elt.core.dao.metadata;

import java.sql.ResultSetMetaData;

import sparrow.elt.core.dao.impl.ColumnTypes;


/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */
public class SparrowResultSetMetaData
    implements ResultSetMetaData {

  final DataTypeResolver[] allDtr;
  final DataTypeResolver[] dtr;

  public SparrowResultSetMetaData(DataTypeResolver[] allDtr, DataTypeResolver[] requiredDtr) {
    this.dtr = requiredDtr;
    this.allDtr = allDtr;
  }

  /**
   *
   * @return DataTypeResolver[]
   */
  public DataTypeResolver[] getDataTypeResolvers() {
    return dtr;
  }


  /**
   *
   * @return DataTypeResolver[]
   */
  public DataTypeResolver[] getAllDataTypeResolvers() {
    return allDtr;
  }

  /**
   * getColumnCount
   *
   * @return int
   */
  public int getColumnCount() {
    return dtr.length;
  }

  /**
   * getColumnDisplaySize
   *
   * @param column int
   * @return int
   */
  public int getColumnDisplaySize(int column) {
    return 22;
  }

  /**
   * getColumnType
   *
   * @param column int
   * @return int
   */
  public int getColumnType(int column) {
    return dtr[column - 1].getDataType();
  }

  /**
   * getPrecision
   *
   * @param column int
   * @return int
   */
  public int getPrecision(int column) {
    return dtr[column - 1].getColumnAttributes().getSize();
  }

  /**
   * getScale
   *
   * @param column int
   * @return int
   */
  public int getScale(int column) {
    return 0;
  }

  /**
   * isNullable
   *
   * @param column int
   * @return int
   */
  public int isNullable(int column) {
    return 0;
  }

  /**
   * isAutoIncrement
   *
   * @param column int
   * @return boolean
   */
  public boolean isAutoIncrement(int column) {
    return false;
  }

  /**
   * isCaseSensitive
   *
   * @param column int
   * @return boolean
   */
  public boolean isCaseSensitive(int column) {
    return false;
  }

  /**
   * isCurrency
   *
   * @param column int
   * @return boolean
   */
  public boolean isCurrency(int column) {
    return false;
  }

  /**
   * isDefinitelyWritable
   *
   * @param column int
   * @return boolean
   */
  public boolean isDefinitelyWritable(int column) {
    return false;
  }

  /**
   * isReadOnly
   *
   * @param column int
   * @return boolean
   */
  public boolean isReadOnly(int column) {
    return true;
  }

  /**
   * isSearchable
   *
   * @param column int
   * @return boolean
   */
  public boolean isSearchable(int column) {
    return false;
  }

  /**
   * isSigned
   *
   * @param column int
   * @return boolean
   */
  public boolean isSigned(int column) {
    return false;
  }

  /**
   * isWritable
   *
   * @param column int
   * @return boolean
   */
  public boolean isWritable(int column) {
    return true;
  }

  /**
   * getCatalogName
   *
   * @param column int
   * @return String
   */
  public String getCatalogName(int column) {
    return "";
  }

  /**
   * getColumnClassName
   *
   * @param column int
   * @return String
   */
  public String getColumnClassName(int column) {
    return dtr[column - 1].getClass().getName();
  }

  /**
   * getColumnLabel
   *
   * @param column int
   * @return String
   */
  public String getColumnLabel(int column) {
    return getColumnName(column);
  }

  /**
   * getColumnName
   *
   * @param column int
   * @return String
   */
  public String getColumnName(int column) {
    return dtr[column - 1].getColumnAttributes().getColumnName();
  }

  /**
   * getColumnTypeName
   *
   * @param column int
   * @return String
   */
  public String getColumnTypeName(int column) {
    return ColumnTypes.getColumnTypeAsString(dtr[column - 1].getDataType());
  }

  /**
   * getSchemaName
   *
   * @param column int
   * @return String
   */
  public String getSchemaName(int column) {
    return "";
  }

  /**
   * getTableName
   *
   * @param column int
   * @return String
   */
  public String getTableName(int column) {
    return "";
  }
}
