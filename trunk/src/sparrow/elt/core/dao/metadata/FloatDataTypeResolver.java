package sparrow.elt.core.dao.metadata;

import sparrow.elt.core.dao.impl.ColumnTypes;
import sparrow.elt.core.exception.TypeCastException;

public class FloatDataTypeResolver
    extends DataTypeResolver {

  public FloatDataTypeResolver(ColumnAttributes ca) {
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
          Float.valueOf(value.trim()) : getDefaultValue(ColumnTypes.FLOAT);
    }
    catch (Exception e) {
      throw new TypeCastException("FLOAT_CONVERSION",
                                  "Float Conversion failed : value=" + value);
    }
  }

  /**
   * getDataType
   *
   * @return int
   */
  public int getDataType() {
    return ColumnTypes.FLOAT;
  }

}
