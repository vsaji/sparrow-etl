package sparrow.elt.core.dao.metadata;

import sparrow.elt.core.dao.impl.ColumnTypes;
import sparrow.elt.core.exception.TypeCastException;

public class LongDataTypeResolver
    extends DataTypeResolver {

  public LongDataTypeResolver(ColumnAttributes ca) {
    super(ca);
  }

  /**
   * getTypeCastedValue
   *
   * @param value String
   * @return Object
   */
  public Object getTypeCastedValue(String value) throws TypeCastException {
    try {
      return (value != null && !value.trim().equals("")) ?
          Long.valueOf(value.trim()) : getDefaultValue(ColumnTypes.LONG);
    }
    catch (Exception e) {
      throw new TypeCastException("LONG_CONVERSION",
                                  "Long Conversion failed : value=" + value);
    }
  }

  /**
   * getDataType
   *
   * @return int
   */
  public int getDataType() {
    return ColumnTypes.LONG;
  }

}
