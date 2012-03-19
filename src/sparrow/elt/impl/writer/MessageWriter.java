package sparrow.elt.impl.writer;

import sparrow.elt.core.config.SparrowDataWriterConfig;
import sparrow.elt.core.exception.DataWriterException;
import sparrow.elt.core.exception.EventNotifierException;
import sparrow.elt.core.log.SparrowLogger;
import sparrow.elt.core.log.SparrowrLoggerFactory;
import sparrow.elt.core.resource.Resource;
import sparrow.elt.core.util.ConfigKeyConstants;
import sparrow.elt.core.util.Constants;
import sparrow.elt.core.util.Sortable;
import sparrow.elt.core.util.SparrowUtil;
import sparrow.elt.core.vo.DataOutputHolder;
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
public class MessageWriter
    implements DataWriter {

  private GenericMessageWriter writer;

  private static final SparrowLogger logger = SparrowrLoggerFactory.
      getCurrentInstance(
      MessageWriter.class);

  /**
   *
   * @param config SparrowDataWriterConfig
   */
  public MessageWriter(SparrowDataWriterConfig config) {

    SparrowUtil.validateParam(new String[] {ConfigKeyConstants.PARAM_DEST_NAME,
                            ConfigKeyConstants.PARAM_KEY_NAME}
                            , "MessageWriter[" + config.getName() + "]",
                            config.getInitParameter());

    //     String destName = config.getInitParameter().getParameterValue(
    //         ConfigKeyConstants.PARAM_DEST_NAME);
    //     String[] res_dest = destName.split("[@]");
    //     Resource r = config.getContext().getResource(res_dest[0]);
    int transaction = config.getOnError().equals(Constants.IGNORE) ?
        Resource.NOT_IN_TRANSACTION : Resource.IN_TRANSACTION;

    if (Resource.NOT_IN_TRANSACTION != transaction) {
      transaction = (SparrowUtil.performTernary(config.
                                              getInitParameter(),
                                              ConfigKeyConstants.
                                              PARAM_EXEMPT_TRANS, false)) ?
          Resource.NOT_IN_TRANSACTION : transaction;
    }

    writer = (Resource.NOT_IN_TRANSACTION == transaction) ? new
        GenericMessageWriterNonTxn(config, transaction) :
        new GenericMessageWriter(config,
                                 transaction);

    /**   if (Constants.JMS_TYPE_QUEUE.equals(r.getParam().getParameterValue(
           ConfigKeyConstants.
           PARAM_JMS_TYPE))) {
         writer = (Resource.NOT_IN_TRANSACTION == transaction) ? new
             JMSQueueWriterNonTxn(config, transaction) :
             new JMSQueueWriter(config,
                                transaction);
       }
       else {
         writer = (Resource.NOT_IN_TRANSACTION == transaction) ? new
             JMSTopicWriterNonTxn(config, transaction) :
             new JMSTopicWriter(config,
                                transaction);

       }**/

  }

  /**
   * startRequest
   *
   * @return boolean
   */
  public boolean startRequest() {
    return writer.startRequest();
  }

  /**
   * endRequest
   *
   * @return boolean
   */
  public boolean endRequest() {
    return writer.endRequest();
  }

  /**
   * endCycle
   */
  public void endCycle() throws EventNotifierException {
    writer.endCycle();
  }

  /**
   * onError
   *
   * @param writerName String
   * @param ex Exception
   */
  public void onError(String writerName, Exception ex) {
    writer.onError(writerName, ex);
  }

  /**
   * destroy
   */
  public void destroy() {
    writer.destroy();
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
   * initialize
   */
  public void initialize() {
    writer.initialize();
  }

  /**
   * endOfProcess
   *
   * @param flag int
   */
  public void endOfProcess(int flag) {
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
   * writeData
   *
   * @param data DataOutputHolder
   * @param statusCode int
   * @return int
   */
  public int writeData(DataOutputHolder data, int statusCode) throws
      DataWriterException {
    return writer.writeData(data, statusCode);
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
