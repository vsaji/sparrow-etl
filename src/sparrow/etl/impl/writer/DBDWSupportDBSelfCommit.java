package sparrow.etl.impl.writer;

import java.util.List;

import sparrow.etl.core.config.SparrowDataWriterConfig;
import sparrow.etl.core.exception.DataWriterException;
import sparrow.etl.core.exception.InitializationException;
import sparrow.etl.core.vo.DataOutputHolder;


/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public class DBDWSupportDBSelfCommit
    extends GenericDBDataWriter {

  protected DoubleBatchHandler handler = null;

  /**
   *
   * @param config SparrowDataWriterConfig
   * @param dbdw DBDataWriter
   */
  public DBDWSupportDBSelfCommit(SparrowDataWriterConfig config) {
    super(config);
  }

  /**
   *
   */
  public void initialize() {

    try {
      super.initialize();
      checkDBSupportBatch(dbdp.getQuery().getConnectionName());
      handler = new DoubleBatchHandler(dbdp);
    }
    catch (Exception ex) {
      throw new InitializationException(
          "Exception occured while initializing DBDataWriter", ex);
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


    //DBParamHolder dbparam = data.getDBParamHolder(KEY_NAME);
    try {
      List l = prepareBatchParam(data);
      handler.add(l);
      int rslt[] = handler.executeBatch();
      handler.reset();

      if (logger.isDebugEnabled()) {
        logger.debug("[" + rslt.length + "] STATEMENT(S) EXECUTED");
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
   * @return boolean
   */
  public boolean isInTransaction() {
    return false;
  }

}
