package sparrow.elt.core.dao.metadata;

import java.text.SimpleDateFormat;

import sparrow.elt.core.dao.impl.ColumnTypes;
import sparrow.elt.core.exception.TypeCastException;


public abstract class DataTypeResolver {

  final ColumnAttributes ca;

  /**
   *
   * @param columnName String
   * @param format String
   * @param defaultValue String
   */
  DataTypeResolver(ColumnAttributes ca) {
    this.ca = ca;
  }

  public final ColumnAttributes getColumnAttributes() {
    return ca;
  }

  /**
   *
   * @return boolean
   */
  public boolean isExcludeColumn() {
    return ca.isExcludeColumn();
  }

/**
*
* @return int
*/
  public abstract int getDataType();

  /**
   *
   * @param value String
   * @throws TypeCastException
   * @return Object
   */
  public abstract Object getTypeCastedValue(String value) throws
      TypeCastException;

  /**
   *
   * @param type int
   * @throws Exception
   * @return Object
   */
  protected final Object getDefaultValue(int type) throws Exception {

    String defaultValue = ca.getDefaultValue();
    boolean valueExists = (defaultValue != null);

    if (!valueExists) {
      return null;
    }
    else {
      switch (type) {
        case ColumnTypes.INTEGER:
          return Integer.valueOf(defaultValue);
        case ColumnTypes.DATE:
          return new SimpleDateFormat(ca.getFormat()).parse(defaultValue);
        case ColumnTypes.DOUBLE:
          return Double.valueOf(defaultValue);
        case ColumnTypes.LONG:
          return Long.valueOf(defaultValue);
        case ColumnTypes.FLOAT:
          return Float.valueOf(defaultValue);
        case ColumnTypes.STRING:
        case ColumnTypes.JAVA_OBJECT:
          return defaultValue;
        case ColumnTypes.BLOB:
          return defaultValue.getBytes();
        default:
          return defaultValue;
      }
    }
  }

}
