package sparrow.etl.core.dao.impl;

import java.util.List;

import sparrow.etl.core.dao.provider.impl.CSVFileProcessor;
import sparrow.etl.core.exception.DataException;


/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */
public class CSVRecordSetImpl_Connected
    implements CSVRecordSet {

  private boolean fileFullRead = false;
  private ColumnHeader header = null;
  private final CSVFileProcessor fProcessor;
  private int fetchSize;
  private long lineNumber = 1;

  /**
   *
   */
  public CSVRecordSetImpl_Connected() {
    fileFullRead = true;
    this.fProcessor = null;
  }

  /**
   *
   * @param fManager FileManager
   * @param fetchSize int
   * @param header Header
   */
  public CSVRecordSetImpl_Connected(CSVFileProcessor fProcessor) {

    this.fProcessor = fProcessor;
    this.header = header;
    fetchSize = fProcessor.getFileProcessInfo().getFetchSize();
    header = fProcessor.getHeader();
  }

  /**
   *
   * @param br BufferedReader
   * @throws IOException
   */
  public void populate() throws DataException {
  }

  /**
   * getColumnHeaders
   *
   * @return ColumnHeader
   */
  public ColumnHeader getColumnHeaders() {
    return header;
  }

  /**
   *
   * @return boolean
   */
  public boolean isFileFullyRead() {
    return fileFullRead;
  }

  /**
   *
   * @return RowIterator
   */
  public RowIterator iterator() {
    RowIterator ri = null;
    if (fProcessor != null) {
      ri = new RowIteratorImpl();
    }
    return ri;
  }

  /**
   *
   * @param index int
   * @return ResultRow
   */
  public ResultRow getRow(int index) {
    throw new UnsupportedOperationException(
        "getRow method is unsupported on [connected] result-wrap type.");
  }

  public ResultRow getFirstRow() {
    throw new UnsupportedOperationException(
        "getFirstRow method is unsupported on [connected] result-wrap type.");
  }

  /**
   * getRowCount
   *
   * @return int
   */
  public int getRowCount() {
    return -1;
  }

  /**
   * close
   */
  public void close() {
//    fProcessor.close();
  }

  /**
   * addRow
   *
   * @param ch ColumnHeader
   * @param row String[]
   */
  public void addRow(ColumnHeader ch, Object[] row) {
    throw new UnsupportedOperationException(
        "addRow method is not supported on [connected] result-wrap type.");
  }

  /**
   * getResult
   *
   * @return List
   */
  public List getResult() {
    throw new UnsupportedOperationException(
        "getResult method is not supported on [connected] result-wrap type.");
  }

  /**
   * setResult
   *
   * @param result List
   */
  public void setResult(List result) {
    throw new UnsupportedOperationException(
        "setResult method is not supported on [connected] result-wrap type.");
  }

  /**
   * addRow
   *
   * @param row ResultRow
   */
  public void addRow(ResultRow row) {
    throw new UnsupportedOperationException(
        "addRow method is not supported on [connected] result-wrap type.");
  }

  public void merge(RecordSet rs) throws DataException {
    throw new DataException(
        "Connected RecordSet mode doesn't support merge function");
  }

  /**
   * getType
   *
   * @return int
   */
  public int getType() {
    return RecordSet.CONNECTED;
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
  private class RowIteratorImpl
      implements RowIterator {

    private Object[] currRecord = null;
    private int i = 0;

    /**
     *
     */
    public RowIteratorImpl() {
    }

    public boolean hasNext() throws DataException {
      boolean hasNext = true;
      if (fetchSize == 0) {
        currRecord = fProcessor.getRow();
        hasNext = (currRecord != null);
        fileFullRead = !hasNext;
      }
      else {
        if (i != fetchSize) {
          currRecord = fProcessor.getRow();
          hasNext = (currRecord != null);
          fileFullRead = !hasNext;
        }
        else {
          hasNext = false;
        }
      }
      return hasNext;
    }

    public ResultRow next() {
      i++;
      return new ResultRowImpl(header, currRecord, fProcessor.getUnprocessedRow(),
                               lineNumber++);
    }
  }

}
