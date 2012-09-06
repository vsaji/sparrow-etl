package sparrow.etl.core.initializer;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import sparrow.etl.core.config.ConfigParam;
import sparrow.etl.core.config.LoadPriorityComparator;
import sparrow.etl.core.config.ResourceConfig;
import sparrow.etl.core.config.ResourcesConfig;
import sparrow.etl.core.config.SparrowResourceConfig;
import sparrow.etl.core.config.UnLoadPriorityComparator;
import sparrow.etl.core.context.SparrowContext;
import sparrow.etl.core.exception.EventNotifierException;
import sparrow.etl.core.exception.InitializationException;
import sparrow.etl.core.log.SparrowLogger;
import sparrow.etl.core.log.SparrowrLoggerFactory;
import sparrow.etl.core.monitor.AppMonitor;
import sparrow.etl.core.monitor.AppObserver;
import sparrow.etl.core.monitor.CycleMonitor;
import sparrow.etl.core.resource.GenericResourceInitializer;
import sparrow.etl.core.resource.Resource;
import sparrow.etl.core.util.Sortable;
import sparrow.etl.core.util.SparrowUtil;


/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */
class ResourceInitializer
    implements Initializable, AppObserver {

  private SparrowContext context = null;
  private List resources = null;
  private HashMap resourceMap = new HashMap();

  private static final SparrowLogger logger = SparrowrLoggerFactory.
      getCurrentInstance(
      ResourceInitializer.class);

  /**
   *
   * @param context SparrowApplicationContextImpl
   */
  ResourceInitializer() {
  }

  /**
   *
   * @throws InitializationException
   */
  public void initialize(SparrowContext context, AppMonitor appMon,
                         CycleMonitor cycleMon) {
    SparrowApplicationContextImpl contxt = (SparrowApplicationContextImpl) context;
    this.context = context;
    appMon.addObserver(this);

    ResourcesConfig resourceConfig = contxt.getConfiguration().
        getResources();
    resources = resourceConfig.getResources();
    resources = sort(resources, new LoadPriorityComparator());

    try {
      if (!resources.isEmpty()) {
        for (Iterator iter = resources.iterator(); iter.hasNext(); ) {
          ResourceConfig item = (ResourceConfig) iter.next();

          GenericResourceInitializer resourceInit = this.getResourceInitializer(
              item);
          try {
            resourceInit.initializeResource();
            Resource r = resourceInit.getResource();
            contxt.getResourceManager().addResource(r);
            resourceMap.put(item.getName(), resourceInit);
          }
          catch (Exception e) {
            logger.error("Error occured while initializing resource [" +
                         item.getName() + "][" + e.getMessage() + "] ");
          }
        }
      }
      else {
        logger.info("No datasource configured");
      }
    }
    catch (InitializationException e) {
      throw e;
    }
    catch (Exception e) {
      throw new InitializationException(e);
    }
  }

  /**
   *
   * @param resources List
   * @return List
   */
  private List sort(List resources, Comparator c) {
    Object[] o = resources.toArray();
    Arrays.sort(o, c);
    return Arrays.asList(o);
  }

  /**
   *
   * @param item DBSourceConfig
   * @throws ClassNotFoundException
   * @throws IllegalAccessException
   * @throws InstantiationException
   * @return IDBSourceInitializer
   */
  private GenericResourceInitializer getResourceInitializer(ResourceConfig item) {

    logger.debug("Initializing Resource : " + item.getName() + ", Type :" +
                 item.getType().getResourceType());
    GenericResourceInitializer initializer = null;
    String InitializerClass = item.getType().getResourceInitializerClass();
    initializer = (GenericResourceInitializer) SparrowUtil.createObject(
        InitializerClass, SparrowResourceConfig.class,
        new SparrowResourceConfigImpl(item));
    return initializer;
  }

  /**
   * beginApplication
   */
  public void beginApplication() throws EventNotifierException {
    for (Iterator it = resources.iterator(); it.hasNext(); ) {
      ResourceConfig key = (ResourceConfig) it.next();
      GenericResourceInitializer gr = (GenericResourceInitializer) resourceMap.
          get(key.getName());
      gr.beginApplication();
    }
  }

  /**
   * endApplication
   */
  public void endApplication() throws EventNotifierException {
    resources = sort(resources, new UnLoadPriorityComparator());
    for (Iterator it = resources.iterator(); it.hasNext(); ) {
      ResourceConfig key = (ResourceConfig) it.next();
      GenericResourceInitializer gr = (GenericResourceInitializer) resourceMap.
          get(key.getName());
      gr.endApplication();
    }
  }

  /**
   *
   * @return int
   */
  public int getPriority(){
    return Sortable.PRIORITY_HIGH;
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
  public class SparrowResourceConfigImpl
      implements SparrowResourceConfig {

    private ResourceConfig item = null;

    SparrowResourceConfigImpl(ResourceConfig item) {
      this.item = item;
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

  }

}
