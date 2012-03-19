package sparrow.elt.core.dao.cache;

import java.util.ArrayList;
import java.util.Iterator;

import sparrow.elt.core.dao.provider.DataProviderElement;
import sparrow.elt.core.dao.provider.DataProviderElementExtn;
import sparrow.elt.core.dao.provider.impl.CacheDataProvider;
import sparrow.elt.core.exception.DataException;
import sparrow.elt.core.exception.EventNotifierException;
import sparrow.elt.core.monitor.AppObserver;
import sparrow.elt.core.monitor.CycleObserver;
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
public class SparrowEventCacheProvider
    implements CycleObserver, AppObserver {

  private final CacheStorage cs;
  private final ArrayList refreshEndCycle;
  private final ArrayList refreshBeginCycle;
  private final ArrayList refreshBeginApp;
  private final ArrayList flushEndCycle;
  private final ArrayList flushBeginCycle;

  /**
   *
   * @param cs CacheStorage
   */
  public SparrowEventCacheProvider(CacheStorage cs) {
    this.cs = cs;
    refreshEndCycle = new ArrayList();
    refreshBeginCycle = new ArrayList();
    refreshBeginApp = new ArrayList();
    flushEndCycle = new ArrayList();
    flushBeginCycle = new ArrayList();
  }

  /**
   *
   * @param providerContext DataProviderElement
   */
  public void registerForEndCycleRefresh(DataProviderElement providerContext) {
    refreshEndCycle.add(providerContext.getName());
    cs.addDataProviderElement(providerContext);
  }

  /**
   *
   * @param providerContext DataProviderElement
   */

  public void registerForBeginCycleRefresh(DataProviderElement providerContext) {
    refreshBeginCycle.add(providerContext.getName());
    cs.addDataProviderElement(providerContext);
  }

  /**
   *
   * @param providerContext DataProviderElement
   */

  public void registerForBeginAppRefresh(DataProviderElement providerContext) {
    refreshBeginApp.add(providerContext.getName());
    cs.addDataProviderElement(providerContext);
  }

  /**
   *
   * @param providerContext DataProviderElement
   */

  public void registerForBeginCycleFlush(DataProviderElement providerContext) {
    flushBeginCycle.add(providerContext.getName());
    cs.addDataProviderElement(providerContext);
  }

  /**
   *
   * @param providerContext DataProviderElement
   */
  public void registerForEndCycleFlush(DataProviderElement providerContext) {
    flushEndCycle.add(providerContext.getName());
    cs.addDataProviderElement(providerContext);
  }


  /**
   * beginCycle
   */
  public void beginCycle() throws EventNotifierException {
    try {
      refreshData(refreshBeginCycle);
       flushData(flushBeginCycle);
    }
    catch (DataException ex) {
      EventNotifierException e = new EventNotifierException(
          "CACHE_PROVIDER_BC_DATA_EXP",
          ex.getMessage());
      throw e;
    }
    catch (Exception ex) {
      EventNotifierException e = new EventNotifierException(
          "CACHE_PROVIDER_BC_EXP",
          ex.getMessage());
      throw e;
    }

  }

  /**
   * endCycle
   */
  public void endCycle() throws EventNotifierException {
    try {
      refreshData(refreshEndCycle);
      flushData(flushEndCycle);
    }
    catch (DataException ex) {
      EventNotifierException e = new EventNotifierException(
          "CACHE_PROVIDER_EC_DATA_EXP",
          ex.getMessage());
      throw e;
    }
    catch (Exception ex) {
      EventNotifierException e = new EventNotifierException(
          "CACHE_PROVIDER_EC_EXP",
          ex.getMessage());
      throw e;
    }

  }

  /**
   * beginApplication
   */
  public void beginApplication() throws EventNotifierException {
    try {
      refreshData(refreshBeginApp);
    }
    catch (DataException ex) {
      EventNotifierException e = new EventNotifierException(
          "CACHE_PROVIDER_BA_DATA_EXP",
          ex.getMessage());
      throw e;
    }
    catch (Exception ex) {
      EventNotifierException e = new EventNotifierException(
          "CACHE_PROVIDER_BA_EXP",
          ex.getMessage());
      throw e;
    }
  }

  /**
   * endApplication
   */
  public void endApplication() throws EventNotifierException {
  }

  private void refreshData(ArrayList l) throws DataException {
    for (Iterator iter = l.iterator(); iter.hasNext(); ) {
      String itemName = (String) iter.next();
      DataProviderElementExtn providerContext = (DataProviderElementExtn) cs.
          getDataProviderElement(itemName);
      CacheDataProvider cdp = (CacheDataProvider) providerContext.getDataProvider();
      cdp.loadData();
    }
  }

  /**
   *
   * @param l ArrayList
   * @throws DataException
   */
  private void flushData(ArrayList l) throws DataException {
    for (Iterator iter = l.iterator(); iter.hasNext(); ) {
      String itemName = (String) iter.next();
      DataProviderElementExtn providerContext = (DataProviderElementExtn) cs.
          getDataProviderElement(itemName);
      CacheDataProvider cdp = (CacheDataProvider) providerContext.getDataProvider();
      cdp.flushData();
    }
  }

  /**
   * getPriority
   *
   * @return int
   */
  public int getPriority() {
    return Sortable.PRIORITY_MEDIUM;
  }

}
