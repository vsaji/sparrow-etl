package sparrow.etl.core.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */
public class MultiColumnTable {

  private static String[][] rows = null;
  private static TableHeader header = null;
  private static int size = 0;

  private static MultiColumnTable instance = null;

  /**
   *
   * @param header TableHeader
   */
  private MultiColumnTable(TableHeader header) {
    this(header, 50);
  }

  /**
   *
   * @param header TableHeader
   * @param numberOfRecord int
   */
  private MultiColumnTable(TableHeader header, int numberOfRecord) {
    rows = new String[numberOfRecord][header.count()];
    this.header = header;
  }

  /**
   *
   * @return MultiColumnTable
   */
  public static final MultiColumnTable getInstance() {
    return instance;
  }

  /**
   *
   * @param header TableHeader
   * @return MultiColumnTable
   */
  public static final MultiColumnTable createInstance(TableHeader header) {
    return (instance = new MultiColumnTable(header));
  }

  /**
   *
   * @param header TableHeader
   * @param numberOfRecord int
   * @return MultiColumnTable
   */
  public static final MultiColumnTable createInstance(TableHeader header,
      int numberOfRecord) {
    return (instance = new MultiColumnTable(header, numberOfRecord));
  }

  /**
   *
   * @return Row
   */
  public synchronized Row newRow() {
    ensureCapacity(size + 1); // Increments modCount!!
    return new Row(size++);
  }

  /**
   *
   * @param primaryKey String
   * @return Row
   */
  public synchronized Row getRow(String primaryKey) {
    Row r = null;
    try {
      r = new Row(primaryKey);
    }
    catch (RecordNotFoundException e) {
    }
    return r;
  }

  /**
   *
   * @return int
   */
  public int count() {
    return size;
  }

  /**
   *
   * @param row Row
   * @return Object
   */
  public synchronized void remove(Row row) {

    int rowIndex = row.getCurrentRowIndex();

    int numMoved = size - rowIndex - 1;
    if (numMoved > 0) {
      System.arraycopy(rows, rowIndex + 1, rows, rowIndex, numMoved);
    }
    rows[--size] = null;
  }

  /**
   *
   * @param criteria Criteria
   */
  public synchronized void remove(Criteria criteria) {
    for (int i = 0; i < size; i++) {
      Row r = new Row(i);
      if (criteria.matchCriteria(r)) {
        remove(r);
      }
    }
  }

  /**
   *
   * @param columnName String
   * @param value String
   * @return List
   */
  public List search(String columnName, String value) {
    int colIndex = header.getIndex(columnName);
    List result = new ArrayList();
    for (int i = 0; i < size; i++) {
      if (rows[i][colIndex] != null && rows[i][colIndex].equals(value)) {
        result.add(new Row(i));
      }
    }
    return result;
  }

  /**
   *
   * @param criteria Criteria
   * @return List
   */
  public List search(Criteria criteria) {
    List result = new ArrayList();
    for (int i = 0; i < size; i++) {
      Row r = new Row(i);
      if (criteria.matchCriteria(r)) {
        result.add(r);
      }
    }
    return result;
  }

  /**
   *
   * @param row String[]
   */
  public synchronized int newRow(String[] row) {
    ensureCapacity(size + 1);
    int currntRow = size++;
    for (int i = 0; i < row.length; i++) {
      rows[currntRow][i] = row[i];
    }
    return currntRow;
  }

  /**
   *
   * @param row String[]
   */
  public synchronized int newRow(Map row) {
    ensureCapacity(size + 1);
    return new Row(row, size++).getCurrentRowIndex();
  }

  /**
   *
   */
  public void flush() {
    rows = new String[50][header.count()];
    size = 0;
  }

  /**
   *
   * @param minCapacity
   */
  private void ensureCapacity(int minCapacity) {
    int oldCapacity = rows.length;
    if (minCapacity > oldCapacity) {
      int newCapacity = (oldCapacity * 3) / 2 + 1;
      if (newCapacity < minCapacity) {
        newCapacity = minCapacity;
      }
      rows = copyOf(rows, newCapacity);
    }
  }

  /**
   *
   * @param original
   * @param newLength
   * @param newType
   * @return
   */

  private String[][] copyOf(String[][] original, int newLength) {
    String[][] copy = new String[newLength][header.count()];
    System.arraycopy(original, 0, copy, 0, Math.min(original.length,
        newLength));
    return copy;
  }

  /**
   *
   * @return String
   */
  public String toString() {
    StringBuffer buff = new StringBuffer(header.toString());
    buff.append("\n");

    for (int i = 0; i < size; i++) {
      for (int j = 0; j < header.count(); j++) {
        buff.append(rows[i][j]).append("|");
      }
      buff.setCharAt(buff.lastIndexOf("|"), '\n');
    }
    return buff.toString();

  }

