package sparrow.etl.impl.extractor.jms;

import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.naming.NamingException;

import sparrow.etl.core.config.ConfigParam;
import sparrow.etl.core.config.SparrowDataExtractorConfig;
import sparrow.etl.core.context.SparrowContext;
import sparrow.etl.core.dao.impl.ColumnHeader;
import sparrow.etl.core.dao.impl.PostProcessAcknowledgement;
import sparrow.etl.core.dao.impl.RecordSetImpl_Disconnected;
import sparrow.etl.core.dao.impl.ResultRow;
import sparrow.etl.core.dao.impl.ResultRowImpl;
import sparrow.etl.core.dao.metadata.ColumnAttributes;
import sparrow.etl.core.dao.metadata.DataTypeResolver;
import sparrow.etl.core.dao.metadata.SparrowResultMetaDataFactory;
import sparrow.etl.core.dao.metadata.SparrowResultSetMetaData;
import sparrow.etl.core.exception.DataException;
import sparrow.etl.core.exception.InitializationException;
import sparrow.etl.core.exception.ParserException;
import sparrow.etl.core.exception.ResourceException;
import sparrow.etl.core.exception.SparrowRuntimeException;
import sparrow.etl.core.exception.TypeCastException;
import sparrow.etl.core.extractor.DataExtractor;
import sparrow.etl.core.log.SparrowLogger;
import sparrow.etl.core.log.SparrowrLoggerFactory;
import sparrow.etl.core.resource.Resource;
import sparrow.etl.core.resource.JMSResourceInitializer.JMSResource;
import sparrow.etl.core.util.AsyncRequestProcessor;
import sparrow.etl.core.util.ConfigKeyConstants;
import sparrow.etl.core.util.RequestListener;
import sparrow.etl.core.util.SparrowUtil;
import sparrow.etl.core.vo.DataHolder;



/**
 *
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * <p>
 * Company:
 * </p>
 *
 * @author Saji Venugopalan
 * @version 1.0
 */
