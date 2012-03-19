package sparrow.elt.impl.services;

import sparrow.elt.core.config.SparrowServiceConfig;
import sparrow.elt.core.exception.EventNotifierException;
import sparrow.elt.core.exception.SemaphoreException;
import sparrow.elt.core.initializer.SparrowApplicationContextImpl;
import sparrow.elt.core.log.SparrowLogger;
import sparrow.elt.core.log.SparrowrLoggerFactory;
import sparrow.elt.core.util.ConfigKeyConstants;
import sparrow.elt.core.util.Semaphore;
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
public abstract class AbstractSemaphoreService
    extends BaseService
    implements Semaphore {

  private static final SparrowLogger logger = SparrowrLoggerFactory.
      getCurrentInstance(
      AbstractSemaphoreService.class);



  protected final SparrowServiceConfig config;
  private final boolean semaphoreCheckRequired;

  /**
   *
   */
  public AbstractSemaphoreService(SparrowServiceConfig config) {
    this.config = config;
    this.semaphoreCheckRequired = SparrowUtil.performTernary( ( (
        SparrowApplicationContextImpl) config.getContext()).getConfiguration().
        getModule(), ConfigKeyConstants.PARAM_SEMAPHORE_CHECK, false);

  }

  /**
   *
   * @throws EventNotifierException
   */
  public void beginApplication() throws EventNotifierException {
    if (semaphoreCheckRequired) {
      try {
        acquireOnStart();
      }
      catch (SemaphoreException ex) {
        ex.printStackTrace();
        throw new EventNotifierException(ex);
      }
    }
    else {
      logger.info("SEMAPHORE CHECK IGNORED");
    }

  }

  /**
   * endApplication
   */
  public void endApplication() throws EventNotifierException {
    if (semaphoreCheckRequired) {
      try {
        releaseOnEnd();
      }
      catch (SemaphoreException ex) {
        throw new EventNotifierException(ex);
      }
    }
    else {
      logger.info("SEMAPHORE CHECK IGNORED");
    }

  }

  /**
   * isProcessTerminationFlagOn
   *
   * @return boolean
   */
  public boolean isProcessTerminationFlagOn() throws SemaphoreException {
    return false;
  }

  /**
   * acquireOnStart
   */
  public abstract void acquireOnStart() throws SemaphoreException;

  /**
   * releaseOnEnd
   */
  public abstract void releaseOnEnd() throws SemaphoreException;
}
