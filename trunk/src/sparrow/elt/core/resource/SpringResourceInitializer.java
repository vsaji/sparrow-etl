package sparrow.elt.core.resource;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import sparrow.elt.core.config.ConfigParam;
import sparrow.elt.core.config.ResourceType;
import sparrow.elt.core.config.SparrowResourceConfig;
import sparrow.elt.core.log.SparrowLogger;
import sparrow.elt.core.log.SparrowrLoggerFactory;
import sparrow.elt.core.util.ConfigKeyConstants;
import sparrow.elt.core.util.SparrowUtil;

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public class SpringResourceInitializer
    implements GenericResourceInitializer {

  private static final String CLASSPATH = "classpath";
  private static final String FILESYSTEM = "filesystem";

  private final SparrowResourceConfig config;
  private ApplicationContext ac;
  /**
   *
   */
  private static SparrowLogger logger = SparrowrLoggerFactory.getCurrentInstance(
      SpringResourceInitializer.class);

  /**
   *
   * @param config SparrowResourceConfig
   */
  public SpringResourceInitializer(SparrowResourceConfig config) {
    SparrowUtil.validateParam(new String[] {ConfigKeyConstants.PARAM_CONTEXT_FILE}
                            , "SpringResourceInitializer",
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
    String contextFiles = config.getInitParameter().getParameterValue(
        ConfigKeyConstants.PARAM_CONTEXT_FILE);
    String[] files = contextFiles.split("[,]");
    String loader = SparrowUtil.performTernary(config.getInitParameter(),
                                             ConfigKeyConstants.PARAM_CONTEXT_LOC, CLASSPATH);

    if (FILESYSTEM.equals(loader)) {
      ac = new FileSystemXmlApplicationContext(files);
    }
    else {
      ac = new ClassPathXmlApplicationContext(files);
    }

    logger.info("Spring Application Context has been initialized with [" +
                files.length + "] file(s). Context Files [" + contextFiles +
                "]");
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
      public Object getResource() {
        return ac;
      }

      /**
       * getResource
       *
       * @param transFlag int
       * @return Object
       */
      public Object getResource(int transFlag) {
        return getResource();
      }

      /**
       * getType
       *
       * @return ResourceType
       */
      public ResourceType getType() {
        return ResourceType.getResourceType("SPRING");
      }
    };

    return r;
  }

}
