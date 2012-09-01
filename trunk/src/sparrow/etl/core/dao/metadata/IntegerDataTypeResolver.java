package sparrow.etl.core.dao.metadata;

import sparrow.etl.core.dao.impl.ColumnTypes;
import sparrow.etl.core.exception.TypeCastException;

public class IntegerDataTypeResolver
    extends DataTypeResolver {

  public IntegerDataTypeResolver(ColumnAttributes ca) {
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
          Integer.valueOf(value.trim()) : getDefaultValue(ColumnTypes.INTEGER);
    }
    catch (Exception e) {
      throw new TypeCastException("INTEGER_CONVERSION",
                                  "Integer Conversion failed : value=" + value
                                  );
    }
  }

  /**
   * getDataType
   *
   * @return int
   */
  public int getDataType() {
    return ColumnTypes.INTEGER;
  }
}
