package sparrow.elt.core.notifier;

import sparrow.elt.core.config.SparrowNotifierConfig;
import sparrow.elt.core.notifier.event.Event;

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public class WatcherNotifier
    extends AsyncNotifier {

  public WatcherNotifier(SparrowNotifierConfig type) {
    //replace it with NotifierConfig object
    super(type);
  }

  /**
   * process
   *
   * @param event Event
   */
  public void processEvent(Event event) {
    System.out.print("WatcherNotifer:" + event.getMessage());
  }

  /**
   * getConfig
   *
   * @return SparrowNotifierConfig
   */
  public SparrowNotifierConfig getConfig() {
    return config;
  }

  /**
   * close
   */
  public void close() {
    arp.close();
  }

  /**
   * initialize
   */
  public void initialize() {
    arp.start();
  }

  /**
   * endProcess
   */

}
