package sparrow.etl.core.dao.impl;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import sparrow.etl.core.exception.DataException;
import sparrow.etl.core.log.SparrowLogger;
import sparrow.etl.core.log.SparrowrLoggerFactory;


/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */
public class RecordSetImpl_Disconnected
    implements RecordSet, RecordSetCacheSupport {

  private List resultSet = null;
  private ColumnHeader header = null;

  private static final SparrowLogger logger = SparrowrLoggerFactory.
      getCurrentInstance(
      RecordSetImpl_Disconnected.class);

  public RecordSetImpl_Disconnected() {
    this(null);
  }

  public RecordSetImpl_Disconnected(ResultRow row) {
    resultSet = new ArrayList();
    if (row != null) {
      resultSet.add(row);
    }
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
   * @param rs ResultSet
   * @param fetchSize int
   * @throws SQLException
   * @throws IOException
   */
  public void populate(ResultSet rs, int fetchSize) throws SQLException,
      IOException {

    if (rs == null) {
      return;
    }

    header = new ColumnHeader(rs.getMetaData());

    if (fetchSize == 0) {
      while (rs.next()) {
        ResultRowImpl row = new ResultRowImpl(header);
        row.setResultSet(rs);
        resultSet.add(row);
      }
    }
    else {

      int i = 0;

      while (rs.next()) {

        ResultRowImpl row = new ResultRowImpl(header);
        row.setResultSet(rs);
        resultSet.add(row);

        if ( (i + 1) == fetchSize) {
          break;
        }

        i++;
      }
    }
  }

  public RowIterator iterator() {
    return new RowIteratorImpl();
  }

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
    if (resultSet != null && resultSet.isEmpty() == false) {
      resultSet.clear();
      resultSet = null;
    }
    /** if(header!=null){
       header.destroy();
       header = null;
     }**/
    logger.debug("Cache Cleaned.");
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
    this.resultSet = result;
  }

  /**
   *
   * @param result List
   */
  public void addResult(List result) {
    if (result != null) {
      this.resultSet.addAll(result);
    }
  }

  /**
   *
   * @param result List
   */
  public void addResult(RecordSetCacheSupport result) {
    try {
      if (result != null && result.getRowCount() > 0) {
        this.resultSet.addAll(result.getResult());
      }
    }
    catch (DataException ex) {
      logger.error("addResult:Exception occured["+ex.getMessage()+"]", ex);
    }
  }


/**
 * addRow
 *
 * @param row String[]
 */
public void addRow(ColumnHeader ch, Object[] row) {
  resultSet.add(new ResultRowImpl(ch, row));
}

/**
 * addRow
 *
 * @param row ResultRow
 */
public void addRow(ResultRow row) {
  resultSet.add(row);
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

  /**
   * merge
   *
   * @param rs RecordSet
   */
  public void merge(RecordSet rs) throws DataException {
    if(rs.getType()==RecordSet.DISCONNECTED){
      RecordSetCacheSupport rcs = (RecordSetCacheSupport) rs;
      addResult(rcs.getResult());
    }else{
      for(RowIterator ri = rs.iterator();ri.hasNext();){
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

}
