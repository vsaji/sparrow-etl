package sparrow.elt.core.initializer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import sparrow.elt.core.exception.EventNotifierException;
import sparrow.elt.core.monitor.AppObserver;
import sparrow.elt.core.util.Sortable;


/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */
abstract class GenericThreadInitializer
    implements AppObserver {

  protected List threadHolder = new ArrayList();

  protected void startThreads() {
    for (Iterator iter = threadHolder.iterator(); iter.hasNext(); ) {
      Thread item = (Thread) iter.next();
      item.start();
    }
  }

  protected void addThread(Runnable runnable, String threadName) {
    threadHolder.add(new Thread(runnable, threadName));
  }

  protected void addThread(Runnable runnable) {
    threadHolder.add(new Thread(runnable));
  }

  abstract void stopProcess();

  /**
   * beginApplication
   */
  public void beginApplication() throws EventNotifierException{
  }

  /**
   * endApplication
   */
  public void endApplication() throws EventNotifierException{
    stopProcess();
    for (Iterator iter = threadHolder.iterator(); iter.hasNext(); ) {
      Thread item = (Thread) iter.next();
      item.stop();
    }

  }

  /**
   *
   * @return int
   */
  public int getPriority() {
    return Sortable.PRIORITY_ABOVE_LOW;
  }

}
