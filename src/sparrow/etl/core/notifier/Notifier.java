package sparrow.etl.core.notifier;

import sparrow.etl.core.config.SparrowNotifierConfig;
import sparrow.etl.core.exception.NotifierException;
import sparrow.etl.core.notifier.event.Event;

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
