package sparrow.elt.core.dao.provider;

import java.util.TimerTask;

import sparrow.elt.core.dao.impl.RecordSet;
import sparrow.elt.core.dao.provider.impl.CacheDataProvider;
import sparrow.elt.core.exception.DataException;


/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */
public class BaseDataProviderElement
    extends TimerTask
    implements DataProviderElementExtn {

  protected DataProvider provider = null;

  /**
   *
   */
  public BaseDataProviderElement() {
  }

  /**
   *
   * @throws CloneNotSupportedException
   * @return Object
   */
  public Object clone() throws CloneNotSupportedException {
    DataProviderElementExtn dpe = (DataProviderElementExtn)super.clone();
    dpe.setDataProvider( (DataProvider) provider.clone());
    return dpe;
  }

  /**
   *
   * @param provider DataProvider
   */
  public void setDataProvider(DataProvider provider) {
    this.provider = provider;
  }

  /**
   *
   * @return DataProvider
   */
  public DataProvider getDataProvider() {
    return provider;
  }

  /**
   * getData
   *
   * @return Object
   */
  public RecordSet getData() throws DataException {
    return provider.getData();
  }

  /**
   * getName
   *
   * @return String
   */
  public String getName() {
    return provider.getName();
  }

  /**
   * close
   */
  public void close() {
    provider.destory();
  }


  /**
   * run
   */
  public void run() {
    CacheDataProvider cdp = (CacheDataProvider) provider;
    try {
      cdp.loadData();
    }
    catch (DataException d) {
      d.printStackTrace();
    }
  }

  /**
   * initialize
   */
  public void initialize() {
    provider.initialize();
  }

  /**
   * destroy
   */
  public void destroy() {
    provider.destory();
  }

}
