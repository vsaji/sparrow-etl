package sparrow.elt.core.resource;

import java.util.Properties;
import javax.mail.Session;

import sparrow.elt.core.config.ConfigParam;
import sparrow.elt.core.config.ResourceType;
import sparrow.elt.core.config.SparrowResourceConfig;
import sparrow.elt.core.exception.InitializationException;
import sparrow.elt.core.log.SparrowLogger;
import sparrow.elt.core.log.SparrowrLoggerFactory;
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
public class EmailResourceInitializer
    implements GenericResourceInitializer {

  private static final String MAIL_HOST = "mail.smtp.host";
  private static final String MAIL_PORT = "mail.smtp.port";
  private static final String MAIL_TRANSPORT_PROTOCOL =
      "mail.transport.protocol";

  private Session session = null;
  private final SparrowResourceConfig config;

  /**
   *
   */
  private static SparrowLogger logger = SparrowrLoggerFactory.getCurrentInstance(
      EmailResourceInitializer.class);

  /**
   *
   * @param config SparrowResourceConfig
   */
  public EmailResourceInitializer(SparrowResourceConfig config) {
    SparrowUtil.validateParam(new String[] {MAIL_HOST}
                            , "EmailResourceInitializer",
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
    Properties properties = new Properties();
    properties.setProperty(MAIL_TRANSPORT_PROTOCOL, "smtp");
    String hostName = config.getInitParameter().getParameterValue(MAIL_HOST);
    String smtpPort = SparrowUtil.performTernary(config.getInitParameter(),
                                               MAIL_PORT, "25");

    properties.setProperty(MAIL_PORT, smtpPort);
    properties.setProperty(MAIL_HOST, hostName);

    logger.info("["+config.getName()+"]-Initializing SMTP Session with HOST [" + hostName + ":" +
                smtpPort + "]");
    try {
      this.session = Session.getInstance(properties);
    }
    catch (Exception e) {
      throw new InitializationException("RESOURCE_INIT_EXP",
                                        "Exception occured while intializing STMP Session HOST [" +
                                        MAIL_HOST + ":" + smtpPort + "][" + e +
                                        "]");
    }
    logger.info("["+config.getName()+"]-SMTP Session Initialized");
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
        if(session==null){
          initializeResource();
        }
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
        return ResourceType.getResourceType("SMTP");
      }
    };

    return r;
  }

}
