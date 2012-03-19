package sparrow.elt.core.notifier.event;

import java.util.HashMap;
import java.util.Map;

import sparrow.elt.core.util.CaseInSensitiveMap;

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public class EventManager {

  private final Map objectRepository = new CaseInSensitiveMap();

  public EventManager() {
  }

  /**
   *
   * @param type String
   * @return EventEvaluator
   */
  public EventEvaluator getEventEvaluator(String type) {
    return (EventEvaluator)objectRepository.get(type);
  }

  public void addEventEvaluator(EventEvaluator ee) {
    objectRepository.put(ee.getConfig().getType(), ee);
  }

  public void close(){
    objectRepository.clear();
  }

}
