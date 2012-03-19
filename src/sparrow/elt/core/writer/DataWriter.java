package sparrow.elt.core.writer;

import sparrow.elt.core.config.SparrowDataWriterConfig;
import sparrow.elt.core.exception.DataWriterException;
import sparrow.elt.core.monitor.AppObserver;
import sparrow.elt.core.monitor.CycleObserver;
import sparrow.elt.core.monitor.EOPObserver;
import sparrow.elt.core.vo.DataOutputHolder;

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */

public interface DataWriter
    extends Cloneable, EOPObserver, CycleObserver, AppObserver {

  public static final int STATUS_SUCCESS = 0;
  public static final int STATUS_EXCEPTION = 1;
  public static final int STATUS_FAIL = 2;
  public static final boolean TRANS_SUCCESS = true;
  public static final boolean TRANS_FAIL = false;
  public static final String WRITE_SUCCESS = "S";
  public static final String WRITE_FAIL = "F";

  public abstract void initialize();

  public abstract int writeData(DataOutputHolder data, int statusCode) throws
      DataWriterException;

  public abstract void onError(String writerName, Exception ex) throws
      DataWriterException;

  public abstract boolean isInTransaction();

  public abstract void setInTransaction(boolean trans);

  public abstract void destroy();

  public abstract boolean startRequest() throws DataWriterException;

  public abstract boolean endRequest() throws DataWriterException;

  public abstract SparrowDataWriterConfig getWriterConfig();
}
