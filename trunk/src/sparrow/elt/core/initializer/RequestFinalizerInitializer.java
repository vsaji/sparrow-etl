package sparrow.elt.core.initializer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import sparrow.elt.core.RequestFinalizer;
import sparrow.elt.core.RequestFinalizerTxnHandled;
import sparrow.elt.core.RequestFinalizerTxnNonHandled;
import sparrow.elt.core.StandaloneRequestFinalizer;
import sparrow.elt.core.config.ConfigParam;
import sparrow.elt.core.config.DataWritersConfig;
import sparrow.elt.core.config.SparrowDataWriterConfig;
import sparrow.elt.core.config.WriterConfig;
import sparrow.elt.core.context.SparrowApplicationContext;
import sparrow.elt.core.context.SparrowContext;
import sparrow.elt.core.exception.EventNotifierException;
import sparrow.elt.core.exception.InitializationException;
import sparrow.elt.core.fifo.FIFO;
import sparrow.elt.core.log.SparrowLogger;
import sparrow.elt.core.log.SparrowrLoggerFactory;
import sparrow.elt.core.monitor.CycleObserver;
import sparrow.elt.core.monitor.EOPObserver;
import sparrow.elt.core.util.ConfigKeyConstants;
import sparrow.elt.core.util.Constants;
import sparrow.elt.core.util.DependentSequenzer;
import sparrow.elt.core.util.SparrowUtil;
import sparrow.elt.core.writer.DataWriter;
import sparrow.elt.core.writer.DataWriterSynchronizeWrapper;


