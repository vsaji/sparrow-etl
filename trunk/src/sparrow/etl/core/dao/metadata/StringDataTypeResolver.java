package sparrow.etl.core.dao.metadata;

import sparrow.etl.core.dao.impl.ColumnTypes;
import sparrow.etl.core.exception.TypeCastException;

public class StringDataTypeResolver
    extends DataTypeResolver {

  public StringDataTypeResolver(ColumnAttributes ca) {
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
      return (value != null && !value.trim().equals("")) ? value :
          getDefaultValue(ColumnTypes.STRING);
    }
    catch (Exception e) {
      throw new TypeCastException("STRING_CONVERSION",
                                  "String Conversion failed : value=" + value
                                  );
    }
  }

  /**
   * getDataType
   *
   * @return int
   */
  public int getDataType() {
    return ColumnTypes.STRING;
  }

}
