package sparrow.elt.core.util;

import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */
public final class MultiColumnHashTable {

  private static TableHeader header = null;
  private static final float DEFAULT_LOAD_FACTOR = 0.75f;
  private static final int DEFAULT_CAPACITY = 11;
  private int threshold;
  private final float loadFactor;
  private final int initialCapacity;
  transient int size;
  private static Row[] rows;

  /**
   *
   */
  private static MultiColumnHashTable instance = null;

  /**
   *
   * @param header TableHeader
   */
  private MultiColumnHashTable(TableHeader header) {
    this(header, DEFAULT_CAPACITY, DEFAULT_LOAD_FACTOR);
  }

  /**
   *
   * @param header TableHeader
   * @param initialCapacity int
   */
  private MultiColumnHashTable(TableHeader header, int initialCapacity) {
    this(header, initialCapacity, DEFAULT_LOAD_FACTOR);
  }

  /**
   *
   * @param header TableHeader
   * @param initialCapacity int
   * @param loadFactor float
   */
  private MultiColumnHashTable(TableHeader header, int initialCapacity,
                               float loadFactor) {
    rows = new Row[initialCapacity];
    this.initialCapacity = initialCapacity;
    this.loadFactor = loadFactor;
    this.header = header;
    threshold = (int) (initialCapacity * loadFactor);
  }

  /**
   *
   * @return MultiColumnTable
   */
  public static final MultiColumnHashTable getInstance() {
    return instance;
  }

  /**
   *
   * @param header TableHeader
   * @return MultiColumnHashTable
   */
  public static final MultiColumnHashTable createInstance(TableHeader header) {
    return (instance = new MultiColumnHashTable(header));
  }

  /**
   *
   * @param header TableHeader
   * @param numberOfRecord int
   * @return MultiColumnHashTable
   */
  public static final MultiColumnHashTable createInstance(TableHeader header,
      int numberOfRecord) {
    return (instance = new MultiColumnHashTable(header, numberOfRecord));
  }

  /**
   *
   * @param header TableHeader
   * @param numberOfRecord int
   * @return MultiColumnHashTable
   */
  public static final MultiColumnHashTable createInstance(TableHeader header,
      int numberOfRecord, float loadFactor) {
    return (instance = new MultiColumnHashTable(header, numberOfRecord,
                                                loadFactor));
  }

  /**
   *
   */
  public synchronized void flush() {
    clear();
  }

  /**
   *
   * @return int
   */
  public synchronized int size() {
    return size;
  }

  /**
   *
   * @return int
   */
  public synchronized int count() {
    return size;
  }

  /**
   *
   * @param key String
   * @return Row
   */
  public synchronized Row getRow(String key) {
    int idx = getIndex(key);
    int keyHash = key.hashCode();
    for (Row e = rows[idx]; e != null; e = e.nextRow) {
      if ( (e.hashCode() == keyHash) && e.getUniqueKey().equals(key)) {
        return e;
      }
    }
    return null;
  }

  /**
   *
   * @param key Object
   * @return int
   */
  private static int hash(String key) {
    int h = key.hashCode() & 0x7FFFFFFF;
    return h;
  }

  /**
   *
   * @param key String
   * @return int
   */
  private static int getIndex(String key) {
    int hashCode = hash(key);
    return hashCode % rows.length;
  }

  /**
   *
   * @param values String[]
   */
  public synchronized void newRow(String[] values) {

    String key = values[0];
    int idx = getIndex(key);

    for (Row rw = rows[idx]; rw != null; rw = rw.nextRow) {
      if ( (rw.hashCode() == key.hashCode()) && rw.getUniqueKey().equals(key)) {
        rw.setRow(values);
        return;
      }
    }

    if (++size > threshold) {
      rehash();
      idx = getIndex(key);
    }

    Row r = new Row(values);

    r.nextRow = rows[idx];
    rows[idx] = r;
  }

  /**
   *
   * @param r Row
   */
  public synchronized void newRow(Row r) {
    newRow(r.getRow());
  }

  /**
   *
   * @return Row
   */
  public synchronized Row newRow() {
    return new Row(new String[header.count()]);
  }

  /**
   *
   */
  public synchronized void clear() {
    if (size > 0) {
      Arrays.fill(rows, null);
      size = 0;
    }
  }

