package sparrow.elt.core.exception;

import java.util.HashMap;
import java.util.Map;

import sparrow.elt.core.config.ExcepHandler;
import sparrow.elt.core.config.ExceptionHandlerConfig;
import sparrow.elt.core.config.Handle;
import sparrow.elt.core.config.IConfiguration;
import sparrow.elt.core.log.SparrowLogger;
import sparrow.elt.core.log.SparrowrLoggerFactory;
import sparrow.elt.core.notifier.NotificationManager;
import sparrow.elt.core.util.Constants;
import sparrow.elt.core.util.SparrowUtil;


/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */
public abstract class ExceptionHandler {

  private static ExceptionHandlerConfig expConfig = null;
  private static boolean initialized = false;
  private static Map handlerCache = null;
  private static boolean shutdownInProgress = false;
  private static ClassMap classMap = new ClassMap();
  private static NotificationManager notificationManager = NotificationManager.
      getInstance();
  protected static Object source = null;

  /**
   * Holds references to Logger object
   */
  private static final SparrowLogger logger = SparrowrLoggerFactory.
      getCurrentInstance(
      ExceptionHandler.class);

  /**
   *
   * @param shtdwnInProgress boolean
   */
  public static final void setShutdownInProgress(boolean shtdwnInProgress) {
    shutdownInProgress = shtdwnInProgress;
  }

  /**
   *
   * @param config IConfiguration
   */
  static final void initialize(IConfiguration config) {
    if (!initialized) {
      expConfig = config.getExceptionHandler();
      initialized = true;
      handlerCache = new HashMap();
    }
  }


  /**
 *
 * @param t Throwable
 * @return boolean
 */
public static final boolean isRegistered(Throwable t){
  return (ProxyExceptionHandler.getExceptionHandle(t.getClass().getName(),t)!=null);
}


  /**
   *
   * @param t Throwable
   * @return ExceptionHandler
   */
  public static final ExceptionHandler getHandler(Throwable t) {
    /**try {
      notificationManager.notifyOnline(new ExceptionEvent(t));
         }
         catch (EvaluatorException ex) {
         }
         catch (NotifierException ex) {
         }
     **/

    return new ProxyExceptionHandler(t);
  }

  /**
   *
   */
  public final void setFatalOnly() {

    if (!shutdownInProgress) {

      logger.info(
          "setFatalOnly called by ["+getHandlerClass()+"]. Reason ["+getDetails()+"] [PROCESS SHUTTING DOWN]");
      System.exit(Constants.ERROR_EXIT);
    }
    else {
      logger.info("Shutdown has already been initiated by CORE ENGINE");
    }
  }

  /**
   *
   * @return boolean
   */
  public abstract boolean isFatal();

  /**
   *
   * @return boolean
   */
  public abstract boolean isRetriable();

  /**
   *
   * @return boolean
   */
  public abstract boolean isIgnorable();

  /**
   *
   * @return boolean
   */
  public String getDetails(){
    return "[Root handler]";
  }

  /**
   *
   * @return String
   */
  public String getHandlerClass(){
    return "ExceptionHandler";
  }

