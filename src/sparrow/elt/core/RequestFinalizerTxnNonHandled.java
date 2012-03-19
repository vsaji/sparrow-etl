package sparrow.elt.core;

import sparrow.elt.core.exception.DataWriterException;
import sparrow.elt.core.exception.RequestProcessException;
import sparrow.elt.core.transformer.DriverRowEventListener;
import sparrow.elt.core.vo.SparrowResultHolder;
import sparrow.elt.core.writer.DataWriter;

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */
public class RequestFinalizerTxnNonHandled
    extends RequestFinalizerAdapter
    implements RequestFinalizer {

  public RequestFinalizerTxnNonHandled() {
  }

  /**
   * processRequest
   *
   * @param resultHolder SparrowResultHolder
   */
  public void processRequest(SparrowResultHolder resultHolder) throws
      RequestProcessException {
    DriverRowEventListener drel = resultHolder.getDriverRowEventListener();
    try {

      clearStatus();

      if (drel.preFinalize()) {
        int i;
        Exception exp = null;
        String errWriterName = null;

        int statusCode = 0; //DataWriter.STATUS_SUCCESS

        for (i = 0; i < writers.length; i++) {

          try {
            if (drel.preWrite(writerNames[i]) && writers[i].startRequest()) {
              if (isDependentFailed(writerDependent[i]) == false) {

                statusCode = writers[i].writeData(resultHolder.
                                                  getDataOutputHolder(),
                                                  statusCode);
                drel.postWrite(writerNames[i], true);
                logStatus(writerNames[i], DataWriter.WRITE_SUCCESS);
              }
              else {
                logStatus(writerNames[i], DataWriter.WRITE_FAIL);
              }
            }
            else {
              logStatus(writerNames[i], DataWriter.WRITE_FAIL);
            }
          }
          catch (DataWriterException ex) {
            logStatus(writerNames[i], DataWriter.WRITE_FAIL);
            exp = ex;
            statusCode = DataWriter.STATUS_EXCEPTION;
            errWriterName = writerNames[i];
            drel.postWrite(writerNames[i], false);
          }
        }

        //----------Error reporting and Finalizing--------------------

        if (exp != null) {
          notifyError(i - 1, errWriterName, exp);
          drel.postFinalize(false);

        }
        else {
          notifyEndRequest();
          drel.postFinalize(true);
        }
        //            System.out.println("RFT3:"+SparrowUtil.printDriverValue(resultHolder.getDataOutputHolder().getDriverRow()) + "-"+ (System.currentTimeMillis()-start));
      }
      else {
        logger.debug("preFinalize returns false");
      }

    }
    catch (Exception ex) {
      throw new RequestProcessException(ex);
    }
  }

  /**
   *
   * @throws DataWriterException

  void notifyEndRequest() throws DataWriterException {

    for (int j = 0; j < writers.length; j++) {
      writers[j].endRequest();
    }
  }
   */
 }
