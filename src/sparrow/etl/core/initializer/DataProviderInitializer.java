package sparrow.etl.core.initializer;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;

import sparrow.etl.core.config.ConfigParam;
import sparrow.etl.core.config.DataProviderConfig;
import sparrow.etl.core.config.SparrowDataProviderConfig;
import sparrow.etl.core.context.SparrowApplicationContext;
import sparrow.etl.core.context.SparrowContext;
import sparrow.etl.core.dao.cache.CacheStorage;
import sparrow.etl.core.dao.cache.SparrowEventCacheProvider;
import sparrow.etl.core.dao.provider.BaseDataProviderElement;
import sparrow.etl.core.dao.provider.DataProvider;
import sparrow.etl.core.dao.provider.DataProviderElementExtn;
import sparrow.etl.core.dao.provider.DataProviderElementResolver;
import sparrow.etl.core.dao.provider.impl.CacheDataProvider;
import sparrow.etl.core.dao.provider.impl.IncrementalCacheDataProvider;
import sparrow.etl.core.dao.provider.impl.MapBasedCacheProvider;
import sparrow.etl.core.dao.provider.impl.MapBasedIncrementalCacheProvider;
import sparrow.etl.core.monitor.AppMonitor;
import sparrow.etl.core.monitor.CycleMonitor;
import sparrow.etl.core.util.ConfigKeyConstants;
import sparrow.etl.core.util.Constants;
import sparrow.etl.core.util.SparrowUtil;


