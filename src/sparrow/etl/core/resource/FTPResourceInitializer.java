package sparrow.etl.core.resource;

import java.util.Map;

import sparrow.etl.core.config.ConfigParam;
import sparrow.etl.core.config.ResourceType;
import sparrow.etl.core.config.SparrowResourceConfig;
import sparrow.etl.core.exception.InitializationException;
import sparrow.etl.core.log.SparrowLogger;
import sparrow.etl.core.log.SparrowrLoggerFactory;
import sparrow.etl.core.util.ConfigKeyConstants;
import sparrow.etl.core.util.SparrowUtil;
import sparrow.etl.core.vo.FTPParamHolder;


/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public class FTPResourceInitializer
    implements GenericResourceInitializer {

  private final SparrowResourceConfig config;
  private final FTPParamHolder paramHolder;
  private final String implClass;

  private static final Map IMPL = SparrowUtil.getImplConfig("ftp");

  /**
   *
   */
  private static SparrowLogger logger = SparrowrLoggerFactory.getCurrentInstance(
      FTPResourceInitializer.class);

  /**
   *
   * @param config SparrowResourceConfig
   */
  public FTPResourceInitializer(SparrowResourceConfig config) {
    SparrowUtil.validateParam(new String[] {ConfigKeyConstants.
                            PARAM_HOST_NAME, ConfigKeyConstants.PARAM_USER_NAME,
                            ConfigKeyConstants.PARAM_PASSWORD}
                            , "FTPResourceInitializer",
                            config.getInitParameter());
    this.config = config;

    paramHolder = new FTPParamHolder(config.getInitParameter());
    String implementation = SparrowUtil.performTernary(config.getInitParameter(),
        ConfigKeyConstants.PARAM_CHANNEL, "default");
    implClass = (String) IMPL.get(implementation);

    if (implClass == null) {
      throw new InitializationException("FTP Implementation [" + implementation +
                                        "] is invalid.");
    }

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
    logger.info("[" + config.getName() +
                "]-FTP Connection will be initialized upon request.");
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
        FTPClientSession session = (FTPClientSession) SparrowUtil.createObject(implClass,
            FTPParamHolder.class, paramHolder);
        return session;

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
        return ResourceType.getResourceType("FTP");
      }
    };

    return r;
  }

}
