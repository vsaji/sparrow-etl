package sparrow.etl.core.resource;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import sparrow.etl.core.config.ConfigParam;
import sparrow.etl.core.config.ResourceType;
import sparrow.etl.core.config.SparrowResourceConfig;
import sparrow.etl.core.exception.EventNotifierException;
import sparrow.etl.core.exception.InitializationException;
import sparrow.etl.core.log.SparrowLogger;
import sparrow.etl.core.log.SparrowrLoggerFactory;
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
public class InitialContextInitializer
    implements GenericResourceInitializer {

  private SpearInitialContext context = null;
  private final ConfigParam param;
  private final String resourceName;

  private static SparrowLogger logger = SparrowrLoggerFactory.getCurrentInstance(
      InitialContextInitializer.class);

  /**
   *
   * @config config ResourceConfig
   */
  public InitialContextInitializer(SparrowResourceConfig config) {
    this.param = config.getInitParameter();
    this.resourceName = config.getName();
  }

  /**
   * beginApplication
   */
  public void beginApplication() {
    logger.info("InitialContext [" + resourceName + "] has obtained.");
  }

  /**
   * endApplication
   */
  public void endApplication() throws EventNotifierException {
    try {
      context.doClose();
      logger.info("InitialContext [" + resourceName + "] has been closed.");
    }
    catch (NamingException ex) {
      throw new EventNotifierException("INIT_CXT_INTI_END_APP_EXP", ex);
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
   * initializeResource
   */
  public void initializeResource() {
    try {
      context = new SpearInitialContext(param);
    }
    catch (Exception ex) {
      throw new InitializationException("RESOURCE_INIT_EXP",
                                        "Exception occured while intializing context [" +
                                        resourceName + "][" + ex + "]");
    }
  }

  /**
   * getResource
   *
   * @return Resource
   */
  public Resource getResource() {
    Resource r = new Resource() {

      public String getName() {
        return resourceName;
      }

      public ConfigParam getParam() {
        return param;
      }

      public Object getResource(int transFlag) {
        if(context==null){
          initializeResource();
        }
        return context;
      }

      public ResourceType getType() {
        return ResourceType.getResourceType("CONTEXT");
      }

      /**
       * getResource
       *
       * @return Object
       */
      public Object getResource() {
        return getResource(Resource.NOT_IN_TRANSACTION);
      }
    };
    return r;
  }

  /**
   *
   * <p>Title: </p>
   * <p>Description: </p>
   * <p>Copyright: Copyright (c) 2004</p>
   * <p>Company: </p>
   * @author not attributable
   * @version 1.0
   */
  private class SpearInitialContext
      extends InitialContext {

    SpearInitialContext(ConfigParam param) throws NamingException {
      super();
      super.init(SparrowUtil.getInitialContextProperties(param));
    }

    public void close() throws NamingException {
    }

    public void doClose() throws NamingException {
      super.close();
    }

  }

}
