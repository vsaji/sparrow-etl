package sparrow.elt.core.dao.impl;

import sparrow.elt.core.exception.DataException;

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public interface RecordSet {

  public static final int DISCONNECTED = 1;
  public static final int CONNECTED = 2;

  public static final RecordSet EMPTY_RECORDSET = new RecordSetImpl_Disconnected();

  abstract void close() throws DataException;

  abstract RowIterator iterator();

  abstract ResultRow getRow(int index) throws DataException;

  abstract ResultRow getFirstRow() throws DataException;

  abstract int getRowCount() throws DataException;

  abstract ColumnHeader getColumnHeaders();

  abstract void merge(RecordSet rs)throws DataException;

  abstract int getType();

}
