package sparrow.etl.impl.writer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import sparrow.etl.core.config.SparrowDataWriterConfig;
import sparrow.etl.core.dao.impl.QueryObject;
import sparrow.etl.core.dao.util.QueryExecutionStrategy;
import sparrow.etl.core.exception.DataException;
import sparrow.etl.core.exception.DataWriterException;
import sparrow.etl.core.exception.InitializationException;
import sparrow.etl.core.util.ConfigKeyConstants;
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
public class DBDWDoubleBatchSupport
    extends GenericDBDataWriter {

  protected DoubleBatchHandler handler = null;
  protected final int batchSize;
  protected int recCounter = 0;

  /**
   *
   * @param config SparrowDataWriterConfig
   */
  public DBDWDoubleBatchSupport(SparrowDataWriterConfig config) {
    super(config);
    batchSize = Integer.parseInt(config.getInitParameter().getParameterValue(
        ConfigKeyConstants.
        PARAM_BATCH_SIZE));
  }

  /**
   *
   */
  public void initialize() {

    try {

      super.initialize();
      checkDBSupportBatch(dbdp.getQuery().getConnectionName());
       strategy = QueryExecutionStrategy.getStrategy(WRITER_NAME,dbdp.
          getQuery().getSQL());
      handler = new DoubleBatchHandler(dbdp);
    }
    catch (Exception ex) {
      throw new InitializationException(
          "Exception occured while initializing DBDataWriter", ex);
    }
  }

  /**
   *
   * @return boolean
   */
  public boolean isInTransaction() {
    return false;
  }

  /**
   * writeData
   *
   * @param data DataOutputHolder
   */
  public int writeData(DataOutputHolder data, int statusCode) throws
      DataWriterException {

    if (KEY_NAME != null && data.getDBParamHolder(KEY_NAME) == null) {
      logger.debug("Skipping writer [" + WRITER_NAME +
                   "] - DBParamHolder is null for key.name [" + KEY_NAME + "]");
      return STATUS_SUCCESS;
    }

   //   DBParamHolder dbparam = data.getDBParamHolder(KEY_NAME);
    try {
      List l = prepareBatchParam(data);
      handler.add(l);
      recCounter++;//= dbparam.getParamList().size();
      if (recCounter >= batchSize) {
        //executeBatch();
      }
    }
    catch (Exception ex) {
      throw new DataWriterException(
          "Exception occured while executing query [" + WRITER_NAME + "]", ex);
    }
    return 0;
  }


  /**
   *
   * @throws DataException
   */
  private void executeBatch() throws DataException {
    int[] rslt = handler.executeBatch();
    handler.reset();
    recCounter = 0;
    if (logger.isDebugEnabled()) {
      logger.debug("[" + rslt.length + "] STATEMENT(S) EXECUTED");
    }
  }

  /**
   *
   */
  public void endCycle() {
    if (recCounter != 0) {
      logger.debug("Batch executing for remaining records : " + recCounter);
      try {
        executeBatch();
      }
      catch (DataException ex) {
        logger.error("Batch execution could not be completed. [" + recCounter +
                     "] records affected", ex);
      }
    }
  }
}