  /**
   *
   */
  private void rehash() {
    int oldCapacity = rows.length;
    Row oldMap[] = rows;

    int newCapacity = oldCapacity * 2 + 1;
    Row newMap[] = new Row[newCapacity];

    threshold = (int) (newCapacity * loadFactor);
    rows = newMap;

    for (int i = oldCapacity; i-- > 0; ) {
      for (Row old = oldMap[i]; old != null; ) {
        Row e = old;
        old = old.nextRow;

        int index = hash(e.getUniqueKey()) % newCapacity;
        e.nextRow = newMap[index];
        newMap[index] = e;
      }
    }
  }

  /**
   *
   * @param key String
   */
  public synchronized void remove(String key) {
    int idx = getIndex(key);
    for (Row rw = rows[idx], prev = null; rw != null; prev = rw,
         rw = rw.nextRow) {
      if ( (rw.hashCode() == key.hashCode()) && rw.getUniqueKey().equals(key)) {
        if (prev != null) {
          prev.nextRow = rw.nextRow;
        }
        else {
          rows[idx] = rw.nextRow;
        }
        size--;
        rw = null;
        return;
      }
    }
  }

  /**
   *
   * @param key String
   */
  public synchronized void remove(Criteria criteria) {
    HashRowIterator r = new HashRowIterator();
    for (; r.hasNext(); ) {
      Row item = r.next();
      if (criteria.matchCriteria(item)) {
        r.remove();
      }
    }
  }

  /**
   *
   * @return RowIterator
   */
  public RowIterator getIterator() {
    return new HashRowIterator();
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
  private class HashRowIterator
      implements RowIterator {

    private Row[] rws = MultiColumnHashTable.this.rows;
    private int index = rows.length;
    private Row r = null;
    private Row lastReturned = null;

    HashRowIterator() {
    }

    /**
     *
     * @return boolean
     */
    public boolean hasNext() {
      Row r1 = r;
      int i = index;
      Row rt[] = rws;
      while (r1 == null && i > 0) {
        r1 = rt[--i];
      }
      r = r1;
      index = i;
      return r1 != null;
    }

    /**
     *
     * @return Row
     */
    public Row next() {
      Row r1 = r;
      int i = index;
      Row rt[] = rws;

      while (r1 == null && i > 0) {
        r1 = rt[--i];
      }
      r = r1;
      index = i;
      if (r1 != null) {
        Row r2 = lastReturned = r;
        r = r2.nextRow;
        return r2;
      }
      throw new NoSuchElementException("MutiColumnHashtable HashRowIterator");
    }

    /**
     *
     */
    final void remove() {

      if (lastReturned == null) {
        throw new IllegalStateException("lastReturned is null");
      }

      synchronized (MultiColumnHashTable.this) {
        Row[] rws = MultiColumnHashTable.this.rows;
        int index = getIndex(lastReturned.getUniqueKey());
        for (Row rw = rws[index], prev = null; rw != null;
             prev = rw, rw = rw.nextRow) {
          if (rw == lastReturned) {
            if (prev == null) {
              rws[index] = rw.nextRow;
            }
            else {
              prev.nextRow = rw.nextRow;
            }
            size--;
            lastReturned = null;
            return;
          }
        }
        throw new ConcurrentModificationException();
      }
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
  public interface RowIterator {
    boolean hasNext();

    Row next();
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

    Row nextRow;

    private String[] row;

    private Row(String[] row) {
      this.row = row;
    }

    private Row(int arraySize) {
      this.row = new String[arraySize];
    }

    public String getValue(String columnName) {
      return row[header.getIndex(columnName)];
    }

    public void setValue(String columnName, String value) {
      row[header.getIndex(columnName)] = value;
    }

    String[] getRow() {
      return row;
    }

    void setRow(String[] row) {
      this.row = row;
    }

    public String getUniqueKey() {
      return row[0];
    }

    public int hashCode() {
      return getUniqueKey().hashCode();
    }

    public String toString() {
      StringBuffer sb = new StringBuffer();
      for (int i = 0; i < row.length; i++) {
        sb.append(row[i]).append("|");
      }
      sb.setCharAt(sb.lastIndexOf("|"), ' ');
      return sb.toString();
    }
  }

  /**
   *
   * @return String
   */
  public synchronized String toString() {
    StringBuffer sb = new StringBuffer();
    RowIterator it = new HashRowIterator();
    sb.append(header).append("\n");

    while (it.hasNext()) {
      Row r = it.next();
      sb.append(r).append("\n");
    }
    return sb.toString();
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
    public boolean matchCriteria(Row r);
  }

}
