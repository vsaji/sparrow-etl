package sparrow.elt.impl.writer;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import sparrow.elt.core.config.SparrowDataWriterConfig;
import sparrow.elt.core.dao.impl.QueryObject;
import sparrow.elt.core.dao.provider.impl.DBDataProvider;
import sparrow.elt.core.dao.provider.impl.DBDataProviderElement;
import sparrow.elt.core.dao.provider.impl.DBDataProviderImpl;
import sparrow.elt.core.dao.util.QueryExecutionStrategy;
import sparrow.elt.core.exception.DataException;
import sparrow.elt.core.exception.DataWriterException;
import sparrow.elt.core.exception.ResourceException;
import sparrow.elt.core.log.SparrowLogger;
import sparrow.elt.core.log.SparrowrLoggerFactory;
import sparrow.elt.core.util.ConfigKeyConstants;
import sparrow.elt.core.util.Constants;
import sparrow.elt.core.util.SparrowUtil;
import sparrow.elt.core.vo.DBParamHolder;
import sparrow.elt.core.vo.DataOutputHolder;
import sparrow.elt.core.writer.AbstractDataWriter;


/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */
public class GenericDBDataWriter
    extends AbstractDataWriter {

  protected DBDataProviderElement dbdpe = null;
  protected DBDataProvider dbdp = null;
  protected QueryExecutionStrategy strategy;

  private final boolean zeroCountFail;

  protected static final SparrowLogger logger = SparrowrLoggerFactory.
      getCurrentInstance(
      DBDataWriter.class);

  /**
   *
   * @param config SparrowDataWriterConfig
   */
  public GenericDBDataWriter(SparrowDataWriterConfig config) {
    super(config);
    zeroCountFail = SparrowUtil.performTernary(config.getInitParameter(),
                                             "zero.count.fail", false);
  }

  /**
   *
   * @return DBDataProviderElement
   */
  protected final DBDataProviderElement getDataProvider() {
    DBDataProviderElement dbdpe = (DBDataProviderElement) config.getContext().
        getDataProviderElement(config.getInitParameter().getParameterValue(
        ConfigKeyConstants.PARAM_DATA_PROVIDER));
    return dbdpe;
  }

  /**
   *
   */
  public void initialize() {
    if (config.getInitParameter().isParameterExist(ConfigKeyConstants.
        PARAM_DATA_PROVIDER)) {
      dbdpe = getDataProvider();
      dbdp = dbdpe.getDBDataProvider();
    }
    else {
      dbdp = new DBDataProviderImpl(config);
    }

    strategy = QueryExecutionStrategy.getStrategy(WRITER_NAME, dbdp.
                                                  getQuery().getSQL());

    super.initialize();
  }

  /**
   *
   * @param conName String
   * @throws SQLException
   * @return boolean
   */
  protected void checkDBSupportBatch(String conName) throws SQLException,
      ResourceException {
    Connection con = getConnection(conName);
    boolean rslt = con.getMetaData().supportsBatchUpdates();
    con.close();
    /** check whether DB supports batch updates**/
    if (!rslt) {
      throw new ResourceException("DB_BATCH_NOT_SUPPORT",
                                  "DB:" + dbdp.getQuery().getConnectionName() +
                                  " does not support batch updates");
    }
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

    //   long start = System.currentTimeMillis();
    prepareQueryObject(data);
    //   System.out.println("[" + WRITER_NAME + "-PQ]:" +
    //                      (System.currentTimeMillis() - start));
    try {
      int updateCount = dbdp.executeQuery();
      //     System.out.println("[" + WRITER_NAME + "-EQ]:" +
      //                       (System.currentTimeMillis() - start));

      if(zeroCountFail && updateCount==0){
        return STATUS_FAIL;
      }

    }
    catch (Exception ex) {
      throw new DataWriterException(
          "Exception occured while executing query [" + WRITER_NAME + "]["+data.getDriverRow()+"]", ex);
    }
    return STATUS_SUCCESS;
  }

  /**
   *
   * @param data DataOutputHolder
   * @return QueryObject
   */
  protected QueryObject prepareQueryObject(DataOutputHolder data) {
    DBParamHolder dbParam = data.getDBParamHolder(KEY_NAME);
    QueryObject qo = dbdp.getQuery();

    if (dbParam != null) {
      //Query param set up users
      qo.getQueryParamAsArray().addAll(dbParam.getParamList());
      //User defined tokens
      qo.getQueryParamAsMap().putAll(dbParam.getParamMap());
      // All the token and their values generated by Spear
      qo.getQueryParamAsMap().putAll(data.getTokenValue());
    }
    else {
      qo.getQueryParamAsMap().putAll(data.getTokenValue());
    }
    return qo;
  }

  /**
   *
   * @param data DataOutputHolder
   * @return List
   */
  protected List prepareBatchParam(DataOutputHolder data) {
    List l = data.getDBParamHolder(KEY_NAME).getParamList();
    List returnList = new ArrayList();

//     QueryObject qo = dbdp.getQuery().getCopy();
//     qo.getQueryParamAsMap().putAll(data.getTokenValue());

    for (Iterator it = l.iterator(); it.hasNext(); ) {
      QueryObject qo = new QueryObject();
      qo.getQueryParamAsMap().putAll(data.getTokenValue());
      qo.getQueryParamAsArray().addAll( (List) it.next());
      strategy.implementStrategy(qo);
      returnList.add(qo.getQueryParamAsArray());
    }
    return returnList;
  }

  /**
   *
   */
  public void destroy() {
    dbdp.destory();
    if (dbdpe != null) {
      dbdpe.destroy();
    }
    dbdp = null;
    dbdpe = null;
  }

  /**
   * onError
   *
   * @param writerName String
   * @param ex Exception
   */
  public void onError(String writerName, Exception ex) {
  }

  /**
   *
   * @param connectionName String
   * @throws SQLException
   * @return Connection
   */
  protected final Connection getConnection(String connectionName) throws
      SQLException {
    return getContext().getDBConnection(connectionName);
  }

  /**
   * endCycle
   */
  public void endCycle() {
    if (config.getTriggerEvent().equals(Constants.END_CYCLE)) {
      executeQuery();
    }
  }

  /**
   *
   */
  public void beginApplication() {
    if (config.getTriggerEvent().equals(Constants.BEGIN_APP)) {
      executeQuery();
    }
  }

  /**
   * beginCycle
   */
  public void beginCycle() {
    if (config.getTriggerEvent().equals(Constants.BEGIN_CYCLE)) {
      executeQuery();
    }
  }

  /**
   *
   * @param flag int
   */
  public void endOfProcess(int flag) {
    if ( (config.getTriggerEvent().equals(Constants.END_APP) &&
          Constants.EP_END_APP == flag) ||
        (config.getTriggerEvent().equals(Constants.END_PROCESS) &&
         Constants.EP_NO_RECORD == flag)) {
      executeQuery();
    }
  }

  /**
   *
   */
  private void executeQuery() {
    QueryObject qo = dbdp.getQuery();
    qo.getQueryParamAsMap().putAll(super.getContext().getAttributes());
    try {
      dbdp.executeQuery();
    }
    catch (DataException ex) {
      ex.printStackTrace();
      logger.error(
          "Exception occured while executing query in endOfProcess[end.app] trigger",
          ex);
    }
  }

}