  /**
   *
   * <p>Title: </p>
   * <p>Description: </p>
   * <p>Copyright: Copyright (c) 2004</p>
   * <p>Company: </p>
   * @author Saji Venugopalan
   * @version 1.0
   */
  private static final class ProxyExceptionHandler
      extends ExceptionHandler {

    private ExceptionHandler handler = null;
    private Throwable t;

    /**
     *
     */
    ProxyExceptionHandler(Throwable t) {
      this.t = t;
      handle();
    }

    /**
     *
     * @param t Throwable
     */
    public void handle() {
      String className = t.getClass().getName();
      handler = getHandler(className, t);
      if (handler != null) {
        handleDefinedException(t);
      }
    }

    /**
     * handleDefinedException
     *
     * @param t Throwable
     */
    private void handleDefinedException(Throwable t) {
      try {
        SparrowUtil.invokeMethod(handler, "handle",
                               new Class[] {classMap.getClass(t)}
                               , new Object[] {t});
      }
      catch (MethodInvocationException ex) {
    	 if(t instanceof SparrowException){
    		 logger.warn("Exception ["+t.getClass().getName()+"] is undefined in ["+handler.getHandlerClass()+"],so trying with Base Exception [SparrowException]");
    		 try {
				SparrowUtil.invokeMethod(handler, "handle",
				         new Class[] {SparrowException.class}
				         , new Object[] {t});
			} catch (MethodInvocationException e) {
				e.printStackTrace();
			}
    	 }else{
    		 handler = null;
    	 }
      }
//      catch (Exception ex) {
//        logger.error("Exception occured :" + ex.getMessage());
//        handler = null;
//      }
    }

    /**
       *
       * @return String
       */
      public String getDetails(){
        return "{"+t.getClass().getName()+"}-{"+t.getMessage()+"}";
      }


    /**
     * isFatal
     *
     * @return boolean
     */
    public boolean isFatal() {
      return (handler != null) ? handler.isFatal() : false;
    }

    /**
     * isRetriable
     *
     * @return boolean
     */
    public boolean isRetriable() {
      return (handler != null) ? handler.isRetriable() : false;
    }

    /**
     *
     * @param expClassName String
     * @return Handle
     */
     static final Handle getExceptionHandle(String expClassName, Throwable t) {
      Handle handle = null;
      if (expConfig.getHandles() != null) {
        Object hndl = expConfig.getHandles().get(expClassName);

        if (hndl == null) {
          String superClass = getSuperClass(t).getName();
          hndl = expConfig.getHandles().get(superClass);
          if (hndl != null) {
            classMap.setSuperClassFound(expClassName, getSuperClass(t));
          }
        }

        handle = (hndl == null) ? null : (Handle) hndl;
      }
      return handle;
    }

    /**
     *
     * @param expClassName String
     * @param t Throwable
     * @return ExceptionHandler
     */
    ExceptionHandler getHandler(String expClassName, Throwable t) {
      ExceptionHandler handler = null;
      try {
        if (!handlerCache.containsKey(expClassName)) {
          Handle handle = getExceptionHandle(expClassName, t);
          if (handle != null) {
            handler = (ExceptionHandler) SparrowUtil.createObject(
                getHandlerClass(handle.getHandlerName()), ErrorConfig.class,
                new ErrorConfig(handle));
            handlerCache.put(expClassName, handler);
          }
          else {
            return null;
          }
        }
        else {
          handler = (ExceptionHandler) handlerCache.get(expClassName);
        }
      }
      catch (Exception e) {
        return null;
      }

      return handler;
    }

    /**
     *
     * @param handlerName String
     * @return String
     */
    String getHandlerClass(String handlerName) {
      ExcepHandler handler = (ExcepHandler) expConfig.getHandlers().get(
          handlerName);
      return handler.getClassName();
    }

    /**
     * isIgnorable
     *
     * @return boolean
     */
    public boolean isIgnorable() {
      return (handler != null) ? handler.isIgnorable() : false;
    }

    /**
     *
     * @param t Throwable
     * @return Class
     */
    private static Class getSuperClass(Throwable t) {
      return t.getClass().getSuperclass();
    }

    /**
     * getHandlerClass
     *
     * @return String
     */
    public String getHandlerClass() {
      return handler.getHandlerClass();
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
  static class ClassMap {

    private Map classMap = new HashMap();

    /**
     *
     * @param t Throwable
     * @return Class
     */
    Class getClass(Throwable t) {
      Class c = (Class) classMap.get(t.getClass().getName());
      return (c == null) ? t.getClass() : c;
    }

    /**
     *
     * @param t Class
     */
    void setSuperClassFound(String baseClassName, Class superClass) {
      classMap.put(baseClassName, superClass);
    }

  }

}
