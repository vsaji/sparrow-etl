package sparrow.etl.core.notifier;

import java.util.Iterator;
import java.util.Map;

import sparrow.etl.core.exception.EvaluatorException;
import sparrow.etl.core.exception.NotifierException;
import sparrow.etl.core.log.SparrowLogger;
import sparrow.etl.core.log.SparrowrLoggerFactory;
import sparrow.etl.core.notifier.event.Event;
import sparrow.etl.core.notifier.event.EventEvaluator;
import sparrow.etl.core.notifier.event.EventManager;
import sparrow.etl.core.util.AsyncRequestProcessor;
import sparrow.etl.core.util.CaseInSensitiveMap;
import sparrow.etl.core.util.RequestListener;


/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public class NotificationManager {

  private final EventManager em;
  private final Map OBJECT_REPOSITORY = new CaseInSensitiveMap();
  private boolean started = false;

  private static AsyncRequestProcessor arp = null;
  private static NotificationManager _instance;
  private static final String LISTNER_NAME = "NM";
  private static final SparrowLogger logger = SparrowrLoggerFactory.
      getCurrentInstance(
      NotificationManager.class);
  private final AsyncNotificationListner listener;

  /**
   *
   */
  private NotificationManager() {
    em = new EventManager();
    listener = new AsyncNotificationListner();
    arp = AsyncRequestProcessor.createAsynchProcessor("NOTIFICATION_MANAGER");
    arp.registerListener(LISTNER_NAME, listener);
  }

  /**
   *
   * @return NotificationManager
   */
  public static final NotificationManager getInstance() {
    _instance = (_instance == null) ? (_instance = new NotificationManager()) :
        _instance;
    return _instance;
  }

  /**
   *
   * @param config SpearNotifiersConfig
   */
  public void addNotifier(Notifier notifier) {
    OBJECT_REPOSITORY.put(notifier.getConfig().getName(), notifier);
  }

  /**
   *
   * @param config SpearNotifiersConfig
   */
  public void addEvent(EventEvaluator ee) {
    em.addEventEvaluator(ee);
  }

  /**
   *
   * @param event Event
   * @throws NotifierException
   * @throws EvaluatorException
   */
  public void notify(Event event) {
    arp.process(LISTNER_NAME, event);
  }

  /**
   *
   * @param event Event
   */
  public void notifyOnline(Event event) throws NotifierException,
      EvaluatorException {
    System.out.println("notifyOnline");
    listener.process(event);
  }


  /**
   *
   * @param name String
   * @throws NotifierException
   * @return Notifier
   */
  public Notifier getNotifier(String name) throws NotifierException {
    return (Notifier) OBJECT_REPOSITORY.get(name);
  }

  /**
   *
   */
  public void start() {
    if (!started) {
      arp.start();
      started = true;
    }
  }

  /**
    *
    */
   public void stop() {
     if (started) {
       arp.close();
       em.close();
       destoryNotifiers();
       OBJECT_REPOSITORY.clear();
       started = false;
     }
   }

   /**
    *
    */
   private void destoryNotifiers(){
     Iterator it = OBJECT_REPOSITORY.values().iterator();
     while(it.hasNext()){
       ((Notifier)it.next()).close();
     }
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
  private  class AsyncNotificationListner
      implements RequestListener {
    /**
     * endProcess
     */
    public void endProcess() {

    }

    /**
     * process
     *
     * @param o Object
     */
    public void process(Object o) throws EvaluatorException, NotifierException {
      Event event = (Event) o;
      if (event.getType() != null) {
        EventEvaluator ee = em.getEventEvaluator(event.getType());
        if (ee != null) {
          ee.evaluate(event);
        }
      }

      String notifierName = event.getNotifierName();

      if (notifierName != null) {
        if (notifierName.toLowerCase().equals("all")) {
          notifyAll(event);
        }
        else if (notifierName.indexOf(",") != -1) {
          notifyMultiple(event, notifierName);
        }
        else {
          Notifier notifier = getNotifier(notifierName);
          notifier.sendNotification(event);
        }

      }

    }

    /**
     *
     * @param event Event
     */
    private void notifyAll(Event event) {
      Iterator it = OBJECT_REPOSITORY.values().iterator();
      while (it.hasNext()) {
        try {
          ( (Notifier) it.next()).sendNotification(event);
        }
        catch (NotifierException ex) {
          logger.error("[notifyAll]:" + ex.getMessage());
        }
      }
    }

    /**
     *
     * @param event Event
     * @param notifierName String
     * @throws NotifierException
     */
    private void notifyMultiple(Event event, String notifierName) {

      String[] notifiersName = notifierName.split("[,]");

      for (int i = 0; i < notifiersName.length; i++) {
        try {
          ( (Notifier) OBJECT_REPOSITORY.get(notifiersName[i])).
              sendNotification(
              event);
        }
        catch (NotifierException ex) {
          logger.error("[notifyMultiple]:" + ex.getMessage());
        }
      }
    }

  }

}
