package sparrow.etl.core;

import java.util.Hashtable;

import sparrow.etl.core.exception.DataWriterException;
import sparrow.etl.core.exception.EventNotifierException;
import sparrow.etl.core.log.SparrowLogger;
import sparrow.etl.core.log.SparrowrLoggerFactory;
import sparrow.etl.core.util.Constants;
import sparrow.etl.core.util.Sortable;
import sparrow.etl.core.writer.DataWriter;


/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public abstract class RequestFinalizerAdapter
    implements RequestFinalizer {

  DataWriter[] writers = null;
  String[] writerNames, writerDependent = null;
  String onError = null;
  boolean isError = false;

  private Hashtable writerStatusTracter = null;

  protected static final SparrowLogger logger = SparrowrLoggerFactory.
      getCurrentInstance(
      RequestFinalizerAdapter.class);

  /**
   *
   */
  public RequestFinalizerAdapter() {
    writerStatusTracter = new Hashtable();
  }

  /**
   *
   * @param writer DataWriter[]
   */
  public void setWriters(DataWriter[] writer) {
    this.writers = writer;
    this.onError = (writer.length > 0) ?
        writers[0].getWriterConfig().getOnError() : Constants.WRITER_OE_IGNORE;
    writerNames = new String[writers.length];
    writerDependent = new String[writers.length];

    for (int i = 0; i < writers.length; i++) {
      writerNames[i] = writers[i].getWriterConfig().getName();
      writerDependent[i] = writers[i].getWriterConfig().getDepends();
    }
  }

  /**
   * getPriority
   *
   * @return int
   */
  public int getPriority() {
    return Sortable.PRIORITY_MEDIUM;
  }

  /**
   *
   * @param pos int
   * @param errWriterName String
   * @param exp Exception
   * @throws DataWriterException
   */
  void notifyError(int pos, String errWriterName, Exception exp) throws
      DataWriterException {
    isError = true;
    for (int j = pos; j > 0; j--) {
      writers[j].onError(errWriterName, exp);
    }
  }

  /**
   *
   */
  public final void endCycle() throws EventNotifierException {
    for (int j = 0; j < writers.length; j++) {
      writers[j].endCycle();
    }
  }

  /**
   *
   */
  public final void beginCycle() throws EventNotifierException {
    for (int j = 0; j < writers.length; j++) {
      writers[j].beginCycle();
    }
  }

  /**
   * endOfProcess
   */
  public void endOfProcess(int flag) throws EventNotifierException {
    for (int j = 0; j < writers.length; j++) {
      writers[j].endOfProcess(flag);
    }

  }

  /**
   *
   * @param writerName String
   * @param dependent String
   * @return boolean
   */
  protected boolean isDependentFailed(String dependent) {
    if (dependent != null && dependent.trim().equals("") == false) {
      String status = (String) writerStatusTracter.get(dependent);
      return (DataWriter.WRITE_FAIL.equals(status));
    }
    else {
      return false;
    }
  }

  /**
   *
   */
  protected void clearStatus() {
    writerStatusTracter.clear();
  }

  /**
   *
   * @param writerName String
   * @param status String
   */
  protected void logStatus(String writerName, String status) {
    writerStatusTracter.put(writerName, status);
  }

  /**
   *
   * @throws DataWriterException
   */
  void notifyEndRequest() throws DataWriterException {

    String errWriterName = null;

    for (int j = 0; j < writers.length; j++) {
      if (!writers[j].endRequest()) {
        if (Constants.WRITER_OE_FAIL_ALL.equals(onError)) {
          errWriterName = writers[j].getWriterConfig().getName();
          break;
        }
      }
    }

    if (errWriterName != null) {
      notifyError(writers.length - 1, errWriterName,
                  new DataWriterException("endRequest Returns false [" +
                                          errWriterName + "]"));
    }
  }

}
