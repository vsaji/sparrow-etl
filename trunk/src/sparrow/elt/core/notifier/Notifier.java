package sparrow.elt.core.notifier;

import sparrow.elt.core.config.SparrowNotifierConfig;
import sparrow.elt.core.exception.NotifierException;
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
public interface Notifier {

  public abstract void initialize();
  public abstract void sendNotification(Event event) throws NotifierException;
  public abstract SparrowNotifierConfig getConfig();
  public abstract void close();

}
