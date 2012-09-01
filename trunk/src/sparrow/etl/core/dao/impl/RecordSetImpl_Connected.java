package sparrow.etl.core.dao.impl;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import sparrow.etl.core.exception.DataException;
import sparrow.etl.core.exception.SparrowRuntimeException;
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
public class RecordSetImpl_Connected
    implements RecordSet, RecordSetCacheSupport {

  private int recordCount = -1;

  private ResultSet rs;
  private Connection con;
  private Statement pstmt;
  private int fetchSize;
  private final ColumnHeader header;

  private static final SparrowLogger logger = SparrowrLoggerFactory.
      getCurrentInstance(
      RecordSetImpl_Connected.class);

  /**
   *
   * @param rs ResultSet
   * @param con Connection
   * @param fetchSize int
   */
  public RecordSetImpl_Connected(ResultSet rs, Statement pstmt,
                                 Connection con, int fetchSize) {
    this.rs = rs;
    this.con = con;
    this.fetchSize = fetchSize;
    this.header = getColumnHeader(rs);
    this.pstmt = pstmt;
  }

  /**
   *
   * @param rs ResultSet
   * @param pstmt Statement
   * @param con Connection
   * @param fetchSize int
   */
  public RecordSetImpl_Connected(ResultSet rs, Statement pstmt,
                                 int fetchSize) {
    this.rs = rs;
    this.con = null;
    this.fetchSize = fetchSize;
    this.header = getColumnHeader(rs);
    this.pstmt = pstmt;
  }

  /**
   *
   * @param rs ResultSet
   * @return ColumnHeader
   */
  private ColumnHeader getColumnHeader(ResultSet rs) {
    try {
      return new ColumnHeader(rs.getMetaData());
    }
    catch (SQLException ex) {
    }
    return null;
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
  public ResultRow getRow(int index) throws DataException {
    ResultRowImpl r = null;
    try {
      if (fetchSize != 0) {
        if (index < fetchSize) {
          rs.absolute(index + 1);
        }
        else {
          throw new DataException("Record out of fetch size [row num:" + index +
                                  ",fetch size:" + fetchSize + "]");
        }
      }
      else {
        rs.absolute(index + 1);
      }
      r = new ResultRowImpl(header).setResultSet(rs);
    }
    catch (Exception ex) {
      throw new DataException(ex);
    }
    return r;
  }

  /**
   *
   * @throws DataException
   * @return ResultRow
   */
  public ResultRow getFirstRow() throws DataException {
    try {
      boolean b = rs.first();
      if (b) {
        ResultRowImpl r = new ResultRowImpl(this.header);
        r.setResultSet(rs);
        rs.beforeFirst();
        return r;
      }
      else {
        return null;
      }
    }
    catch (IOException ex) {
      throw new DataException("IOException occured while retriving first row",
                              ex);
    }
    catch (SQLException ex) {
      throw new DataException("SQLException occured while retriving first row",
                              ex);
    }
  }

  /**
   * getRowCount
   *
   * @return int
   */
  public int getRowCount() throws DataException {
    if (recordCount == -1) {
      try {
        recordCount = (rs.last()) ? rs.getRow() : 0;
        rs.beforeFirst();
        recordCount = (fetchSize != 0 && recordCount > fetchSize) ? fetchSize :
            recordCount;
      }
      catch (SQLException ex) {
        throw new DataException(
            "SQLException occured while getting the record count", ex);
      }
    }
    return recordCount;
  }

  /**
   * close
   */
  public void close() throws DataException {
    try {
      if (rs != null) {
        rs.close();
      }
      if (pstmt != null) {
        pstmt.close();
      }
      if (con != null) {
        con.close();
      }
    }
    catch (Exception ex) {
      if("pool not open".equalsIgnoreCase(ex.getMessage())){
        return;
      }
      logger.error(ex.toString(), ex);
      throw new DataException(ex);
    }
    finally {
      rs = null;
      pstmt = null;
      con = null;
     // header.destroy();
      logger.debug("ResultSet Closed.Connection Closed.");
    }

  }

  /**
   * getResult
   *
   * @return List
   */
  public List getResult() throws DataException {
    throw new DataException("Unsupported operation");
//    return null;
  }

  /**
   * setResult
   *
   * @param result List
   */
  public void setResult(List result) {
  }

  /**
   * addRow
   *
   * @param row String[]
   */
  public void addRow(ColumnHeader ch, Object[] row) {
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
   * addRow
   *
   * @param row ResultRow
   */
  public void addRow(ResultRow row) {
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

    private int rowCount = 0;
    private int resultSetCount = recordCount;

    public RowIteratorImpl() {
      try {
        rs.beforeFirst();
        resultSetCount = getRowCount();
      }
      catch (DataException ex) {
        throw new SparrowRuntimeException(ex);
      }
      catch (SQLException ex) {
        throw new SparrowRuntimeException(ex);
      }

    }

    /**
     *
     * @throws DataException
     * @return boolean
     */
    public boolean hasNext() throws DataException {
      boolean b = true;
      try {

        if (rs.isBeforeFirst() && rowCount > 0) {
          throw new DataException("Illegal Operation encountered");
        }

        b = (fetchSize != 0) ? (rowCount < fetchSize) : b;

        b = (b && rowCount < resultSetCount && rs.next());
        if (b) {
          rowCount++;
        }
      }
      catch (SQLException ex) {
        throw new DataException(ex);
      }
      return b;
    }

    /**
     *
     * @throws DataException
     * @return ResultRow
     */
    public ResultRow next() throws DataException {
      try {
        return new ResultRowImpl(header).setResultSet(rs);
      }
      catch (SQLException ex) {
        throw new DataException(ex);
      }
      catch (IOException ex) {
        throw new DataException(ex);
      }
    }
  }

  public void merge(RecordSet rs) throws DataException {
    throw new DataException("Connected RecordSet mode doesn't support merge function");
  }

   /**
    * getType
    *
    * @return int
    */
   public int getType() {
     return RecordSet.CONNECTED;
   }

}
