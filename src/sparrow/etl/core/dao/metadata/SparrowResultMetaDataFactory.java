package sparrow.etl.core.dao.metadata;

import java.io.InputStream;
import java.util.HashMap;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import sparrow.etl.core.util.SparrowUtil;


/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */
public class SparrowResultMetaDataFactory {

  private static final HashMap map = new HashMap();

  private SparrowResultMetaDataFactory() {
  }

  /**
   *
   * @param fileName String
   * @return DataTypeResolver[]
   */
  public static final SparrowResultSetMetaData getSparrowResultSetMetaData(String
      fileName, String colset) {

    /**    String key = fileName + colset;
        if (map.containsKey(key)) {
          return (SparrowResultSetMetaData) map.get(fileName + colset);
        }
        else {**/
    //map.put(key, srsmd);
    SparrowResultSetMetaData srsmd;
    try {
      InputStream in = SparrowUtil.getFileAsStream(fileName);
      srsmd = getSparrowResultSetMetaData(in, colset);
      in.close();
    }
    catch (Exception ex) {
      ex.printStackTrace();
      srsmd = null;
    }

    return srsmd;
    //}
  }

  /**
   *
   * @param file File
   * @return DataTypeResolver[]
   */
  private static final SparrowResultSetMetaData getSparrowResultSetMetaData(
      InputStream in, String colset) {
    try {
      SAXParser sp = SAXParserFactory.newInstance().newSAXParser();
      ColumnDefSAXHandler handler = new ColumnDefSAXHandler(colset);
      sp.parse(in, handler);
      return new SparrowResultSetMetaData(handler.getAllColumnTypes(),
                                        handler.getUnExculdedColumnTypes());
    }
    catch (Exception ex) {
      ex.printStackTrace();
      return null;
    }
  }
}