/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */
public class RequestFinalizerInitializer
    extends GenericThreadInitializer
    implements CycleObserver, EOPObserver {

  private static SparrowLogger logger = SparrowrLoggerFactory.getCurrentInstance(
      RequestFinalizerInitializer.class);

  private static final Map WRITER_EVENT_MAPPING = SparrowUtil.getImplConfig(
      "writer_event");
  private static final Map WRITER_SINGLETON_MAPPING = SparrowUtil.getImplConfig(
      "writer_singleton");

  private final SparrowApplicationContext context;

  private List requestFinalizerHolder = null;

  private List triggerEventEOPList = null;
  private List triggerEventEOAList = null;
  private List triggerEventEOCList = null;
  private List triggerEventBOCList = null;
  private List writerList = null;
  private List writerNames = null;

  private String onError;

  /**
   *
   * @param context SparrowApplicationContext
   */
  RequestFinalizerInitializer(SparrowApplicationContext context) {
    this.requestFinalizerHolder = new ArrayList();
    this.context = context;
    this.triggerEventEOPList = new ArrayList();
    this.triggerEventEOAList = new ArrayList();
    this.triggerEventEOCList = new ArrayList();
    this.triggerEventBOCList = new ArrayList();
    this.writerList = new ArrayList();
    this.writerNames = new ArrayList();
  }

  /**
   *
   * @param context SparrowApplicationContext
   * @param fifo ResponseFIFO
   */
  void initialize(Map writerQueues) {
    try {

      DataWritersConfig writer = context.getConfiguration().getDataWriters();
      onError = writer.getOnError();
      onError = (onError == null) ? "" : onError;

      String rfClass = (Constants.WRITER_OE_FAIL_ALL.equals(onError)) ?
          RequestFinalizerTxnHandled.class.getName() :
          RequestFinalizerTxnNonHandled.class.getName();

      Map singletonRefWriterHolder = new HashMap();

      HashSet triggerEventEOANames = new HashSet();
      HashSet triggerEventEOPNames = new HashSet();
      HashSet triggerEventEOCNames = new HashSet();
      HashSet triggerEventBOCNames = new HashSet();
      HashSet triggerEventBOANames = new HashSet();

      List wrters = writer.getDataWriters();

      validateWriterEvent(wrters);

      List writersInSeq = DependentSequenzer.sequenceDependent(writer.
          getDataWriters());
      Map writersInMap = DependentSequenzer.getItemsInMap(wrters);

      int numberOfThread = writer.getThreadCount();

      String threadName = null;

      for (int i = 0; i < numberOfThread; i++) {
        threadName = Constants.PREFIX_DATA_WRITER + i;

        List dataWriterList = new ArrayList();

        if (logger.isDebugEnabled()) {
          logger.debug("Initializing DataWriter : " + threadName);
        }

        int j = 0;

        for (Iterator iter = writersInSeq.iterator(); iter.hasNext(); j++) {

          String name = (String) iter.next();
          WriterConfig item = (WriterConfig) writersInMap.get(name);

          // Begining of Application Writers
          if (item.getTiggerEvent().equals(Constants.BEGIN_APP)) {
            if (!triggerEventBOANames.contains(item.getName())) {
              DataWriter dw = createDataWriter(item);
              dw.initialize();
              //this.triggerEventBOAList.add(dw);
              triggerEventBOANames.add(item.getName());
            }
            continue;
          }

          // End of Application Writers
          if (item.getTiggerEvent().equals(Constants.END_APP)) {
            if (!triggerEventEOANames.contains(item.getName())) {
              DataWriter dw = createDataWriter(item);
              dw.initialize();
              this.triggerEventEOAList.add(dw);
              triggerEventEOANames.add(item.getName());
            }
            continue;
          }

          // End of Process Writers
          if (item.getTiggerEvent().equals(Constants.END_PROCESS)) {
            if (!triggerEventEOPNames.contains(item.getName())) {
              DataWriter dw = createDataWriter(item);
              dw.initialize();
              this.triggerEventEOPList.add(dw);
              triggerEventEOPNames.add(item.getName());
            }
            continue;
          }

          // End of Cycle Writers
          if (item.getTiggerEvent().equals(Constants.END_CYCLE)) {
            if (!triggerEventEOCNames.contains(item.getName())) {
              DataWriter dw = createDataWriter(item);
              dw.initialize();
              this.triggerEventEOCList.add(dw);
              triggerEventEOCNames.add(item.getName());
            }
            continue;
          }

          // Begining of Cycle Writers
          if (item.getTiggerEvent().equals(Constants.BEGIN_CYCLE)) {
            if (!triggerEventBOCNames.contains(item.getName())) {
              DataWriter dw = createDataWriter(item);
              dw.initialize();
              this.triggerEventBOCList.add(dw);
              triggerEventBOCNames.add(item.getName());
            }
            continue;
          }

          DataWriter dw = null;
          if (item.isSingleInstance() || WRITER_SINGLETON_MAPPING.containsKey(item.getType().
          getWriterTypeAsString())) {

            if (singletonRefWriterHolder.containsKey(
                item.getName())) {
              dw = (DataWriter) singletonRefWriterHolder.get(item.
                  getName());
            }
            else {
              dw = createDataWriter(item);
              dw = new DataWriterSynchronizeWrapper(dw);
              dw.initialize();
              singletonRefWriterHolder.put(item.getName(), dw);
            }

          }
          else {
            dw = createDataWriter(item);
            dw.initialize();
          }
          dataWriterList.add(dw);
        }

        RequestFinalizer rf = (RequestFinalizer) SparrowUtil.createObject(rfClass);

        DataWriter[] writers = (DataWriter[]) dataWriterList.toArray(new
            DataWriter[dataWriterList.size()]);

        StandaloneRequestFinalizer reqFinal = new StandaloneRequestFinalizer(
            context);

        reqFinal.setFIFO((FIFO)writerQueues.get(threadName));
        reqFinal.setRequestFinalizer(rf);
        reqFinal.setWriters(writers);

        super.addThread(reqFinal, threadName);
        this.requestFinalizerHolder.add(reqFinal);
      }

      super.startThreads();
    }
    catch (Exception e) {
      throw new InitializationException(e);
    }
  }

  /**
   *
   * @param item WriterConfig
   * @return DataWriter
   */
  private DataWriter createDataWriter(WriterConfig item) {
    DataWriter dw = (DataWriter) SparrowUtil.createObject(item.getType().
        getWriterClass(),
        SparrowDataWriterConfig.class,
        new SpearDataWriterConfigImpl(item));
    boolean trans = SparrowUtil.performTernary(item,
                                             ConfigKeyConstants.
                                             PARAM_EXEMPT_TRANS, false);
    dw.setInTransaction(!trans);

    if (!writerNames.contains(item.getName())) {
      writerList.add(dw);
      writerNames.add(item.getName());
    }

    return dw;
  }

  /**
   *
   */
  private void validateWriterEvent(List writerConfigs) {
    for (Iterator iter = writerConfigs.iterator(); iter.hasNext(); ) {
      WriterConfig item = (WriterConfig) iter.next();
      String event = (String) WRITER_EVENT_MAPPING.get(item.getType().
          getWriterTypeAsString());
      String writerEvent = item.getTiggerEvent();
      if ((event.indexOf("ALL")!=-1) || (event.indexOf(writerEvent) != -1)) {
        continue;
      }
      else {
        throw new InitializationException("Writer [" + item.getName() +
                                          "] with TYPE[" +
                                          item.getType().getWriterTypeAsString() +
                                          "] does not support TRIGGER-EVENT [" +
                                          writerEvent + "]. Supported is/are [" +
                                          event.substring(0,event.lastIndexOf(";")) + "]");
      }
    }
  }

  /**
   * endCycle
   */
  public void endCycle() throws EventNotifierException {
    ensureFinalizerQueueIsEmpty();

    //Trigger all request Event Writers
    for (Iterator it = requestFinalizerHolder.iterator(); it.hasNext(); ) {
      StandaloneRequestFinalizer rf = (StandaloneRequestFinalizer) it.next();
      rf.endCycle();
    }
    //Trigger End-Of-Cycle Event Writers
    for (Iterator iter = this.triggerEventEOCList.iterator(); iter.hasNext(); ) {
      DataWriter item = (DataWriter) iter.next();
      item.endCycle();
    }

  }

  /**
   *
   * @throws EventNotifierException
   */
  public void beginCycle() throws EventNotifierException {
    //Trigger all request Event Writers
    for (Iterator it = requestFinalizerHolder.iterator(); it.hasNext(); ) {
      StandaloneRequestFinalizer rf = (StandaloneRequestFinalizer) it.next();
      rf.beginCycle();
    }

    //Trigger Begining-Of-Cycle Event Writers
    for (Iterator iter = this.triggerEventBOCList.iterator(); iter.hasNext(); ) {
      DataWriter item = (DataWriter) iter.next();
      item.beginCycle();
    }
  }

  /**
   *
   * @throws EventNotifierException
   */
  public void beginApplication() throws EventNotifierException {
    //Trigger Begining-Of-Application Event Writers
    for (Iterator iter = this.writerList.iterator(); iter.hasNext(); ) {
      DataWriter item = (DataWriter) iter.next();
      item.beginApplication();
    }
  }

  /**
   *
   * @throws EventNotifierException
   */
  public void endApplication() throws EventNotifierException {
    //Trigger Begining-Of-Application Event Writers
    for (Iterator iter = this.writerList.iterator(); iter.hasNext(); ) {
      DataWriter item = (DataWriter) iter.next();
      item.endApplication();
    }
    super.endApplication();
  }

  /**
   *
   */
  private void ensureFinalizerQueueIsEmpty() {
    while (!SparrowUtil.getFinalizerQueueInfo().isQueueEmpty()) {
      try {
        System.out.println("[ensureFinalizerQueueIsEmpty]");
        Thread.sleep(10); //To make sure that end cycle is called only after consuming all the message from the response qeue.
      }
      catch (InterruptedException ex) {
        ex.printStackTrace();
      }
    }
  }

  /**
   * stopProcess
   */
  void stopProcess() {
    logger.warn("Application shutdown initiated");

    for (Iterator iter = this.requestFinalizerHolder.iterator(); iter.hasNext(); ) {
      StandaloneRequestFinalizer item = (StandaloneRequestFinalizer) iter.next();
      item.stopProcess = true;
    }
  }

  /**
   * endOfProcess
   */
  public void endOfProcess(int flag) throws EventNotifierException {
    ensureFinalizerQueueIsEmpty();
    for (Iterator iter = this.requestFinalizerHolder.iterator(); iter.hasNext(); ) {
      StandaloneRequestFinalizer item = (StandaloneRequestFinalizer) iter.next();
      item.endOfProcess(flag);
      break;
    }
    //Trigger all End-Of-Process Event Writers
    if (flag == Constants.EP_NO_RECORD) {
      for (Iterator iter = this.triggerEventEOPList.iterator(); iter.hasNext(); ) {
        DataWriter item = (DataWriter) iter.next();
        item.endOfProcess(flag);
      }
    }
    //Trigger all End-Of-App Event Writers
    if (flag == Constants.EP_END_APP) {

      for (Iterator iter = this.triggerEventEOAList.iterator(); iter.hasNext(); ) {
        DataWriter item = (DataWriter) iter.next();
        item.endOfProcess(flag);
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
  class SpearDataWriterConfigImpl
      implements SparrowDataWriterConfig {

    private WriterConfig item = null;

    SpearDataWriterConfigImpl(WriterConfig item) {
      this.item = item;
    }

    /**
     * getInitParameter
     *
     * @return ConfigParam
     */
    public ConfigParam getInitParameter() {
      return item;
    }

    /**
     * getContext
     *
     * @return SparrowContext
     */
    public SparrowContext getContext() {
      return context;
    }

    /**
     * getName
     *
     * @return String
     */
    public String getName() {
      return item.getName();
    }

    /**
     * getTransformerClass
     *
     * @return String
     */
    public String getDataWriterClass() {
      return item.getClassName();
    }

    /**
     * getOnError
     *
     * @return String
     */
    public String getOnError() {
      return onError;
    }

    /**
     * getDepends
     *
     * @return String
     */
    public String getDepends() {
      return item.getDepends();
    }

    /**
     * getTriggerEvent
     *
     * @return String
     */
    public String getTriggerEvent() {
      return item.getTiggerEvent();
    }

  }

}
