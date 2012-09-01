package sparrow.etl.core.util;

import sparrow.etl.core.log.SparrowLogger;
import sparrow.etl.core.log.SparrowrLoggerFactory;

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */
public class ProxyAsyncRequestProcessor
    extends AsyncRequestProcessor
    implements Runnable {

  private static final SparrowLogger logger = SparrowrLoggerFactory.
      getCurrentInstance(
      AsyncRequestProcessor.class);

  private boolean started = false;

  /**
   *
   * @param name String
   */
  ProxyAsyncRequestProcessor(String name) {
    this(name, 200);
  }

  /**
   *
   * @param name String
   * @param qSize int
   */
  ProxyAsyncRequestProcessor(String name,int qSize) {
    super(name, qSize);
  }


  /**
   * run
   */
  public void run() {
    while (run) {
      AsyncRequest request = null;
      try {
        request = (AsyncRequest) queue.consume();
        request.getRequestListener().process(request.getRequest());
      }
      catch (Exception e) {
        logger.error("Exception occured while processing AsyncRequest[" +
                     request + "]", e);
      }
    }
  }

  /**
   * start
   */
  public void start() {
    if (!started) {
      new Thread(this, name).start();
      started = true;
    }
  }

}
