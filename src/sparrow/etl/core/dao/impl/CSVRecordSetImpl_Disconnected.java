package sparrow.etl.core.dao.impl;

import java.util.ArrayList;
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
public class CSVRecordSetImpl_Disconnected
    implements CSVRecordSet {

  private List resultSet;
  private final CSVFileProcessor fProcessor;

  private long lineNumber = 1;
  private int fetchSize;
  private boolean fileFullRead = false;
  private ColumnHeader header = null;

  /**
   *
   */
  public CSVRecordSetImpl_Disconnected() {
    resultSet = new ArrayList();
    fileFullRead = true;
    this.fProcessor = null;
  }

  /**
   *
   * @param fManager FileManager
   * @param fetchSize int
   * @param header Header
   */
  public CSVRecordSetImpl_Disconnected(CSVFileProcessor fProcessor) {
    resultSet = new ArrayList();
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

    resultSet.clear();

    Object line[] = null;

    int i = 0;

    if (fetchSize == 0) {

      while (true) {

        line = fProcessor.getRow();

        if (line == null) {
          fileFullRead = true;
          break;
        }

        if (line.length == 0) {
          continue;
        }

        resultSet.add(new ResultRowImpl(header, line,
                                        fProcessor.getUnprocessedRow(),
                                        lineNumber++));

      }
    }
    else {

      while (true) {
        if (i++ < fetchSize) {
          line = fProcessor.getRow();

          if (line == null) {
            fileFullRead = true;
            break;
          }

          if (line.length == 0) {
            i--;
            continue;
          }
          resultSet.add(new ResultRowImpl(header, line,
                                          fProcessor.getUnprocessedRow(),
                                          lineNumber++));
        }
        else {
          break;
        }
      }
    }
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
    return new RowIteratorImpl();
  }

  /**
   *
   * @param index int
   * @return ResultRow
   */
  public ResultRow getRow(int index) {
    return (ResultRow) resultSet.get(index);
  }

  public ResultRow getFirstRow() {
    return getRow(0);
  }

  /**
   * getRowCount
   *
   * @return int
   */
  public int getRowCount() {
    return resultSet.size();
  }

  /**
   * close
   */
  public void close() {
    resultSet.clear();
  }

  /**
   * addRow
   *
   * @param ch ColumnHeader
   * @param row String[]
   */
  public void addRow(ColumnHeader ch, Object[] row) {
    resultSet.add(new ResultRowImpl(ch, row));
  }

  /**
   * getResult
   *
   * @return List
   */
  public List getResult() {
    return resultSet;
  }

  /**
   * setResult
   *
   * @param result List
   */
  public void setResult(List result) {
    resultSet = result;
  }

  /**
   *
   * @param result List
   */
  public void addResult(List result) {
    resultSet.addAll(result);
  }

  /**
   * getColumnHeaders
   *
   * @return ColumnHeader
   */
  /**
   * getColumnHeaders
   *
   * @return ColumnHeader
   */
  public ColumnHeader getColumnHeaders() {
    return header;
  }

  /**
   * addRow
   *
   * @param row ResultRow
   */
  public void addRow(ResultRow row) {
    resultSet.add(row);
  }

  /**
   *
   * @param rs RecordSet
   * @throws DataException
   */
  public void merge(RecordSet rs) throws DataException {
    if (rs.getType() == RecordSet.DISCONNECTED) {
      RecordSetCacheSupport rcs = (RecordSetCacheSupport) rs;
      addResult(rcs.getResult());
    }
    else {
      for (RowIterator ri = rs.iterator(); ri.hasNext(); ) {
        addRow(ri.next());
      }
    }
  }

  /**
   * getType
   *
   * @return int
   */
  public int getType() {
    return RecordSet.DISCONNECTED;
  }

  private class RowIteratorImpl
      implements RowIterator {

    private ResultRow[] rows = null;
    private int rowCount = 0;
    private int resultSetCount = 0;

    public RowIteratorImpl() {
      resultSetCount = resultSet.size();
      rows = (ResultRow[]) resultSet.toArray(new ResultRow[resultSetCount]);
    }

    public boolean hasNext() {
      return rowCount < resultSetCount;
    }

    public ResultRow next() {
      return rows[rowCount++];
    }
  }
}
