package sparrow.elt.core.dao.metadata;

import sparrow.elt.core.dao.impl.ColumnTypes;
import sparrow.elt.core.exception.TypeCastException;

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */
public class BlobDataTypeResolver
    extends DataTypeResolver {

  public BlobDataTypeResolver(ColumnAttributes ca) {
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
      return (value != null && !value.trim().equals("")) ? value.getBytes() :
          getDefaultValue(ColumnTypes.BLOB);
    }
    catch (Exception e) {
      throw new TypeCastException("BYTE_CONVERSION",
                                  "Byte Conversion failed : value=" + value);
    }
  }

  /**
   * getDataType
   *
   * @return int
   */
  public int getDataType() {
    return ColumnTypes.BLOB;
  }

}
