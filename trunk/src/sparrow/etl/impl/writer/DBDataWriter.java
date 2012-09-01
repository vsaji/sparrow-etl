package sparrow.etl.impl.writer;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import sparrow.etl.core.config.SparrowDataWriterConfig;
import sparrow.etl.core.exception.DataWriterException;
import sparrow.etl.core.exception.EventNotifierException;
import sparrow.etl.core.util.ConfigKeyConstants;
import sparrow.etl.core.util.Sortable;
import sparrow.etl.core.util.SparrowUtil;
import sparrow.etl.core.vo.DataOutputHolder;
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
public class DBDataWriter
    implements DataWriter {

  final DataWriter writer;

  private static final HashMap writerClassMap = new HashMap() {
    {
      Map impls = SparrowUtil.getImplConfig("writer_extn");
      for (Iterator it = impls.keySet().iterator(); it.hasNext(); ) {
        String key = (String) it.next();
        put(key, impls.get(key));
      }
    }
  };

  /**
   *
   * @param config SparrowDataWriterConfig
   */
  public DBDataWriter(SparrowDataWriterConfig config) {

    writer = (DataWriter) SparrowUtil.createObject(writerClassMap.get(
        getWriterClassKey(config)).toString(),
                                                 SparrowDataWriterConfig.class,
                                                 config);

    /*
     new DBWriterSupportSelfCommit(config);
             (Constants.WRITER_OE_FAIL_ALL.equals(config.getOnError())) ?
             (DataWriter)new DBWriterSupportAllorNone(config) :
             new DBWriterSupportSelfCommit(config);
     **/
  }

  /**
   *
   * @param config SparrowDataWriterConfig
   * @return String
   */
  private String getWriterClassKey(SparrowDataWriterConfig config) {

    int batchSize = SparrowUtil.performTernary(config.getInitParameter(),
                                             ConfigKeyConstants.
                                             PARAM_BATCH_SIZE, 0);
    boolean isValid = (batchSize > 1);
    boolean isBpr = SparrowUtil.performTernary(config.getInitParameter(),
                                             ConfigKeyConstants.
                                             PARAM_BATCH_PER_REQ, false);

    String key = config.getOnError() + "@batch/" + isValid + "/bpr/" + isBpr;

    if (key.startsWith("failall@batch/false")) {
      if (SparrowUtil.performTernary(config.
                                   getInitParameter(),
                                   ConfigKeyConstants.
                                   PARAM_EXEMPT_TRANS, false)) {
        key = "ignore@batch/false/bpr/" + isBpr;
      }
    }
    return key;
  }

  /**
   * destroy
   */
  public void destroy() {
    writer.destroy();
  }

  /**
   * requestPostCommit
   *
   * @return boolean
   */
  public boolean endRequest() throws DataWriterException {
    return writer.endRequest();
  }

  /**
   * requestPreCommit
   *
   * @return boolean
   */
  public boolean startRequest() throws DataWriterException {
    return writer.startRequest();
  }

  /**
   * initialize
   */
  public void initialize() {
    writer.initialize();
  }

  /**
   *
   * @param data DataOutputHolder
   * @param statusCode int
   * @throws DataWriterException
   * @return int
   */
  public int writeData(DataOutputHolder data, int statusCode) throws
      DataWriterException {
    return writer.writeData(data, statusCode);
  }

  /**
   * onError
   *
   * @param writerName String
   * @param ex Exception
   */
  public void onError(String writerName, Exception ex) throws
      DataWriterException {
    writer.onError(writerName, ex);
  }

  /**
   * getWriterConfig
   *
   * @return SparrowDataWriterConfig
   */
  public SparrowDataWriterConfig getWriterConfig() {
    return writer.getWriterConfig();
  }

  /**
   * endCycle
   */
  public void endCycle() throws EventNotifierException {
    writer.endCycle();
  }

  /**
   * endOfProcess
   *
   * @param flag int
   */
  public void endOfProcess(int flag) throws EventNotifierException {
    writer.endOfProcess(flag);
  }

  /**
   * beginCycle
   */
  public void beginCycle() throws EventNotifierException {
    writer.beginCycle();
  }

  /**
   * isInTransaction
   *
   * @return boolean
   */
  public boolean isInTransaction() {
    return writer.isInTransaction();
  }

  /**
   * setInTransaction
   *
   * @param trans boolean
   */
  public void setInTransaction(boolean trans) {
    writer.setInTransaction(trans);
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
   * beginApplication
   */
  public void beginApplication() throws EventNotifierException {
        writer.beginApplication();
  }

  /**
   * endApplication
   */
  public void endApplication() throws EventNotifierException {
        writer.endApplication();
  }

}
