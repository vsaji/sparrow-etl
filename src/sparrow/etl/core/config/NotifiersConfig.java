package sparrow.etl.core.config;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import sparrow.etl.jaxb.EVENTType;
import sparrow.etl.jaxb.NOTIFIERSType;
import sparrow.etl.jaxb.NOTIFIERType;


/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public interface NotifiersConfig {

  abstract Map getNotifiers();

  abstract Map getEvents();

}

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
class NotifiersConfigImpl
    implements NotifiersConfig {

  private Map notifiers = null;
  private Map events = null;

  /**
   *
   * @param handler EXCEPTIONHANDLERType
   */
  NotifiersConfigImpl(NOTIFIERSType notifiers) {
    if (notifiers != null) {
      bind(notifiers.getNOTIFIER(), notifiers.getEVENTS().getEVENT());
    }
  }

  /**
   * getNotifiers
   *
   * @return Map
   */
  public Map getNotifiers() {
    return notifiers;
  }

  /**
   * getEvents
   *
   * @return Map
   */
  public Map getEvents() {
    return events;
  }


  /**
   *
   * @param hndlrs List
   * @param hndls List
   */
  private void bind(List notifrs, List evnts) {

      if (!notifrs.isEmpty()) {
        notifiers = new HashMap(notifrs.size());
        for (Iterator iter = notifrs.iterator(); iter.hasNext(); ) {
          NOTIFIERType item = (NOTIFIERType) iter.next();
          addNotifier(item.getNAME(), new NotifierConfigImpl(item));
        }
      }

      if (evnts!=null && !evnts.isEmpty()) {
        events = new HashMap(evnts.size());
        for (Iterator iter = evnts.iterator(); iter.hasNext(); ) {
          EVENTType item = (EVENTType) iter.next();
          addEvent(item.getTYPE(), new EventConfigImpl(item));
        }
      }

    }


    /**
     *
     * @param name String
     * @param notifier NotifierConfig
     */
    private void addNotifier(String name, NotifierConfig notifier) {
      notifiers.put(name, notifier);
    }

    /**
     *
     * @param type String
     * @param event EventConfig
     */
    private void addEvent(String type, EventConfig event) {
      events.put(type, event);
    }



}
