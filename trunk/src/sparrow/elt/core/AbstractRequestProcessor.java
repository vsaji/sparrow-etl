package sparrow.elt.core;

import sparrow.elt.core.context.ContextVariables;
import sparrow.elt.core.context.SparrowApplicationContext;
import sparrow.elt.core.dao.impl.ResultRow;
import sparrow.elt.core.exception.DataException;
import sparrow.elt.core.exception.RequestProcessException;
import sparrow.elt.core.fifo.FIFO;
import sparrow.elt.core.log.SparrowLogger;
import sparrow.elt.core.log.SparrowrLoggerFactory;
import sparrow.elt.core.lookup.DataLookupManager;
import sparrow.elt.core.lookup.DataLookupManagerFactory;
import sparrow.elt.core.transformer.DataTransformer;
import sparrow.elt.core.util.Constants;
import sparrow.elt.core.util.SparrowUtil;
import sparrow.elt.core.vo.DataOutputHolder;
import sparrow.elt.core.vo.SparrowResultHolder;

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */
public abstract class AbstractRequestProcessor {

  protected SparrowApplicationContext context = null;
  protected boolean isLookupExist = true;

  private DataLookupManager lookupManager = null;

  private static int exceptionCount = 0;

  /**
   *
   */
  private static final SparrowLogger logger = SparrowrLoggerFactory.
      getCurrentInstance(
      AbstractRequestProcessor.class);

  /**
   *
   * @param context SparrowApplicationContext
   */
  AbstractRequestProcessor(SparrowApplicationContext context) {
    this.context = context;
    isLookupExist = context.getConfiguration().getDataLookUp().isLookUpExist();

    if (isLookupExist) {
      String loadType = context.getConfiguration().
          getDataLookUp().getLoadType();
      lookupManager = DataLookupManagerFactory.getDataLookupManager(loadType,
          context);
      lookupManager.initialize();
    }

  }

  /**
   *
   * @param resultHolder SparrowResultHolder
   */
  abstract void publish(SparrowResultHolder resultHolder);

  /**
   *
   * @param driverResult ResultRow
   * @param endCycle Object
   * @throws RequestProcessException
   */
  protected void processRequest(ResultRow driverResult, Object endCycle) throws
      RequestProcessException {

    long strtTime = System.currentTimeMillis();

    DataSetHolder dataSetHolder = new DataSetHolder();
    final SparrowResultHolder sparrowResultHolder = new SparrowResultHolder();

    dataSetHolder.setDriverRow(driverResult);

    DataTransformer dt = context.getDataTransformer();

    if (logger.isReqTraceEnabled()) {
      logger.log("START Request:[" +
                 SparrowUtil.printDriverValue(driverResult) + "]",
                 SparrowLogger.REQ_TRACE);
    }

    //  logger.log("START Request:[" + SparrowUtil.printDriverValue(driverResult)+"]", SparrowLogger.INFO);
    /********************Handling lookup start ************************/

    if (isLookupExist) {
      try {

        if (logger.isReqTraceEnabled()) {
          logger.log("Before lookup", SparrowLogger.REQ_TRACE);
        }
        // long start = System.currentTimeMillis();
        lookupManager.loadLookupResult(dataSetHolder,
                                       dt.getDriverRowEventListener());
        // long end = System.currentTimeMillis();

        // System.out.println("Total Lookup time:"+SparrowUtil.printDriverValue(driverResult) + "-"+ (end-start));
        if (logger.isReqTraceEnabled()) {
          logger.log("After lookup", SparrowLogger.REQ_TRACE);
        }

      }
      catch (DataException ex1) {
        logger.error("DataException occured while running look-up:[" +
                     SparrowUtil.printDriverValue(driverResult) + "]", ex1);
      }
    }
    else {

      SparrowUtil.addResultAsKeyValue(dataSetHolder.getDriverRow(),
                                    Constants.DRIVER,
                                    dataSetHolder.getDataSetAsKeyValue());

      /** dataSetHolder.getLookupResults().put(Constants.DRIVER,
                                            new RecordSetImpl_Disconnected(
           dataSetHolder.getDriverRow()));
       **/
    }

    /********************End of handling lookup ************************/

    DataOutputHolder resultHolder = null;

    try {
      dt.setDriverRow(driverResult);

      if (logger.isReqTraceEnabled()) {
        logger.log("Before Enrich", SparrowLogger.REQ_TRACE);
      }

      // long start = System.currentTimeMillis();
      resultHolder = dt.enrichData(dataSetHolder);
      //  long end = System.currentTimeMillis();
      //System.out.println("Total Enrich time:"+SparrowUtil.printDriverValue(driverResult) + "-"+ (end-start));
      if (logger.isReqTraceEnabled()) {
        logger.log("After Enrich", SparrowLogger.REQ_TRACE);
      }

      // if the request was marked for rejection then it will ignored by
      // AbstractFinalizer class
      sparrowResultHolder.setIgnoreRequest(dt.isMarkedForRejection());
      sparrowResultHolder.setSoftRejection(dt.isSoftRejection());

      if (resultHolder == null) {
        // Exception should not be thrown here because if the request is the last request for this cycle
        // then the end cycle will be missed to notify to AbstractFinalizer.
        // AbstractFinalizer looks up ignore request flag accordingly skips the process.
        logger.error(" enrichData call returned NULL [" +
                     SparrowUtil.printDriverValue(driverResult) + "]");

        if (!sparrowResultHolder.isIgnoreRequest()) {
          markForRejection(dt, driverResult, new RequestProcessException("DataOutputHolder is null"));
          sparrowResultHolder.setIgnoreRequest(true);
          sparrowResultHolder.setSoftRejection(false);
        }

      }

      resultHolder.setTokenValue(dataSetHolder.getDataSetAsKeyValue());

    }
    catch (Exception ex) {
      context.setAttribute(ContextVariables.EXCEPTION_COUNT,new Integer(exceptionCount++));
      logger.error("Exception exception thrown while processing [" +
                   SparrowUtil.printDriverValue(driverResult) + "]", ex);
      if (!sparrowResultHolder.isIgnoreRequest()) {
        markForRejection(dt, driverResult, ex);
        sparrowResultHolder.setIgnoreRequest(true);
        sparrowResultHolder.setSoftRejection(false);
      }
    }
    finally {
      dataSetHolder.destroy();
      dataSetHolder = null;
    }

    resultHolder.setDriverRow(driverResult);

    sparrowResultHolder.setLcEvent(dt);
    sparrowResultHolder.setDataOutputHolder(resultHolder);
    sparrowResultHolder.setDriverRowEventListener(dt.getDriverRowEventListener());

    if (endCycle != null && endCycle == FIFO.END_CYCLE_MESSAGE) {
      sparrowResultHolder.setEndCycle(true);
    }

    if (dt.getDriverRowEventListener().preQueue()) {
      if (logger.isDebugEnabled()) {
        logger.log("Publishing processed data to response fifo :[" +
                   SparrowUtil.printDriverValue(driverResult) + "]",
                   SparrowLogger.DEBUG);
      }
      sparrowResultHolder.setStartTime(strtTime);
      publish(sparrowResultHolder);
    }
  }

  /**
   *
   * @param dt DataTransformer
   * @param rr ResultRow
   * @param reason String
   */
  private void markForRejection(DataTransformer dt, ResultRow rr, Throwable t) {
    try {
      dt.markForRejection("Exception raised by AbstractRequestProcessor",t);
    }
    catch (Exception ex) {
      logger.error("Exception occured while marking the request as rejected : " +
                   SparrowUtil.printDriverValue(rr), ex);
    }
  }

}
