package sparrow.etl.core.writer;

import sparrow.etl.core.config.SparrowDataWriterConfig;
import sparrow.etl.core.exception.DataWriterException;
import sparrow.etl.core.exception.EventNotifierException;
import sparrow.etl.core.util.Sortable;
import sparrow.etl.core.vo.DataOutputHolder;

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */

public class DataWriterSynchronizeWrapper
    implements DataWriter {

  final DataWriter actualInstance;
  final Object lock = new Object();

  public DataWriterSynchronizeWrapper(DataWriter actualInstance) {
    this.actualInstance = actualInstance;
  }

  /**
   * destroy
   */
  public synchronized void destroy() {
    synchronized (lock) {
      actualInstance.destroy();
    }
  }


  /**
   * startRequest
   *
   * @return boolean
   */
  public synchronized boolean startRequest() throws DataWriterException {
    synchronized (lock) {
      return actualInstance.startRequest();
    }
  }



  /**
   * endRequest
   *
   * @return boolean
   */
  public synchronized boolean endRequest() throws DataWriterException {
    synchronized (lock) {
      return actualInstance.endRequest();
    }
  }

  /**
   * getWriterConfig
   *
   * @return SparrowDataWriterConfig
   */
  public SparrowDataWriterConfig getWriterConfig() {
    return actualInstance.getWriterConfig();
  }

  /**
   * initialize
   */
  public void initialize() {
    actualInstance.initialize();
  }

  /**
   * onError
   *
   * @param writerName String
   * @param ex Exception
   */
  public synchronized void onError(String writerName, Exception ex) throws
      DataWriterException {
    synchronized (lock) {
      actualInstance.onError(writerName, ex);
    }
  }


  /**
   * writeData
   *
   * @param data DataOutputHolder
   */
  public synchronized int writeData(DataOutputHolder data, int statusCode) throws
      DataWriterException {
    synchronized (lock) {
      return actualInstance.writeData(data, statusCode);
    }
  }

  /**
   * endCycle
   */
  public synchronized void endCycle() throws EventNotifierException {
    synchronized (lock) {
      actualInstance.endCycle();
    }
  }

  /**
   * endOfProcess
   *
   * @param flag int
   */
  public void endOfProcess(int flag) throws EventNotifierException {
    actualInstance.endOfProcess(flag);
  }

  /**
   * beginCycle
   */
  public void beginCycle() throws EventNotifierException {
    synchronized (lock) {
      actualInstance.beginCycle();
    }

  }

  /**
   * isInTransaction
   *
   * @return boolean
   */
  public boolean isInTransaction() {
    synchronized (lock) {
      return actualInstance.isInTransaction();
    }

  }

  /**
   * setInTransaction
   *
   * @param trans boolean
   */
  public void setInTransaction(boolean trans) {
    synchronized (lock) {
      actualInstance.setInTransaction(trans);
    }

  }

  /**
   * getPriority
   *
   * @return int
   */
  public int getPriority() {
    return Sortable.PRIORITY_ABOVE_LOW;
  }

  /**
   * beginApplication
   */
  public void beginApplication() throws EventNotifierException {
    synchronized (lock) {
      actualInstance.beginApplication();
    }
  }

  /**
   * endApplication
   */
  public void endApplication() throws EventNotifierException {
    synchronized (lock) {
      actualInstance.endApplication();
    }
  }
}