//import sparrow.elt.core.dao.cache.CacheDataProvider;

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */
public class DataProviderInitializer
    implements Initializable {


  public DataProviderInitializer() {
  }

  /**
   * initialize
   *
   * @param context SparrowContext
   * @param appMon AppMonitor
   * @param cycleMon CycleMonitor
   */
  public void initialize(SparrowContext context, AppMonitor appMon,
                         CycleMonitor cycleMon) {


    List dataProviders = ( (SparrowApplicationContext) context).getConfiguration().
        getDataProviders().getProviders();

    HashMap dpIdentifier = new HashMap();

    HashMap objectMap = new HashMap();

    CacheStorage cs = new CacheStorage();

    DataProviderElementResolver dhcr = new DataProviderElementResolver();

    SparrowEventCacheProvider secp = new SparrowEventCacheProvider(cs);
    cycleMon.addObserver(secp);
    appMon.addObserver(secp);

    String baseDPClass, key = null;

    for (Iterator iter = dataProviders.iterator(); iter.hasNext(); ) {

      DataProviderConfig item = (DataProviderConfig) iter.next();

      if (item.isParameterExist(ConfigKeyConstants.PARAM_DP_CACHE_TYPE)) {

        String cacheType = item.getParameterValue(ConfigKeyConstants.
                                                  PARAM_DP_CACHE_TYPE);

        dpIdentifier.put(item.getName(), Constants.CACHE_RESOLVER_KEY);


        boolean incrementalCache = cacheType.equals(Constants.
            CACHE_TYPE_INCREMENTAL);

        BaseDataProviderElement baseDP = (BaseDataProviderElement)
            getDataProviderElement(item.getType().getProviderElementClass(),
                                   item.getType().getProviderClass(),
                                   context, item, true, incrementalCache);

        objectMap.put(item.getName(),baseDP);

        // END.CYCLE CACHE Handling
        if (cacheType.equals(Constants.END_CYCLE)) {
          secp.registerForEndCycleRefresh(baseDP);
          continue;
        }
        // BEGIN.CYCLE CACHE Handling
        else if (cacheType.equals(
            Constants.BEGIN_CYCLE)) {
          secp.registerForBeginCycleRefresh(baseDP);
          continue;
        }
        // BEGIN.APP CACHE Handling
        else if (cacheType.equals(
            Constants.BEGIN_APP)) {
          secp.registerForBeginAppRefresh(baseDP);
          continue;
        }
        // INCREMENTAL CACHE Handling
        else if (cacheType.equals(Constants.CACHE_TYPE_INCREMENTAL)) {
          secp.registerForBeginAppRefresh(baseDP);
          String flushEvent = item.getParameterValue(ConfigKeyConstants.
              PARAM_CACHE_FLUSH_EVENT);
          if (flushEvent != null) {
            // INCREMENTAL CACHE : FLUSH EVENT->end.cycle handling
            if (Constants.END_CYCLE.equals(flushEvent)) {
              secp.registerForEndCycleFlush(baseDP);
            }
            // INCREMENTAL CACHE : FLUSH EVENT->begin.cycle handling
            else if (Constants.BEGIN_CYCLE.equals(flushEvent)) {
              secp.registerForBeginCycleFlush(baseDP);
            }
          }
          continue;
        }
        // TIMER.REFRESH CACHE Handling
        else if (cacheType.equals(Constants.CACHE_TYPE_TIMER_REFRESH)) {

          long sleepTime = SparrowUtil.performTernaryForLong(item,
              ConfigKeyConstants.PARAM_DP_CACHE_REFRESH_TIME, 1000);
          sleepTime = (sleepTime < 1000) ? 1000 : sleepTime;

          cs.addDataProviderElement(baseDP);
          new Timer().schedule(baseDP, 0, sleepTime);
          continue;
        }
      }
      else {

        String providerClass = item.getType().getProviderClass();
        baseDPClass = item.getType().getProviderElementClass();
        key = baseDPClass + "@" + providerClass;
        dpIdentifier.put(item.getName(), key);

        if (!objectMap.containsKey(item.getName())) {

          //      poolFactoryMap.put(key,
          //                         new DataProviderElementPoolManager(baseDPClass,
          //          providerClass, isDBProvider, context));

          objectMap.put(item.getName(),
                             getDataProviderElement(baseDPClass, providerClass,
              context, item, false, false));

        }
        continue;
      }
    }

    dhcr.setCacheStorage(cs);
    dhcr.setDataProviderObjectMap(objectMap);
    dhcr.setResolver(dpIdentifier);
    ( (SparrowApplicationContextImpl) context).setDataProviderElementResolver(
        dhcr);
  }

  /**
   *
   * @param baseDBClass String
   * @param providerClass String
   * @param isDBProvider boolean
   * @param context SparrowContext
   * @return DataProviderElementExtn
   */
  private DataProviderElementExtn getDataProviderElement(String
      baseDBClass,
      String providerClass, SparrowContext context, DataProviderConfig item,
      boolean cache, boolean incrementalCache) {

    DataProviderElementExtn obj = (DataProviderElementExtn) SparrowUtil.
        createObject(baseDBClass);
    SparrowDataProviderConfig impl = new SpearDataProviderConfigImpl(item,context);
    DataProvider provider = (DataProvider) SparrowUtil.createObject(providerClass,
        SparrowDataProviderConfig.class, impl);
    if (cache) {
    	boolean lKeysExist = item.isParameterExist(ConfigKeyConstants.PARAM_LOOKUP_KEYS);
      if(lKeysExist){
     	  provider = (incrementalCache) ? new MapBasedIncrementalCacheProvider(provider,impl):
    		  new MapBasedCacheProvider(provider,impl);
      }else{
    	  provider = (incrementalCache) ? new IncrementalCacheDataProvider(provider,impl):
    		  new CacheDataProvider(provider,impl);
      }
      provider.initialize();
    }
    obj.setDataProvider(provider);
    return obj;
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
    private class SpearDataProviderConfigImpl
        implements SparrowDataProviderConfig {

      private final DataProviderConfig item;
      private final SparrowContext context;

      SpearDataProviderConfigImpl(DataProviderConfig item,SparrowContext context) {
        this.item = item;
        this.context = context;
      }

      /**
       * getInitParameter
       *
       * @return ConfigParam
       */
      public ConfigParam getInitParameter() {
        return item;
      }

      /**
       * getContext
       *
       * @return SparrowContext
       */
      public SparrowContext getContext() {
        return context;
      }

      /**
       * getName
       *
       * @return String
       */
      public String getName() {
        return item.getName();
      }

    /**
     * getClassName
     *
     * @return String
     */
    public String getClassName() {
      return item.getType().getProviderClass();
    }
  }
}
