/* Generated by Together */

package sparrow.etl.core.context;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import sparrow.etl.core.dao.provider.DataProviderElement;
import sparrow.etl.core.exception.ProviderNotFoundException;
import sparrow.etl.core.exception.ResourceException;
import sparrow.etl.core.exception.ServiceUnavailableException;
import sparrow.etl.core.resource.Resource;
import sparrow.etl.core.services.PluggableService;
import sparrow.etl.core.transaction.SparrowTransactionManager;


/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public interface SparrowContext {
  public Connection getDBConnection(String dsName) throws SQLException;

  public Resource getResource(String dsName) throws ResourceException;

  public Connection getTransactionEnabledDBConnection(String dsName) throws
      SQLException;

  public SparrowTransactionManager getTransactionManager();

  public PluggableService getService(String serviceName) throws
      ServiceUnavailableException;

  public String getContextParam(String paramName);

  public DataProviderElement getDataProviderElement(String dataProviderName) throws
      ProviderNotFoundException;

  public String getAppName();

  public String getProcessId();

  public String getAppDescription();

  public void setAttribute(String name,Object value);

  public Object getAttribute(String name);

  public Object getBean(String name);

  public Object getBean(String resourceName,String name);

  public Map getAttributes();
}
