package sparrow.elt.impl.services;

import sparrow.elt.core.exception.EventNotifierException;
import sparrow.elt.core.services.PluggableService;
import sparrow.elt.core.util.Sortable;

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */
public  class BaseService
    implements PluggableService {
  public BaseService() {
  }

  /**
   * beginCycle
   */
  public void beginCycle() {
  }

  /**
   * endCycle
   */
  public void endCycle() {
  }

  /**
   * getService
   *
   * @param serviceName Object
   * @return Object
   */
  public Object getService(Object serviceName) {
    return this;
  }

  /**
   * getService
   *
   * @return Object
   */
  public Object getService() {
    return this;
  }

  /**
   * getPriority
   *
   * @return int
   */
  public int getPriority() {
    return Sortable.PRIORITY_HIGH;
  }

  /**
   * beginApplication
   */
  public void beginApplication() throws EventNotifierException {
  }

  /**
   * endApplication
   */
  public void endApplication() throws EventNotifierException {
  }

}
