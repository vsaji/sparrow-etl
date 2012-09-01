package sparrow.etl.core.resource;

import javax.naming.Context;
import javax.naming.NamingException;

import sparrow.etl.core.config.ConfigParam;
import sparrow.etl.core.config.ResourceType;
import sparrow.etl.core.config.SparrowResourceConfig;
import sparrow.etl.core.exception.ResourceException;
import sparrow.etl.core.log.SparrowLogger;
import sparrow.etl.core.log.SparrowrLoggerFactory;
import sparrow.etl.core.util.ConfigKeyConstants;
import sparrow.etl.core.util.SparrowUtil;


/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public class EJBResourceInitializer
    implements GenericResourceInitializer {

  private final SparrowResourceConfig config;
  private String resourceName, jndiName = null;

  /**
   *
   */
  private static SparrowLogger logger = SparrowrLoggerFactory.getCurrentInstance(
      EJBResourceInitializer.class);

  /**
   *
   * @param config SparrowResourceConfig
   */
  public EJBResourceInitializer(SparrowResourceConfig config) {
    SparrowUtil.validateParam(new String[] {ConfigKeyConstants.PARAM_LOOKUP}
                            , "EJBResourceInitializer",
                            config.getInitParameter());
    this.config = config;
  }

  /**
   * beginApplication
   */
  public void beginApplication() {
  }

  /**
   * endApplication
   */
  public void endApplication() {
  }

  /**
   * initializeResource
   */
  public void initializeResource() {
    String[] lookup = config.getInitParameter().getParameterValue(
        ConfigKeyConstants.PARAM_LOOKUP).split("[@]");
    resourceName = lookup[0];
    jndiName = lookup[1];
    logger.info("EJB Lookup initialized for [" + resourceName + "@" + jndiName +
                "]. Object will be looked-up upon request.");
  }

  /**
   * getPriority
   *
   * @return int
   */
  public int getPriority() {
    return PRIORITY_ABOVE_MEDIUM;
  }

  /**
   * getResource
   *
   * @return Resource
   */
  public Resource getResource() {

    Resource r = new Resource() {
      /**
       * getName
       *
       * @return String
       */
      public String getName() {
        return config.getName();
      }

      /**
       * getParam
       *
       * @return ConfigParam
       */
      public ConfigParam getParam() {
        return config.getInitParameter();
      }

      /**
       * getResource
       *
       * @return Object
       */
      public Object getResource() throws ResourceException {
        Context ctx = null;
        try {
          ctx = (Context) config.getContext().getResource(resourceName).
              getResource();
          return ctx.lookup(jndiName);
        }
        catch (ResourceException ex) {
          throw ex;
        }
        catch (NamingException ex1) {
          ex1.printStackTrace();
          return new ResourceException("NAMING_EXCEPTION",
              "NamingException occured while lookup resource [" + jndiName +
                                       "]");
        }
      }

      /**
       * getResource
       *
       * @param transFlag int
       * @return Object
       */
      public Object getResource(int transFlag) throws ResourceException {
        return getResource();
      }

      /**
       * getType
       *
       * @return ResourceType
       */
      public ResourceType getType() {
        return ResourceType.getResourceType("EJB");
      }
    };

    return r;
  }

}
