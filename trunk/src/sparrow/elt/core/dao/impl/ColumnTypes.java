package sparrow.elt.core.dao.impl;

import java.util.HashMap;

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public final class ColumnTypes {

  public static final int STRING = 3565;
  public static final int INTEGER = 3563;
  public static final int NUMBER = 3561;
  public static final int INT = 3563;
  public static final int DOUBLE = 3564;
  public static final int FLOAT = 3566;
  public static final int BLOB = 3567;
  public static final int CLOB = 3570;
  public static final int LONG = 3568;
  public static final int JAVA_OBJECT = 3569;
  public static final int DATE = 3562;

  private static final HashMap map = new HashMap() {
    {
      put(new Integer(STRING), "STRING");
      put(new Integer(INTEGER), "INTEGER");
      put(new Integer(NUMBER), "NUMBER");
      put(new Integer(INTEGER), "INT");
      put(new Integer(DOUBLE), "DOUBLE");
      put(new Integer(FLOAT), "FLOAT");
      put(new Integer(BLOB), "BLOB");
      put(new Integer(CLOB), "CLOB");
      put(new Integer(LONG), "LONG");
      put(new Integer(JAVA_OBJECT), "JAVA_OBJECT");
      put(new Integer(DATE), "DATE");
    }
  };

  private static final HashMap H2TYPE = new HashMap() {
    {
      put(new Integer(STRING), "VARCHAR");
      put(new Integer(INTEGER), "INTEGER");
      put(new Integer(NUMBER), "NUMBER");
      put(new Integer(INTEGER), "INT");
      put(new Integer(DOUBLE), "DOUBLE");
      put(new Integer(FLOAT), "REAL");
      put(new Integer(BLOB), "BLOB");
      put(new Integer(CLOB), "CLOB");
      put(new Integer(LONG), "BIGINT");
      put(new Integer(JAVA_OBJECT), "OTHER");
      put(new Integer(DATE), "DATETIME");
    }
  };

  private ColumnTypes() {}


  /**
   *
   * @param type String
   * @throws NoSuchFieldException
   * @return int
   */
  public static final String getColumnTypeAsString(int type) {
    return map.get(new Integer(type)).toString();
  }

  public static final String getH2ColumnTypeAsString(int type) {
    return H2TYPE.get(new Integer(type)).toString();
  }

}
