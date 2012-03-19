package sparrow.elt.core;

import sparrow.elt.core.context.SparrowApplicationContext;
import sparrow.elt.core.dao.impl.PostProcessAcknowledgement;
import sparrow.elt.core.exception.EventNotifierException;
import sparrow.elt.core.exception.RequestProcessException;
import sparrow.elt.core.log.SparrowLogger;
import sparrow.elt.core.log.SparrowrLoggerFactory;
import sparrow.elt.core.monitor.EndCycleMonitor;
import sparrow.elt.core.util.SparrowUtil;
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
class AbstractRequestFinalizer {

  private RequestFinalizerAdapter rf;

  private static final SparrowLogger logger = SparrowrLoggerFactory.
      getCurrentInstance(
      AbstractRequestFinalizer.class);

  /**
   *
   * @param context SparrowApplicationContext
   */
  AbstractRequestFinalizer(SparrowApplicationContext context) {

  }

  /**
   *
   * @param resultHolder SpeDataarResultHolder
   * @throws RequestProcessException
   */
  public final void processRequest(SparrowResultHolder resultHolder) throws
      RequestProcessException {

  //  long start = System.currentTimeMillis();

    try {
      rf.isError = false;

      if (!resultHolder.isIgnoreRequest()) {
        rf.processRequest(resultHolder);
      }
    }
    catch (Exception ex) {
      throw new RequestProcessException(ex);
    }
    finally {

      /**System.out.println("Total Write Time : [" +
                            SparrowUtil.printDriverValue(resultHolder.
             getDataOutputHolder().getDriverRow()) + "]-" +
                            (System.currentTimeMillis() - start));
         System.out.println("Total Time : [" +
                            SparrowUtil.printDriverValue(resultHolder.
             getDataOutputHolder().getDriverRow()) + "]-" +
                            (System.currentTimeMillis() -
                             resultHolder.getStartTime()));
       **/
      if (resultHolder.isEndCycle()) {
        EndCycleMonitor.getInstance().increment();
      }
      executePostProcess(resultHolder);

      if (logger.isReqTraceEnabled()) {
        logger.log("END Request:[" +
                   SparrowUtil.printDriverValue(resultHolder.getDataOutputHolder().
                                              getDriverRow()) + "]",
                   SparrowLogger.REQ_TRACE);
      }
      // logger.log("END Request:[" + SparrowUtil.printDriverValue(resultHolder.getDriverResult()) + "]", SparrowLogger.INFO);

      resultHolder.destroy();
      resultHolder = null;
    }
  }

  /**
   *
   * @param resultHolder SparrowResultHolder
   */
  private void executePostProcess(SparrowResultHolder resultHolder) {
    PostProcessAcknowledgement ppc = resultHolder.getDataOutputHolder().
        getDriverRow().
        getPostProcessAcknowledgement();
    if (ppc != null) {
      int flag = PostProcessAcknowledgement.SUCCESS;

      if (rf.isError || (resultHolder.isIgnoreRequest()==true && resultHolder.isSoftRejection()==false)) {
        flag = PostProcessAcknowledgement.FAILED;
      }
      if (resultHolder.isIgnoreRequest()==true && resultHolder.isSoftRejection()== true) {
        flag = PostProcessAcknowledgement.IGNORED;
      }
      ppc.acknowledge(flag);
    }
  }

  /**
   *
   * @param rf RequestFinalizer
   */
  public void setRequestFinalizer(RequestFinalizer rf) {
    this.rf = (RequestFinalizerAdapter) rf;
  }

  /**
   *
   * @param writer DataWriter[]
   */
  public void setWriters(DataWriter[] writer) {
    rf.setWriters(writer);
  }

  /**
   *
   */
  public final void endCycle() throws EventNotifierException {
    rf.endCycle();
  }

  /**
   *
   */
  public final void beginCycle() throws EventNotifierException {
    rf.beginCycle();
  }

  public final void endOfProcess(int flag) throws EventNotifierException {
    rf.endOfProcess(flag);
  }

}
