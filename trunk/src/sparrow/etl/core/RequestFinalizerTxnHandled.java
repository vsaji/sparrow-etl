package sparrow.etl.core;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;

import sparrow.etl.core.exception.DataWriterException;
import sparrow.etl.core.exception.RequestProcessException;
import sparrow.etl.core.log.SparrowLogger;
import sparrow.etl.core.log.SparrowrLoggerFactory;
import sparrow.etl.core.transaction.SparrowTransactionManager;
import sparrow.etl.core.transformer.DriverRowEventListener;
import sparrow.etl.core.vo.SparrowResultHolder;
import sparrow.etl.core.writer.DataWriter;


/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */
public class RequestFinalizerTxnHandled
    extends RequestFinalizerAdapter
    implements RequestFinalizer {

  private static final SparrowLogger logger = SparrowrLoggerFactory.
      getCurrentInstance(
      RequestFinalizer.class);

  private boolean status = DataWriter.TRANS_SUCCESS;

  /**
   *
   */
  public RequestFinalizerTxnHandled() {
  }

  /**
   * processRequest
   *
   * @param resultHolder SparrowResultHolder
   */
  public void processRequest(SparrowResultHolder resultHolder) throws
      RequestProcessException {
    DriverRowEventListener drel = resultHolder.getDriverRowEventListener();
    SparrowTransactionManager stm = null;

    try {

      resetStatus();
      clearStatus();

      if (drel.preFinalize()) {

        stm = SparrowTransactionManager.getTransactionManager();
        stm.begin();

        int i;
        Exception exp = null;
        String errWriterName = null;
        int statusCode = DataWriter.STATUS_SUCCESS;

        for (i = 0; i < writers.length; i++) {

          if (writers[i].isInTransaction() &&
              (status == DataWriter.TRANS_FAIL)) {
            drel.postWrite(writerNames[i], false);
            logStatus(writerNames[i], DataWriter.WRITE_FAIL);
            continue;
          }

          try {

            if (drel.preWrite(writerNames[i]) && writers[i].startRequest()) {
              if (isDependentFailed(writerDependent[i]) == false) {
                statusCode = writers[i].writeData(resultHolder.
                                                  getDataOutputHolder(),
                                                  statusCode);

                if (statusCode == DataWriter.STATUS_FAIL) {
                  checkAndFailTrans(drel, writers[i]);
                }
                else {
                  drel.postWrite(writerNames[i], true);
                  logStatus(writerNames[i], DataWriter.WRITE_SUCCESS);
                }
              }
              else { //isDependentFailed = false
                checkAndFailTrans(drel, writers[i]);
              }
            }
            else { //preWriter or startRequest = false
              checkAndFailTrans(drel, writers[i]);
            }
          }
          catch (DataWriterException ex) {
            logger.error("DataWriterException occured [" + writerNames[i] +
                         "] - Transaction will be ROLLED BACK ", ex);
            exp = ex;
            statusCode = DataWriter.STATUS_EXCEPTION;
            errWriterName = writerNames[i];
            checkAndFailTrans(drel, writers[i]);
          }
        }

        //----------Error reporting and Finalizing--------------------

        if (status == DataWriter.TRANS_FAIL) {
          notifyError(i - 1, errWriterName, exp);
          drel.postFinalize(false);
          setRollbackOnly(stm);
        }
        else {
          notifyEndRequest();
          drel.postFinalize(true);
        }
      }
    }
    catch (Exception ex) {
      setRollbackOnly(stm);
      throw new RequestProcessException(ex);
    }
    finally {
      commitTrans(stm);
      stm = null;
    }
  }

  /**
   *
   * @param stm SparrowTransactionManager
   */
  private void commitTrans(SparrowTransactionManager stm) {
    if (stm != null) {
      try {
        stm.commit();
      }
      catch (SystemException ex) {
        new RequestProcessException(ex);
      }
      catch (HeuristicRollbackException ex) {
        new RequestProcessException(ex);
      }
      catch (HeuristicMixedException ex) {
        new RequestProcessException(ex);
      }
      catch (RollbackException ex) {
        new RequestProcessException(ex);
      }
    }
  }

  /**
   *
   * @param stm SparrowTransactionManager
   */
  private void setRollbackOnly(SparrowTransactionManager stm) {
    if (stm != null) {
      try {
        stm.setRollbackOnly();
      }
      catch (SystemException ex) {
        new RequestProcessException(ex);
      }
    }
  }

  /**
   *
   * @param writer DataWriter
   */
  public void checkAndFailTrans(DriverRowEventListener drevl, DataWriter writer) {
    if (writer.isInTransaction() && status) {
      this.status = DataWriter.TRANS_FAIL;
    }
    String writerName = writer.getWriterConfig().getName();
    drevl.postWrite(writerName, false);
    logStatus(writerName, DataWriter.WRITE_FAIL);
  }

  /**
   *
   */
  public void resetStatus() {
    this.status = DataWriter.TRANS_SUCCESS;
  }

}
