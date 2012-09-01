package sparrow.etl.core.util;

import sparrow.etl.core.exception.SemaphoreException;


/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */
public interface Semaphore {

  public abstract void acquireOnStart() throws SemaphoreException;

  public abstract void releaseOnEnd() throws SemaphoreException;

  public abstract boolean isProcessTerminationFlagOn() throws SemaphoreException;

}
