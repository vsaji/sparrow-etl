package sparrow.elt.core.services;

import sparrow.elt.core.exception.SchedulerException;

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public interface SchedulerService extends PluggableService{

  public abstract void executeTask() throws SchedulerException;

}
