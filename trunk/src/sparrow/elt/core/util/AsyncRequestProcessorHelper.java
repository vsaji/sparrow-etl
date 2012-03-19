package sparrow.elt.core.util;

import sparrow.elt.core.monitor.AppObserver;

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */
public class AsyncRequestProcessorHelper
    implements AppObserver {
  public AsyncRequestProcessorHelper() {
  }

  /**
   * beginApplication
   */
  public void beginApplication() {
    AsyncRequestProcessor.beginApplication();
  }

  /**
   * endApplication
   */
  public void endApplication() {
    AsyncRequestProcessor.endApplication();
  }

  public int getPriority(){
    return Sortable.PRIORITY_ABOVE_LOW;
  }

}