  /**
   *
   * <p>Title: </p>
   * <p>Description: </p>
   * <p>Copyright: Copyright (c) 2004</p>
   * <p>Company: </p>
   * @author Saji Venugopalan
   * @version 1.0
   */
  public static class Row {

    private int currentRow = 0;
    private int curClmn = 0;

    /**
     *
     * @param currentRow int
     */
    public Row(int currentRow) {
      this.currentRow = currentRow;
    }

    /**
     *
     * @param row String[]
     * @param currentRow int
     */
    public Row(String[] row, int currentRow) {
      this.currentRow = currentRow;
      setValue(row);
    }

    /**
     * Row
     *
     * @param map Map
     * @param i int
     */
    public Row(Map row, int i) {
      this.currentRow = currentRow;
      setValue(row);
    }

    /**
     *
     */
    public Row() {
      this(size++);
    }

    /**
     *
     * @param primaryKey String
     * @throws RecordNotFoundException
     */
    public Row(String primaryKey) throws RecordNotFoundException {

      int index = rowIndexForSearch(primaryKey);

      if (index != -1) {
        this.currentRow = index;
      }
      else {
        throw new RecordNotFoundException("No record found for ["
                                          + primaryKey + "]");
      }
    }

    /**
     *
     * @param row String[]
     */
    public void setValue(String[] row) {
      for (int i = 0; i < row.length; i++) {
        setValue(row[i]);
      }
    }

    /**
     *
     * @param row Map
     */
    public void setValue(Map row) {
      for (Iterator iter = row.keySet().iterator(); iter.hasNext(); ) {
        String item = (String) iter.next();
        setValue(item, (String) row.get(item));
      }
    }

    /**
     *
     * @param value
     */
    public void setValue(String value) {
      if (curClmn == 0 && currentRow != 0) {
        checkUnique(value);
      }
      rows[currentRow][curClmn++] = value;
    }

    /**
     *
     * @param value
     */
    private void checkUnique(String value) {
      int dupRow = rowIndex(value);
      if (dupRow != -1) {
        currentRow = dupRow;
        size--;
      }
    }

    /**
     *
     * @param columnName
     * @param value
     */
    public void setValue(String columnName, String value) {

      int cClmn = header.getIndex(columnName);

      if (cClmn == 0 && currentRow != 0) {
        checkUnique(value);
      }
      rows[currentRow][cClmn] = value;
    }

    /**
     *
     * @return int
     */
    public int getCurrentRowIndex() {
      return currentRow;
    }

    /**
     *
     * @param primaryKey
     * @return
     */
    private int rowIndex(String primaryKey) {
      int index = -1;
      for (int i = 0; i < size - 1; i++) {
        if (rows[i][0] != null && rows[i][0].equals(primaryKey)) {
          index = i;
          break;
        }
      }
      return index;
    }

    /**
     *
     * @param primaryKey
     * @return
     */
    private int rowIndexForSearch(String primaryKey) {
      int index = -1;
      for (int i = 0; i < size; i++) {
        if (rows[i][0] != null && rows[i][0].toString().equals(primaryKey)) {
          index = i;
          break;
        }
      }
      return index;
    }

    /**
     *
     * @param columnName
     * @return
     */
    public String getValue(String columnName) {
      return (String) rows[currentRow][header.getIndex(columnName)];
    }

    /**
     *
     * @param columnName
     * @return
     */
    public String getValue(int colIndex) {
      return (String) rows[currentRow][colIndex];
    }

    /**
     *
     * @return String
     */
    public String toString() {
      StringBuffer sb = new StringBuffer("[");

      for (int i = 0; i < header.count(); i++) {
        sb.append(rows[currentRow][i]).append(",");
      }

      sb.deleteCharAt(sb.length() - 1);
      sb.append("]");
      return sb.toString();
    }

  }

  /**
   *
   * <p>Title: </p>
   * <p>Description: </p>
   * <p>Copyright: Copyright (c) 2004</p>
   * <p>Company: </p>
   * @author Saji Venugopalan
   * @version 1.0
   */
  private static class RecordNotFoundException
      extends Exception {
    public RecordNotFoundException(String message) {
      super(message);
    }

  }

  /**
   *
   * <p>Title: </p>
   * <p>Description: </p>
   * <p>Copyright: Copyright (c) 2004</p>
   * <p>Company: </p>
   * @author Saji Venugopalan
   * @version 1.0
   */
  public interface Criteria {
    public boolean matchCriteria(Row row);
  }

}
