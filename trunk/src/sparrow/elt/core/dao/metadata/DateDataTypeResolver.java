package sparrow.elt.core.dao.metadata;

import java.text.SimpleDateFormat;
import java.util.Date;

import sparrow.elt.core.dao.impl.ColumnTypes;
import sparrow.elt.core.exception.TypeCastException;


public class DateDataTypeResolver
    extends DataTypeResolver {

  public DateDataTypeResolver(ColumnAttributes ca) {
    super(ca);
  }

  /**
   * getTypeCastedValue
   *
   * @param value String
   * @return Object
   */
  public Object getTypeCastedValue(String value) throws TypeCastException {
    Date d = null;
    try {
      d = (value != null && !value.trim().equals("")) ?
          new SimpleDateFormat(ca.getFormat()).parse(value) :
          (Date) getDefaultValue(ColumnTypes.DATE);
    }
    catch (Exception e) {
      throw new TypeCastException("DATE_CONVERSION",
                                  "Date Conversion failed : value=" + value +
                                  ",format=" + ca.getFormat());
    }
    return d;
  }

  /**
   * getDataType
   *
   * @return int
   */
  public int getDataType() {
    return ColumnTypes.DATE;
  }

}
