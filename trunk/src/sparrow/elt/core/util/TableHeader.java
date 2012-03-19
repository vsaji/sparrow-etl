package sparrow.elt.core.util;

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */
public class TableHeader {

  private String[] columns = null;

  private int size = 0;

  /**
   *
   * @param columnCount int
   */
  public TableHeader(int columnCount) {
    this.columns = new String[columnCount];
  }

  /**
   *
   */
  public TableHeader() {
    this(10);
  }

  /**
   *
   * @param columns String[]
   */
  public TableHeader(String[] columns) {
    this.columns = columns;
    size = columns.length;
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
   * @param header String
   */
  public void addHeader(String header) {
    ensureCapacity(size + 1);
    columns[size++] = header;
  }

  /**
   *
   * @param header
   * @return
   */
  public int getIndex(String header) {
    int index = 0;
    for (int i = 0; i < size; i++) {
      if (columns[i].equals(header)) {
        index = i;
        break;
      }
    }
    return index;
  }

  /**
   *
   * @param minCapacity
   */
  private void ensureCapacity(int minCapacity) {
    int oldCapacity = columns.length;
    if (minCapacity > oldCapacity) {
      int newCapacity = (oldCapacity * 3) / 2 + 1;
      if (newCapacity < minCapacity) {
        newCapacity = minCapacity;
      }
      columns = copyOf(columns, newCapacity);
    }
  }

  /**
   *
   * @param original
   * @param newLength
   * @return
   */
  private static String[] copyOf(String[] original, int newLength) {
    String[] copy = new String[newLength];
    System.arraycopy(original, 0, copy, 0, Math.min(original.length,
        newLength));
    return copy;
  }

  /**
   *
   * @return String
   */
  public String toString() {
    StringBuffer buff = new StringBuffer();
    for (int i = 0; i < size; i++) {
      buff.append(columns[i]).append("|");
    }
    buff.deleteCharAt(buff.lastIndexOf("|"));
    return buff.toString();
  }

}