public class JMSDataExtractor
    implements DataExtractor, RequestListener {

  private static SparrowLogger logger = SparrowrLoggerFactory
      .getCurrentInstance(JMSDataExtractor.class);
  private static final String DEFAULT_COL_DEF =
      "com/cs/sg/sparrow/impl/extractor/jms/jms-coldef.xml";
  private static final String ON_ERROR_DEFAULT_VALUE = "direct.to.dq";
  private static final String ON_ERROR_PRSRV_IN_STORE = "preserve.in.store";

  private final SparrowContext context;
  private final ConfigParam param;
  private final String name;
  private final String errorLogDir;
  private final long pauseTime;
  private final int retryLimit;
  private final boolean printMsg;
  private final String onFailure;
  private final int fetchSize;
  
//  private AsyncRequestProcessor arpMessageRemoval = null;
  private AsyncRequestProcessor asyncMsgProcessor = null;
  private SparrowMessageListernerPoolManager poolManager = null;
  private Consumer[] consumers = null;
  private Publisher deadLetterPub = null;
  private ColumnHeader ch = null;
  private MessageStore msgStore = null;
  private DataTypeResolver[] dtr = null;
  private ThreadGroup tg = null;
  private PrintWriter errLogWriter = null;
  private List messages = null;

  private boolean storeConfigured = false;
  private boolean isDataLoaderInProgress = false;
  private int consumersCount = 1;
  private volatile int msgCount = 0;  
  private int reqColLen, colLen;
  private String[] headerProps = null;
  
  
  
  /**
   *
   * @param config
   *            DataExtractorConfig
   * @param context
   *            SparrowContext
   */
  public JMSDataExtractor(SparrowDataExtractorConfig config) {

    this.param = config.getInitParameter();
    SparrowUtil.validateParam(new String[] {
                            ConfigKeyConstants.PARAM_DEST_NAME,
                            ConfigKeyConstants.PARAM_ERR_LOG_DIR}
                            , "JMSDataExtractor",
                            param);
    this.context = config.getContext();
    this.name = config.getName();
    this.pauseTime = SparrowUtil.performTernaryForLong(config.getInitParameter(),
        ConfigKeyConstants.PARAM_PAUSE_TIME, 500);
    this.retryLimit = SparrowUtil.performTernary(config.getInitParameter(),
                                               ConfigKeyConstants.
                                               PARAM_RETRY_LIMIT, 0);
    this.poolManager = new SparrowMessageListernerPoolManager(config);
    this.errorLogDir = param.getParameterValue(ConfigKeyConstants.
                                               PARAM_ERR_LOG_DIR);
    this.fetchSize = SparrowUtil.performTernary(config.getInitParameter(),
            ConfigKeyConstants.PARAM_FETCH_SIZE, 0); 
    String hderProps = config.getInitParameter().getParameterValue(
        ConfigKeyConstants.PARAM_HEADER_PROPS);

    if (hderProps != null && !hderProps.trim().equals("")) {
      headerProps = hderProps.split("[,]");
    }
    this.messages = new ArrayList(); //Vector();
    this.printMsg = SparrowUtil.performTernary(config.getInitParameter(),
                                             "print.message", false);
    this.onFailure = SparrowUtil.performTernary(config.getInitParameter(),
                                              ConfigKeyConstants.
                                              PARAM_ON_FAILURE,
                                              ON_ERROR_DEFAULT_VALUE);
    if (printMsg) {
      logger.info(
          "PARAM [print.message] is set to true. It will slowdown this process and cause your log overflow. ");
    }
    if (!ON_ERROR_DEFAULT_VALUE.equals(onFailure) &&
        !ON_ERROR_PRSRV_IN_STORE.equals(onFailure)) {
      throw new InitializationException("Unknown value [" + onFailure +
                                        "] found for PARAM[on.failure]. Supported values are [" +
                                        ON_ERROR_DEFAULT_VALUE + " (or) " +
                                        ON_ERROR_PRSRV_IN_STORE + "] ");
    }

  }

  /**
   * destroy
   */
  public void destroy() {
    try {
      // consumer.stop();
      // consumer.close();
      shutdownConsumers();

      if (storeConfigured && msgStore.size() > 0) {
        logger
            .warn("THERE ARE ["
                  + msgStore.size()
                  + "] UNPRODESSED MESSAGE(S) IN THE FILE STORE. MESSAGE(S) WILL BE PROCESSED IN THE NEXT RUN.");
      }

      errLogWriter.close();

//      if (arpMessageRemoval != null) {
//        arpMessageRemoval.close();
//      }

      if (asyncMsgProcessor != null) {
        asyncMsgProcessor.close();
      }

      if (deadLetterPub != null) {
        deadLetterPub.close();
      }
      poolManager.destroy();
    }
    catch (Exception ex) {
      logger.error("Exception occured while closing services", ex);
    }

  }

  /**
   * initialize
   */
  public void initialize() {
    try {

      asyncMsgProcessor = AsyncRequestProcessor.createAsynchProcessor(
          "async_msg_processor", 20000);
      asyncMsgProcessor.registerListener(this);
      asyncMsgProcessor.start();

      String errfileName = errorLogDir + "/" + context.getProcessId() +
          "_unprocessed_msg_" +
          SparrowUtil.formatDate(new Date(), "MMddyyyy") + ".log";
      errLogWriter = new PrintWriter(new FileOutputStream(errfileName, true));

      ch = resolveColumnHeader();

      poolManager.setColDefAttributes(getColDefAttributes());

      /** INIT DEAD LETTER QUEUE * */
      initDeadLetterQueue();
      /** * INIT Message Persistor ** */
      initStore();

      /*********************************************************/
      if (ON_ERROR_DEFAULT_VALUE.equals(onFailure) && deadLetterPub == null) {
        logger.warn("PARAM [on.failure] set to DEFAULT value [" +
                    ON_ERROR_DEFAULT_VALUE + "], however, failure messages will not be directed to any dead letter queue as the PARAM[" +
                    ConfigKeyConstants.PARAM_DEADLTTR_Q +
                    "] is not configured. Failure message will be logged to [" +
                    errfileName + "] file.");
      }
      else if (ON_ERROR_PRSRV_IN_STORE.equals(onFailure) && !storeConfigured) {
        throw new InitializationException("Failure messages cannot be preserved in the store as the message store is not configured");
      }
      else if (ON_ERROR_DEFAULT_VALUE.equals(onFailure) && deadLetterPub != null) {
        logger.info("PARAM [on.failure] set to [" + ON_ERROR_DEFAULT_VALUE +
                    "], Failure messages will be directed to [" +
                    param.getParameterValue(ConfigKeyConstants.PARAM_DEADLTTR_Q) +
                    "]");
      }

      /*********************************************************/
      if (storeConfigured && msgStore.size() > 0) {
        logger.info("[" + msgStore.size()
                    + "] MESSAGE(S) HAS BEEN LOADED FROM THE MESSAGE STORE.");
        loadMessageFromStore();
      }
      /*********************************************************/

      consumersCount = SparrowUtil.performTernary(param,
                                                ConfigKeyConstants.
                                                PARAM_CONSUMER_CNT,
                                                1);
      initAndStartConsumers();

    }
    catch (Exception e) {
      throw new InitializationException(
          "Exception occured while initializing JMSDataExtractor", e);
    }
  }

  /**
   *
   * @param s
   *            Session
   * @param d
   *            Destination
   * @param consumerCount
   *            int
   */
  private void initAndStartConsumers() throws ResourceException,
      NamingException {

    String destinationName = param
        .getParameterValue(ConfigKeyConstants.PARAM_DEST_NAME);
    String[] resource_destination = destinationName.split("[@]");
    String resource = resource_destination[0];
    String destination = resource_destination[1];

    consumers = new Consumer[consumersCount];

    logger.info("Initializing [" + consumersCount +
                "] cosumer thread(s) to process messages from [" + resource +
                "=>" + destination + "] Queue");

    tg = new ThreadGroup("consumers");
    tg.setDaemon(true);

    for (int i = 0; i < consumersCount; i++) {
      JMSResource r = (JMSResource) context.getResource(resource);
      Session s = r.getSession(Resource.NOT_IN_TRANSACTION);
      Destination d = r.getDestination(destination);

      consumers[i] = new Consumer(s, d);
      Thread consumerThread = new Thread(tg, consumers[i], name
                                         + "_msg_consumer[" + i + "]");
      consumerThread.start();
    }
  }

  /**
   *
   */
  private void shutdownConsumers() throws Exception {

    for (int i = 0; i < consumersCount; i++) {
      consumers[i].stop(); // sets run=false;
    }

    while (tg.activeCount() > 0) {
      Thread.sleep(2000);
    }

    for (int i = 0; i < consumersCount; i++) {
      consumers[0].close(); // closes session
    }
  }

  /**
   *
   * @return ColumnHeader
   */
  private ColumnHeader resolveColumnHeader() {
    String colDefFile = SparrowUtil.performTernary(param,
                                                 ConfigKeyConstants.
                                                 PARAM_COLUMN_DEF,
                                                 DEFAULT_COL_DEF);
    SparrowResultSetMetaData srsmd = SparrowResultMetaDataFactory
        .getSparrowResultSetMetaData(colDefFile, name);
    dtr = srsmd.getAllDataTypeResolvers();
    colLen = dtr.length;
    reqColLen = srsmd.getDataTypeResolvers().length;
    return new ColumnHeader(srsmd);
  }

  /**
   *
   * @return ColumnAttributes[]
   */
  private ColumnAttributes[] getColDefAttributes() {
    ColumnAttributes[] colAttribs = new ColumnAttributes[reqColLen];
    for (int i = 0; i < reqColLen; i++) {
      colAttribs[i] = dtr[i].getColumnAttributes();
    }
    return colAttribs;
  }

  /**
   *
   */
  private void waitForRequestCompletion() {
//    while (true) {
//			if (arp.isRequestQueueEmpty()) {
//				break;
//			}
    try {
      Thread.sleep(pauseTime);
    }
    catch (InterruptedException ex) {
      logger.error(ex.getLocalizedMessage(), ex);
    }
    //   }
  }

  /**
   *
   */
  private void initStore() {

    String persistorType = param
        .getParameterValue(ConfigKeyConstants.PARAM_STORE_TYPE);

    if (persistorType != null) {
      msgStore = MessageStoreFactory
          .createMessageStore(
          persistorType,
          param
          .getParameterValue(ConfigKeyConstants.PARAM_STORE_SRC), param
          .getParameterValue(ConfigKeyConstants.PARAM_PRSRV_STORE));

//      arpMessageRemoval = AsyncRequestProcessor.createAsynchProcessor(name +
//          "_storeFileListener", 500);
//      arpMessageRemoval.registerListener(new MessageRemovalRequestListener());
//      arpMessageRemoval.start();

      storeConfigured = true;
    }
  }

  /**
   *
   * @param r
   *            JMSResource
   * @param s
   *            Session
   * @throws ResourceException
   * @throws NamingException
   */
  private void initDeadLetterQueue() throws ResourceException,
      NamingException {

    String deadLetterQueueName = param
        .getParameterValue(ConfigKeyConstants.PARAM_DEADLTTR_Q);

    if (deadLetterQueueName != null) {

      String[] resource_queue = deadLetterQueueName.split("[@]");

      JMSResource r_dead = (JMSResource) context
          .getResource(resource_queue[0]);
      Session s_dead = r_dead.getSession(Resource.NOT_IN_TRANSACTION);
      Destination d_dead = r_dead.getDestination(resource_queue[1]);
      deadLetterPub = new Publisher(s_dead, d_dead, headerProps);
    }
  }

  /**
   *
   */
  private void loadMessageFromStore() {
    for (MessageIterator it = msgStore.iterator(); it.hasNext(); ) {
      process(it.next());
    }
  }

  /**
   * loadData
   *
   * @return DataHolder
   */
  public DataHolder loadData() {

	    // Stop consuming message
	    isDataLoaderInProgress = true;
	    //suspendConsumers();
	    waitForRequestCompletion();
	    DataHolder holder = new DataHolder();
	    RecordSetImpl_Disconnected rsTemp = new RecordSetImpl_Disconnected();
	//    synchronized(messages){
	    rsTemp.setResult(new ArrayList(messages));
	    //System.out.println(rsTemp.getResult().size() + "<==>" + messages.size());
	    messages.clear();
	    msgCount=0;
	//    }
	    isDataLoaderInProgress = false;
	    holder.setData(rsTemp);
	    //resumeConsumers();
	    return holder;
  }

  /**
   *
   */
//  private void suspendConsumers() {
//    for (int i = 0; i < consumersCount; i++) {
//      consumers[i].suspend(); // sets run=false;
//    }
//  }

  /**
   *
   */
//  private void resumeConsumers() {
//    for (int i = 0; i < consumersCount; i++) {
//      consumers[i].resume(); // sets run=true;
//    }
//
//  }

  /**
   * endProcess
   */
  public void endProcess() {
  }

  /**
   * process
   *
   * @param o
   *            Object
   */
  public void process(Object o) {

    SparrowJMSMessage message = (SparrowJMSMessage) o;
    SparrowMessageListener listener = null;
    try {

      JMSPostProcessAcknowledgement postProcessCmd = null;

      if (storeConfigured) {
        postProcessCmd = new JMSPostProcessAcknowledgement(message);
      }
      //addtional pause.
      while (isDataLoaderInProgress) {
        //empty loop - waiting for data loading to be completed.
        //System.out.println("Waiting for completion of DataLoader operation[" +
        //                           messages.size() + "]");
      }

      listener = poolManager.getMessageListener();
      String[] strRow = listener.onMessage(message);

      Object[] row = getDataTypeResolvedRow(strRow);
      ResultRow rr = new ResultRowImpl(ch, row, postProcessCmd);
      messages.add(rr);
      //rs.addRow(rr);
      //logger.debug("ADDED:==>" + message.getFileName());

    }
    catch (DataException e) {
      logger.error("DataException occured while processing message ["
                   + ( (o != null) ? o.toString() : null) + "]", e);
      handleErrors(message, e.getErrorCode() + "-"
                   + e.getErrorDescription());
    }
    catch (TypeCastException e) {
      logger.error("TypeCastException occured while processing message ["
                   + ( (o != null) ? o.toString() : null) + "]", e);
      handleErrors(message, e.getErrorDescription());
    }
    catch (ParserException e) {
      logger.error("ParserException occured while processing message ["
                   + ( (o != null) ? o.toString() : null) + "]", e);
      handleErrors(message, e.getErrorDescription());
    }
    catch (Exception e) {
      logger.error("Exception occured while processing message ["
                   + ( (o != null) ? o.toString() : null) + "]", e);
      handleErrors(message, e.getMessage());
    }
    finally {
      if (listener != null) {
        if (logger.isDebugEnabled()) {
          logger.debug("Returning MessageListener[" + listener.hashCode() +
                       "] to pool");
        }
        listener.returnObject();
      }
    }
  }

  /**
   *
   * @param values
   *            String[]
   * @throws TypeCastException
   * @return Object[]
   */
  private Object[] getDataTypeResolvedRow(String[] values) throws
      TypeCastException {
    Object[] o = new Object[reqColLen];
    int i = 0;

    for (int j = i; i < colLen; i++) {
      if (!dtr[i].isExcludeColumn()) {
        o[j++] = dtr[i].getTypeCastedValue(values[i]);
      }
    }
    return o;
  }

  /**
   *
   * @param msg SparrowJMSMessage
   */
  private void removeMsg(SparrowJMSMessage msg) {
    try {
      msgStore.remove(msg.getInternalMessageId());
    }
    catch (Exception ex) {
      logger.error("Exception occured while removing message [" +
                   msg + "] from the store");
      ex.getStackTrace();
    }

  }

  /**
   *
   * @param message
   *            Message
   * @param reason
   *            String
   */
  private void handleErrors(SparrowJMSMessage message, String reason) {

    //Step 1 : Check retry enabled
    if (retryLimit > 0 && message.incrementRetryCount() < (retryLimit-1)) {
      logger.warn("Retrying [" + message.getMessageId() + "]:[" +
                  message.getRetryCount() + "]");
      message.setLoadedFromStore(true); //just to escape from the message being stored twice.
      asyncMsgProcessor.process(message);
      return;
    }

    writeToErrorLog(message.getMessage(), reason);

    if (ON_ERROR_DEFAULT_VALUE.equals(onFailure)) {
      //Step 2 : (Only if the step 1 is skipped) Check Dead Letter Queue is enabled
      boolean isPubExp = false;
      if (deadLetterPub != null) {
        logger.debug("REASON:" + reason);
        try {
          deadLetterPub.publish(message, reason);
        }
        catch (Exception e) {
          isPubExp = true;
          e.printStackTrace();
          logger.error("Publishing error message to DeadLetterQ is failed. Message will be written into the error message log file",
                       e);
        }
      }
      // Step 4: This assumes the following points before deleting message from the store.
      //         Retry limit is not enabled or exceeded
      //         Dead Litter queue is not enabled or message published to DLQ
      //         Message is logged.
      if (storeConfigured && isPubExp == false) {
        removeMsg(message);
      }
    }
    else {
      msgStore.preserve(message.getInternalMessageId());
    }
  }

  /**
   *
   * @param message SparrowJMSMessage
   */
  private void handleIgnore(SparrowJMSMessage message) {
    writeToErrorLog(message.getMessage(), "[IGNORED]");
    if (storeConfigured) {
      removeMsg(message);
    }
  }

  /**
   *
   * @param msg String
   * @param exp Exception
   */
  private void writeToErrorLog(String msg, Throwable exp) {
    writeToErrorLog(msg, (String)(exp != null ? exp.getMessage() : null));
  }

  /**
   *
   * @param msg String
   * @param exp Exception
   */
  private void writeToErrorLog(String msg, String reason) {
    if (reason != null) {
      errLogWriter.println("["+SparrowUtil.formatDate(new Date(),"yyyy-MM-dd HH:mm:ss")+"] EXCEPTION===>[" + reason + "]");
    }
    errLogWriter.println(msg +
                       "---------------------------------------------------");
    errLogWriter.flush();
  }

  /**
   *
   * <p>
   * Title:
   * </p>
   * <p>
   * Description:
   * </p>
   * <p>
   * Copyright: Copyright (c) 2004
   * </p>
   * <p>
   * Company:
   * </p>
   *
   * @author Saji Venugopalan
   * @version 1.0
   */
  private class Consumer
      implements Runnable {

    final Session s;

    final Destination d;

    private boolean isTransacted;

    private boolean ackRequired;

    private boolean run = true;

//    private boolean consume = true;

    /**
     *
     * @param s   Session
     * @param d   Destination
     */
    Consumer(Session s, Destination d) {
      this.s = s;
      this.d = d;

      try {
        this.isTransacted = s.getTransacted();
        this.ackRequired = (s.getAcknowledgeMode() != Session.AUTO_ACKNOWLEDGE);
      }
      catch (JMSException ex) {
        throw new InitializationException(
            "JMSException occured while checking session transacted/acknowledgement mode",
            ex);
      }
    }

    /**
     *
     */
    public void close() throws Exception {
      if (s != null) {
        s.close();
      }
    }

    /**
     *
     */
    public void stop() {
      run = false;
    }

    /**
     *
     */
//    public void suspend() {
//      consume = false;
//    }

//    private void waitThread() {
//      try {
//        Thread.sleep(500);
//      }
//      catch (InterruptedException ex) {
//        logger
//            .error(
//            "InterruptedException occured while forcing the thread to sleep",
//            ex);
//      }
//    }

    /**
     *
     */
//    public void resume() {
//      consume = true;
//    }

    /**
     * run
     */
    public void run() {
      MessageConsumer mc = null;
      try {
        mc = s.createConsumer(d);
      }
      catch (JMSException ex) {
        throw new SparrowRuntimeException(
            "JMSException occured while creating consumer", ex);
      }

      while (run) {
        SparrowJMSMessage sprMsg = null;
        Throwable exception = null;
        try {
          if (!isDataLoaderInProgress && (fetchSize==0 || (fetchSize > 0 && (msgCount+1)<= fetchSize))) {

            Message message = mc.receive(1000);

            if (message == null) {
              continue;
            }

            if (isDataLoaderInProgress) {
              if (logger.isDebugEnabled()) {
                logger.debug("DataLoader started after consuming the message");
              }
              waitForConsumerResumeSignal();
            }

            sprMsg = new SparrowJMSMessage(message, headerProps);

            if (printMsg) {
              logger.info("MESSAGE [" + sprMsg.getMessage() + "]");
            }

            if (storeConfigured) {
              msgStore.persist(sprMsg);
            }

            asyncMsgProcessor.process(sprMsg);
            //process(sprMsg);
            
            msgCount++;
            
            if (isTransacted) {
              s.commit();
            }
            else if (ackRequired) {
              message.acknowledge();
            }
          }
          else {
            if (logger.isDebugEnabled()) {
              logger.debug((isDataLoaderInProgress) ? "DataLoader opertation is in progress." : "Message count["+msgCount+"] is > Fetch Size ["+fetchSize+"]");
            }
            Thread.sleep(1000);
          }
        }
        catch (JMSException ex1) {
          exception = ex1;
          logger
              .error(
              "JMSException occured while consuming message",
              ex1);
          if (sprMsg != null) {
            writeToErrorLog(sprMsg.getMessage(), ex1);
          }
          ex1.printStackTrace();
        }
        catch (Exception ex) {
          exception = ex;
          logger.error("Exception occured while consuming message",
                       ex);
          ex.printStackTrace();
        }
        finally {
          if (exception != null) {
            /********************************************************/
            if (sprMsg != null) {
              writeToErrorLog(sprMsg.getMessage(), exception);
              if (storeConfigured) {
                try {
                  msgStore.remove(sprMsg.getInternalMessageId());
                }
                catch (Exception ex3) {
                  ex3.getStackTrace();
                }
              }
            }
            /********************************************************/
            if (isTransacted) {
              try {
                s.rollback();
              }
              catch (JMSException ex2) {
                ex2.printStackTrace();
              }
            }
            /********************************************************/
          }
        }
      }

      // while(true)
    } // run method

    /**
     *
     * @throws InterruptedException
     */
    private void waitForConsumerResumeSignal() throws InterruptedException {
      while (isDataLoaderInProgress) {
        Thread.sleep(500);
      }
    }

  } // Consumer class

  /**
   *
   * <p>
   * Title:
   * </p>
   * <p>
   * Description:
   * </p>
   * <p>
   * Copyright: Copyright (c) 2004
   * </p>
   * <p>
   * Company:
   * </p>
   *
   * @author Saji Venugopalan
   * @version 1.0
   */
  public class MessageRemovalRequestListener
      implements RequestListener {

    public void endProcess() {
    }

    public void process(Object o) throws Exception {
      if (storeConfigured) {
        msgStore.remove(o);
      }
    }

  }

  /**
   *
   * <p>
   * Title:
   * </p>
   * <p>
   * Description:
   * </p>
   * <p>
   * Copyright: Copyright (c) 2004
   * </p>
   * <p>
   * Company:
   * </p>
   *
   * @author Saji Venugopalan
   * @version 1.0
   */
  private class JMSPostProcessAcknowledgement
      implements
      PostProcessAcknowledgement {

    final SparrowJMSMessage msg;

    JMSPostProcessAcknowledgement(SparrowJMSMessage msg) {
      this.msg = msg;
    }

    /**
     *
     * @param flag int
     */
    public void acknowledge(int flag) {

      switch (flag) {
        case IGNORED:
          handleIgnore(msg);
          break;
        case FAILED:
          handleErrors(msg, "Request Failed");
          break;
        default:
          removeMsg(msg);
          //arpMessageRemoval.process(msg.getFileName());
      }
    }
  }

}
