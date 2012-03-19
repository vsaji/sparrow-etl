package sparrow.elt.impl.writer;

import java.util.ArrayList;

import sparrow.elt.core.config.SparrowDataWriterConfig;
import sparrow.elt.core.dao.impl.QueryObject;
import sparrow.elt.core.dao.util.QueryExecutionStrategy;
import sparrow.elt.core.exception.DataException;
import sparrow.elt.core.exception.DataWriterException;
import sparrow.elt.core.exception.InitializationException;
import sparrow.elt.core.util.ConfigKeyConstants;
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
public class DBDataWriterSupportBatch
    extends GenericDBDataWriter {

  protected BatchHandler handler = null;
  protected int batchSize = 0;
  protected int recCounter = 0;

  /**
   *
   * @param config SparrowDataWriterConfig
   */
  public DBDataWriterSupportBatch(SparrowDataWriterConfig config) {
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

      QueryExecutionStrategy strategy = QueryExecutionStrategy.getStrategy(WRITER_NAME,dbdp.
          getQuery().getSQL());
      handler = (strategy.getStrategy()==QueryExecutionStrategy.STRATEGY_NONE) ? (BatchHandler)
          new StatementBatchHandler(strategy.getSQL()) : new PreparedStatementBatchHandler(strategy);
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


    QueryObject qo = prepareQueryObject(data);
    try {

      handler.add(qo);

      qo.getQueryParamAsMap().clear();
      qo.getQueryParamAsArray().clear();

      recCounter++;
      qo = null;
      if (recCounter == batchSize) {
        executeBatch();
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
    if (logger.isInfoEnabled()) {
      logger.info("[" + rslt.length + "] STATEMENT(S) EXECUTED");
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

  /**
   *
   * <p>Title: </p>
   * <p>Description: </p>
   * <p>Copyright: Copyright (c) 2004</p>
   * <p>Company: </p>
   * @author Saji Venugopalan
   * @version 1.0
   */
  private interface BatchHandler {
    abstract void add(QueryObject query);

    abstract int[] executeBatch() throws DataException;

    abstract void reset();
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
  private class StatementBatchHandler
      implements BatchHandler {

    private ArrayList sqlCollections = null;
    private final String sql;

    /**
     *
     */
    public StatementBatchHandler(String sql) {
      this.sql = sql;
      sqlCollections = new ArrayList();
    }

    /**
     * add
     *
     * @param query QueryObject
     */
    public void add(QueryObject query) {
      sqlCollections.add(sql);
    }

    /**
     *
     */
    public int[] executeBatch() throws DataException {
// /     System.out.println(hashCode()+"-->"+sqlCollections.size());
      return dbdp.executeSQLBatch(sqlCollections);
    }

    /**
     * reset
     */
    public void reset() {
      sqlCollections.clear();
    }

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
  private class PreparedStatementBatchHandler
      implements BatchHandler {

    private final String sql;
    private ArrayList sqlParamCollections = null;
    private final QueryExecutionStrategy strategy;

    /**
     *
     * @param sql String
     */
    PreparedStatementBatchHandler(QueryExecutionStrategy strategy) {
      this.sql = strategy.getSQL();
      this.strategy = strategy;
      sqlParamCollections = new ArrayList();
    }

    /**
     *
     * @param query QueryObject
     */
    public void add(QueryObject query) {
      // System.out.println("["+WRITER_NAME+"]["+hashCode()+"][add]");
      strategy.implementStrategy(query);
      sqlParamCollections.add(new ArrayList(query.getQueryParamAsArray()));
    }

    /**
     *
     */
    public int[] executeBatch() throws DataException {
      //  System.out.println("["+WRITER_NAME+"]["+hashCode()+"][executeBatch]");
      return dbdp.executeBatch(sql, sqlParamCollections);
    }

    /**
     * reset
     */
    public void reset() {
      //   System.out.println("["+WRITER_NAME+"]["+hashCode()+"][reset]");
      sqlParamCollections.clear();
    }

  }

}
