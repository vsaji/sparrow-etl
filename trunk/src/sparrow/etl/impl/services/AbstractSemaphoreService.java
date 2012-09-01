package sparrow.etl.impl.services;

import sparrow.etl.core.config.SparrowServiceConfig;
import sparrow.etl.core.exception.EventNotifierException;
import sparrow.etl.core.exception.SemaphoreException;
import sparrow.etl.core.initializer.SparrowApplicationContextImpl;
import sparrow.etl.core.log.SparrowLogger;
import sparrow.etl.core.log.SparrowrLoggerFactory;
import sparrow.etl.core.util.ConfigKeyConstants;
import sparrow.etl.core.util.Semaphore;
import sparrow.etl.core.util.SparrowUtil;

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
