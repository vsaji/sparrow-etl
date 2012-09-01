package sparrow.etl.core.dao.metadata;

import sparrow.etl.core.dao.impl.ColumnTypes;
import sparrow.etl.core.exception.TypeCastException;

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */
public class DoubleDataTypeResolver
    extends DataTypeResolver {

  public DoubleDataTypeResolver(ColumnAttributes ca) {
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
          Double.valueOf(value.trim()) : getDefaultValue(ColumnTypes.DOUBLE);
    }
    catch (Exception e) {
      throw new TypeCastException("DOUBLE_CONVERSION",
                                  "Double Conversion failed : value=" + value);
    }
  }

  /**
   * getDataType
   *
   * @return int
   */
  public int getDataType() {
    return ColumnTypes.DOUBLE;
  }

}
