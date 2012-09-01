package sparrow.etl.core.dao.provider.impl;

import java.util.List;

import sparrow.etl.core.dao.impl.QueryObject;
import sparrow.etl.core.dao.provider.BaseDataProviderElement;
import sparrow.etl.core.dao.provider.DataProvider;
import sparrow.etl.core.dao.provider.DataProviderElementExtn;
import sparrow.etl.core.exception.DataException;


/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */
public class DBDataProviderElement
    extends BaseDataProviderElement
    implements DataProviderElementExtn {

  private DBDataProvider dbprovider = null;

  public DBDataProviderElement() {
  }

  public QueryObject getQuery() {
    return dbprovider.getQuery();
  }

  public void setDataProvider(DataProvider provider) {
    super.setDataProvider(provider);
    dbprovider = (DBDataProvider) provider;
  }

  public void setQuery(QueryObject q) {
    dbprovider.setQuery(q);
  }

  public int executeQuery() throws DataException {
    return dbprovider.executeQuery();
  }

  public int[] executeBatch(List params) throws DataException {
    return dbprovider.executeBatch(params);
  }

  public String getName() {
    return dbprovider.getName();
  }

  public DBDataProvider getDBDataProvider() {
    return dbprovider;
  }

}
