package sparrow.elt.core.initializer;

import java.util.Iterator;
import java.util.Map;

import sparrow.elt.core.config.ConfigParam;
import sparrow.elt.core.config.EventConfig;
import sparrow.elt.core.config.NotifierConfig;
import sparrow.elt.core.config.NotifiersConfig;
import sparrow.elt.core.config.SparrowEventConfig;
import sparrow.elt.core.config.SparrowNotifierConfig;
import sparrow.elt.core.context.SparrowApplicationContext;
import sparrow.elt.core.context.SparrowContext;
import sparrow.elt.core.log.SparrowLogger;
import sparrow.elt.core.log.SparrowrLoggerFactory;
import sparrow.elt.core.monitor.AppMonitor;
import sparrow.elt.core.monitor.AppObserver;
import sparrow.elt.core.monitor.CycleMonitor;
import sparrow.elt.core.notifier.NotificationManager;
import sparrow.elt.core.notifier.Notifier;
import sparrow.elt.core.notifier.event.EventEvaluator;
import sparrow.elt.core.util.SparrowUtil;


/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public class NotifierInitializer
    implements AppObserver {

  private static final SparrowLogger logger = SparrowrLoggerFactory.
    getCurrentInstance(
    NotifierInitializer.class);


  NotificationManager nm = NotificationManager.getInstance();

  public NotifierInitializer() {
  }

  /**
   *
   * @param context SparrowApplicationContext
   * @param appMon AppMonitor
   * @param cycleMon CycleMonitor
   */
  public void initialize(SparrowApplicationContext context, AppMonitor appMon,
                         CycleMonitor cycleMon) {

    NotifiersConfig config = context.getConfiguration().getNotifiers();
    Map notifrs = config.getNotifiers();

    Iterator it = null;

    if (notifrs != null) {
      //-----------------------------------------------
      it = notifrs.keySet().iterator();
      String key = null;

      while (it.hasNext()) {

        key = (String) it.next();

        SparrowNotifierConfig snConfig = new SpearNotifierConfigImpl( (
            NotifierConfig) config.getNotifiers().get(key),
            context);

        Notifier notifier = (Notifier) SparrowUtil.createObject(snConfig.
            getClassName(), SparrowNotifierConfig.class, snConfig);
        notifier.initialize();
        nm.addNotifier(notifier);
      }

      Map events = config.getEvents();
      //-----------------------------------------------
      if (events != null) {
        it = config.getEvents().keySet().iterator();

        while (it.hasNext()) {
          key = (String) it.next();
          SparrowEventConfig seConfig = new SpearEventConfigImpl( (
              EventConfig) config.getEvents().get(key),
              context);

          EventEvaluator ee = (EventEvaluator) SparrowUtil.createObject(seConfig.
              getClassName(), SparrowEventConfig.class, seConfig);
          nm.addEvent(ee);
        }

      }
    }
    nm.start();
    appMon.addObserver(this);
  }

  /**
   * beginApplication
   */
  public void beginApplication() {

  }

  /**
   * endApplication
   */
  public void endApplication() {
    nm.stop();
    logger.warn("NotificationManger shutdown initiated");
  }

  /**
   * getPriority
   *
   * @return int
   */
  public int getPriority() {
    return AppObserver.PRIORITY_LOW;
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
  private class SpearNotifierConfigImpl
      implements SparrowNotifierConfig {

    NotifierConfig config;
    SparrowContext context;

    /**
     *
     * @param config NotifierConfig
     * @param context SparrowContext
     */
    SpearNotifierConfigImpl(NotifierConfig config, SparrowContext context) {
      this.config = config;
      this.context = context;
    }

    /**
     * getContext
     *
     * @return SparrowContext
     */
    public SparrowContext getContext() {
      return context;
    }

    /**
     * getInitParameter
     *
     * @return ConfigParam
     */
    public ConfigParam getInitParameter() {
      return config;
    }

    /**
     * getName
     *
     * @return String
     */
    public String getName() {
      return config.getName();
    }

    /**
     * getClassName
     *
     * @return String
     */
    public String getClassName() {
      return config.getClassName();
    }

    /**
     * getType
     *
     * @return String
     */
    public String getType() {
      return config.getType();
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
  private class SpearEventConfigImpl
      implements SparrowEventConfig {

    EventConfig config;
    SparrowContext context;

    /**
     *
     * @param config NotifierConfig
     * @param context SparrowContext
     */
    SpearEventConfigImpl(EventConfig config, SparrowContext context) {
      this.config = config;
      this.context = context;
    }

    /**
     * getContext
     *
     * @return SparrowContext
     */
    public SparrowContext getContext() {
      return context;
    }

    /**
     * getInitParameter
     *
     * @return ConfigParam
     */
    public ConfigParam getInitParameter() {
      return config;
    }

    /**
     * getName
     *
     * @return String
     */
    public String getName() {
      return config.getType();
    }

    /**
     * getClassName
     *
     * @return String
     */
    public String getClassName() {
      return config.getClassName();
    }

    /**
     * getType
     *
     * @return String
     */
    public String getType() {
      return config.getType();
    }

    /**
     * getNotifierName
     *
     * @return String
     */
    public String getNotifierName() {
      return config.getNotifierName();
    }

  }

}
