package sparrow.elt.core.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import sparrow.elt.core.fifo.Queue;
import sparrow.elt.core.log.SparrowLogger;
import sparrow.elt.core.log.SparrowrLoggerFactory;


/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */
public abstract class AsyncRequestProcessor {

  private static final HashMap REPOSITORY = new HashMap();
  private static Timer timer = null;

  protected final String name;
  protected final Queue queue;
  protected boolean run = true;
  protected RequestListener defaultListener = null;

  protected HashMap LISTENER_REGISTRY = new HashMap();

  private static final SparrowLogger logger = SparrowrLoggerFactory.
      getCurrentInstance(
      AsyncRequestProcessor.class);

  /**
   *
   * @param name String
   * @param queueSize int
   */
  AsyncRequestProcessor(String name, int queueSize) {
    this.name = name;
    this.queue = new Queue(name, queueSize);
  }

  /**
   *
   * @param listenerName String
   * @param request Object
   */
  public void process(String listenerName, Object request) {
    RequestListener rl = (RequestListener) LISTENER_REGISTRY.get(listenerName);
    if (rl == null) {
      throw new NullPointerException("RequestListener [" + listenerName +
                                     "] does not exist");
    }
    queue.produce(new AsyncRequest(rl, request));
  }


  /**
   *
   * @param request Object
   */
  public void process(Object request) {
      queue.produce(new AsyncRequest(defaultListener, request));
  }



  /**
   *
   */
  public void close() {
    logger.warn("Closing AsyncRequestProcessor instance["+this.name+"]. Listeners "+LISTENER_REGISTRY.keySet()+" will be closed.");
    String[] listeners = (String[]) LISTENER_REGISTRY.keySet().toArray(new
        String[LISTENER_REGISTRY.keySet().size()]);
    for (int i = 0; i < listeners.length; i++) {

      repeat:for (; ; ) {
        if (isRequestQueueEmpty()) {
          destroy(listeners[i]);
          break repeat;
        }
        else {
          waitForRequestCompletion();
          continue repeat;
        }
      }
    }
    run = false;
    REPOSITORY.remove(this.name);
    logger.warn("AsyncRequestProcessor instance["+this.name+"] closed.");
  }

  /**
   *
   */
  private void waitForRequestCompletion() {
    try {
      Thread.sleep(500);
    }
    catch (InterruptedException ex) {
      logger.error(ex.getLocalizedMessage(), ex);
    }
  }

  /**
   *
   * @param name String
   * @param p RequestListener
   */
  public void registerListener(String name, RequestListener p) {
    if (!LISTENER_REGISTRY.containsKey(name)) {
      LISTENER_REGISTRY.put(name, p);
    }
  }

  /**
   *
   * @param defaultListener RequestListener
   */
  public void registerListener(RequestListener defaultListener) {
    registerListener(this.name, defaultListener);
    this.defaultListener = defaultListener;
  }


  /**
   *
   * @param name String
   * @return RequestListener
   */
  public RequestListener getRequestListener(String name) {
    return (RequestListener) LISTENER_REGISTRY.get(name);
  }

  /**
   *
   * @return RequestListener
   */
  public RequestListener getRequestListener() {
    return defaultListener;
  }


  /**
   *
   * @return int
   */
  public boolean isRequestQueueEmpty() {
    return queue.isQueueEmpty();
  }

  /**
   *
   * @param name String
   * @return AsyncRequestProcessor
   */
  public final static AsyncRequestProcessor createAsynchProcessor(String name) {
    return createAsynchProcessor(name,200);
  }

  /**
   *
   * @param name String
   * @param qDepth int
   * @return AsyncRequestProcessor
   */
  public final static AsyncRequestProcessor createAsynchProcessor(String name,int qDepth) {
    ProxyAsyncRequestProcessor aop = null;
    if (REPOSITORY.containsKey(name)) {
      aop = (ProxyAsyncRequestProcessor) REPOSITORY.get(name);
    }
    else {
      aop = new ProxyAsyncRequestProcessor(name,qDepth);
      REPOSITORY.put(name, aop);
    }
    return aop;
  }

  /**
   *
   */
  public abstract void start();

  /**
   *
   * @param name String
   * @return AsyncRequestProcessor
   */
  public final static AsyncRequestProcessor getAsynchProcessor(String name) {
    return (AsyncRequestProcessor) REPOSITORY.get(name);
  }

  /**
   *
   * @param p RequestListener
   */
  private void destroy(RequestListener p) {
    p.endProcess();
  }

  /**
   *
   * @param p RequestListener
   */
  public void destroy(String name) {
    destroy(getRequestListener(name));
    removeListener(name);
  }

  /**
   *
   * @param key String
   */
  private void removeListener(String key) {
    LISTENER_REGISTRY.remove(key);
  }

  /**
   *
   */
  static final void beginApplication() {
    timer = new Timer();
    timer.schedule(new ThreadMonitor(), 0, 15000);
  }

  /**
   * endApplication
   */
  static final void endApplication() {
    if (SparrowUtil.isThisCallFromCore()) {
      for (Iterator it = REPOSITORY.keySet().iterator(); it.hasNext(); ) {
        AsyncRequestProcessor aop = (AsyncRequestProcessor) it.next();
        aop.close();
      }
    }
    timer.cancel();
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
  private static class ThreadMonitor
      extends TimerTask {
    /**
     * run
     */
    public void run() {
      if (logger.isDebugEnabled()) {
        logger.log("AsyncRequestProcessor Thread Monitor : Total services [" +
                   REPOSITORY.size() + "]", SparrowLogger.DEBUG);
      }
    }
  }

  public String toString() {
    return LISTENER_REGISTRY.toString();
  }

}
