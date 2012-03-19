package sparrow.elt.core.dao.metadata;

import java.util.ArrayList;
import java.util.Iterator;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import sparrow.elt.core.dao.impl.ColumnTypes;
import sparrow.elt.core.dao.util.DBUtil;

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */
public class ColumnDefSAXHandler
    extends DefaultHandler {

  private static final String TAG_COL_SET = "column-set";
  private static final String TAG_COL = "column";
  private static final String ATT_NAME = "name";
  private static final String ATT_TYPE = "type";
  private static final String ATT_DEFAULT_VALUE = "default-value";
  private static final String ATT_FORMAT = "format";
  private static final String ATT_SIZE = "size";
  private static final String ATT_EXCLD_COL = "exclude-column";
  private static final String ATT_XPATH = "xpath";

  private ArrayList al, allTypes, unexcldTypes = null;

  private String currentSet = null;

  private final String colSet;
  /**
   *
   */
  public ColumnDefSAXHandler(String colSet) {
    this.colSet = colSet;
  }

  public void startDocument() {
  }

  /**
   *
   * @param uri String
   * @param localName String
   * @param qName String
   * @param attributes Attributes
   * @throws SAXException
   */
  public void startElement(String uri, String localName, String qName,
                           Attributes attributes) throws SAXException {

    if (TAG_COL_SET.equals(qName)) {
      currentSet = attributes.getValue(ATT_NAME);
      if (currentSet.equals(colSet)) {
        al = new ArrayList();
      }
    }

    if (TAG_COL.equals(qName) && al != null) {
      al.add(getDataTypeResolver(attributes));
    }

  }

  /**
   * getDataTypeResolver
   *
   * @param attributes Attributes
   * @return DataTypeResolver
   */
  private DataTypeResolver getDataTypeResolver(Attributes attributes) {

    final ColumnAttributes ca = new ColumnAttributes();

    String size = attributes.getValue(ATT_SIZE);
    size = (size != null) ? size.trim() : null;

    String excludeCol = attributes.getValue(ATT_EXCLD_COL);

    ca.setFormat(attributes.getValue(ATT_FORMAT));
    ca.setDefaultValue(attributes.getValue(ATT_DEFAULT_VALUE));
    ca.setXPath(attributes.getValue(ATT_XPATH));
    ca.setColumnName(attributes.getValue(ATT_NAME));
    ca.setSize( (size != null && !size.equals("")) ?
               Integer.parseInt(size) : -1);
    ca.setExcludeColumn( (excludeCol != null) ?
                        Boolean.valueOf(excludeCol).booleanValue() : false);

    try {
      switch (DBUtil.getColumnType(ColumnTypes.class,
                                   attributes.getValue(ATT_TYPE))) {
        case ColumnTypes.STRING:
          return new StringDataTypeResolver(ca);
        case ColumnTypes.DOUBLE:
          return new DoubleDataTypeResolver(ca);
        case ColumnTypes.LONG:
          return new LongDataTypeResolver(ca);
        case ColumnTypes.INTEGER:
          return new IntegerDataTypeResolver(ca);
        case ColumnTypes.FLOAT:
          return new FloatDataTypeResolver(ca);
        case ColumnTypes.DATE:
          return new DateDataTypeResolver(ca);
        case ColumnTypes.BLOB:
          return new BlobDataTypeResolver(ca);
        default:
          return new StringDataTypeResolver(ca);
      }
    }
    catch (Exception ex) {
      return null;
    }
  }

  /**
   *
   * @param uri String
   * @param localName String
   * @param qName String
   * @throws SAXException
   */
  public void endElement(String uri, String localName, String qName) throws
      SAXException {
    if (TAG_COL_SET.equals(qName) && al != null) {

      allTypes = new ArrayList(al);
      al.clear();
      al = null;

      unexcldTypes = new ArrayList();

      for (Iterator it = allTypes.iterator(); it.hasNext(); ) {
        DataTypeResolver dtr = (DataTypeResolver) it.next();
        if (!dtr.isExcludeColumn()) {
          unexcldTypes.add(dtr);
        }
      }
    }
  }

  /**
   *
   * @return DataTypeResolver[]
   */
  public DataTypeResolver[] getAllColumnTypes() {
    return (DataTypeResolver[]) allTypes.toArray(new DataTypeResolver[
                                                 allTypes.size()]);
  }

  /**
   *
   * @return DataTypeResolver[]
   */
  public DataTypeResolver[] getUnExculdedColumnTypes() {
    return (DataTypeResolver[]) unexcldTypes.toArray(new DataTypeResolver[
        unexcldTypes.size()]);
  }

}
