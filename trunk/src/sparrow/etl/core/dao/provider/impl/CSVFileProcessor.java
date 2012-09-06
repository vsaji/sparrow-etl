package sparrow.etl.core.dao.provider.impl;

import java.io.File;
import java.io.IOException;
import java.io.Reader;

import sparrow.etl.core.context.SparrowContext;
import sparrow.etl.core.dao.impl.ColumnHeader;
import sparrow.etl.core.dao.metadata.DataTypeResolver;
import sparrow.etl.core.dao.metadata.SparrowResultMetaDataFactory;
import sparrow.etl.core.dao.metadata.SparrowResultSetMetaData;
import sparrow.etl.core.exception.DataException;
import sparrow.etl.core.exception.TypeCastException;
import sparrow.etl.core.report.RejectedEntry;
import sparrow.etl.core.util.FileProcessInfo;
import sparrow.etl.core.util.FileProcessor;


/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */
public class CSVFileProcessor
    extends FileProcessor {

  private final SparrowContext context;
  private final FileProcessInfo info;
  private CSVReader reader;
  private int headerColLen;
  private int reqColLen;
  private DataTypeResolver[] dtr = null;
  private final ColumnHeader header;

  /**
   *
   * @param context SparrowContext
   * @param info FileProcessInfo
   */
  public CSVFileProcessor(SparrowContext context, FileProcessInfo info) {
    super(context, info);
    this.context = context;
    this.info = info;
    this.header = loadHeader();
  }

  public void reset() {
  }

  /**
   *
   * @param r Reader
   */
  public void setReader(Reader r) {
    this.reader = (info.isFixedLengthFile()) ?
        new FixedLengthReader(r, header, info.isTrimValue()) :
        new CSVReader(r, info.getFileDelimiter(), info.isTrimValue());
  }

  /**
   * getHeader
   *
   * @param file File
   * @return String[]
   */
  public ColumnHeader getHeader() {
    reader.rollToStartingPoint(info.getStartLineNumber());
    return header;
  }

  /**
   * getHeader
   *
   * @param file File
   * @return String[]
   */
  public ColumnHeader loadHeader() {

    SparrowResultSetMetaData resultSetMetaData = SparrowResultMetaDataFactory.
        getSparrowResultSetMetaData(info.
                                  getColumnDefinitionValue(), info.getName());
    dtr = resultSetMetaData.getAllDataTypeResolvers();
    this.headerColLen = dtr.length;
    this.reqColLen = resultSetMetaData.getDataTypeResolvers().length;
    return new ColumnHeader(resultSetMetaData);
  }

  /**
   * processRecord
   *
   * @param row String
   * @return String[]
   */
  public Object[] getRow() throws DataException {

    String[] strRow = reader.getLine();
    int trace = 0;

    if (strRow == null) {
      return strRow;
    }

    Object[] row = null;

    if ( (!info.isIgnoreColMismatch() && strRow.length != headerColLen) ||
        (info.isIgnoreColMismatch() && strRow.length < headerColLen)) {
      try {

        RejectedEntry re = new RejectedEntry();
        re.setPrimaryValue(String.valueOf(reader.getLineNumber()));
        re.setRejectedEntry(reader.getLastProcessedLine());
        re.setRejectReason("Column Length Mismatch");
        re.setReporterSource(info.getName());
        reportRejection(info.getName(), re);

        row = getRow();
        trace = 1;
      }
      catch (Exception ex) {
        logger.error("Column Length Mismatch - Rejection failed for [" +
                     reader.getLineNumber() + "]");
        row = null;
        trace = 2;
      }
      if (row == null && trace == 2) {
        row = getRow();
        trace = 3;
      }
    }

    row = (row == null && trace == 0) ? getDataTypeResolvedRow(strRow) : row;

    if (row != null && row.length == 0) {
      row = getRow();
    }

    return row;
  }

  /**
   *
   * @param values String[]
   * @return Object[]
   */
  private Object[] getDataTypeResolvedRow(String[] values) {
    Object[] o = new Object[reqColLen];
    int i = 0;
    try {
      for (int j = i; i < headerColLen; i++) {
        if (!dtr[i].isExcludeColumn()) {
          o[j++] = dtr[i].getTypeCastedValue(values[i]);
        }
      }
    }
    catch (TypeCastException tce) {
      RejectedEntry re = new RejectedEntry();
      re.setPrimaryValue(String.valueOf(reader.getLineNumber()));
      re.setRejectedEntry(reader.getLastProcessedLine());
      re.setRejectReason("Column=" + dtr[i].getColumnAttributes().getColumnName() +
                         ":" + tce.getErrorCode() + "-" +
                         tce.getErrorDescription());
      re.setReporterSource(info.getName());
      try {
        reportRejection(info.getName(), re);
        o = new Object[0];
      }
      catch (Exception ex) {
        logger.error("Exception while invoking reportRejection Method", ex);
        o = new Object[0];
      }
    }
    return o;
  }

  /**
   * validate
   *
   * @param file File
   * @return boolean
   */
  public boolean validate(File file) {
    return false;
  }

  /**
   * close
   */
  public void close() {
    try {
      if (reader != null) {
        reader.close();
      }
    }
    catch (IOException ex) {
      logger.error("Exception occured while closing the CSVReader instance", ex);
    }
  }

  /**
   * getUnprocessedRow
   *
   * @return Object
   */
  public Object getUnprocessedRow() {
    return reader.getLastProcessedLine();
  }

}
