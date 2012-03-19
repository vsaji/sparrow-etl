package sparrow.elt.core.initializer;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import sparrow.elt.core.config.ConfigParam;
import sparrow.elt.core.config.LoadPriorityComparator;
import sparrow.elt.core.config.ResourceConfig;
import sparrow.elt.core.config.ResourcesConfig;
import sparrow.elt.core.config.SparrowResourceConfig;
import sparrow.elt.core.config.UnLoadPriorityComparator;
import sparrow.elt.core.context.SparrowContext;
import sparrow.elt.core.exception.EventNotifierException;
import sparrow.elt.core.exception.InitializationException;
import sparrow.elt.core.log.SparrowLogger;
import sparrow.elt.core.log.SparrowrLoggerFactory;
import sparrow.elt.core.monitor.AppMonitor;
import sparrow.elt.core.monitor.AppObserver;
import sparrow.elt.core.monitor.CycleMonitor;
import sparrow.elt.core.resource.GenericResourceInitializer;
import sparrow.elt.core.resource.Resource;
import sparrow.elt.core.util.Sortable;
import sparrow.elt.core.util.SparrowUtil;


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
        new SpearResourceConfigImpl(item));
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
  public class SpearResourceConfigImpl
      implements SparrowResourceConfig {

    private ResourceConfig item = null;

    SpearResourceConfigImpl(ResourceConfig item) {
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
