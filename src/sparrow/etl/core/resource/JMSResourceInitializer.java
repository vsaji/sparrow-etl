package sparrow.etl.core.resource;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.NamingException;

import sparrow.etl.core.config.ConfigParam;
import sparrow.etl.core.config.ResourceType;
import sparrow.etl.core.config.SparrowResourceConfig;
import sparrow.etl.core.exception.EventNotifierException;
import sparrow.etl.core.exception.InitializationException;
import sparrow.etl.core.exception.ResourceException;
import sparrow.etl.core.log.SparrowLogger;
import sparrow.etl.core.log.SparrowrLoggerFactory;
import sparrow.etl.core.transaction.SparrowTransaction;
import sparrow.etl.core.transaction.SparrowTransactionManager;
import sparrow.etl.core.util.ConfigKeyConstants;
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
public class JMSResourceInitializer
    implements GenericResourceInitializer {

  private ConnectionFactory factory = null;
  private final String resourceName;
  private Connection jmsConn;
  private JMSResource resource = null;

  protected Context context = null;
  protected final SparrowResourceConfig config;
  protected final ConfigParam param;

  private static SparrowLogger logger = SparrowrLoggerFactory.getCurrentInstance(
      JMSResourceInitializer.class);

  private JMSSessionPoolManager jspm;

  /**
   *
   * @config config ResourceConfig
   */
  public JMSResourceInitializer(SparrowResourceConfig config) {
    this.config = config;
    this.param = config.getInitParameter();
    this.resourceName = config.getName();
  }

  /**
   * initializeResource
   *
   * @config config ConfigParam
   */
  public void initializeResource() {
    try {
      initContext();
      factory = initializeConnectionFactory();
      jspm = new JMSSessionPoolManager(factory, param,context);
      jmsConn = jspm.getConnection();
    }
    catch (Exception ex) {
      throw new InitializationException("RESOURCE_INIT_EXP",
                                        "Exception occured while intializing JMS Connection [" +
                                        resourceName + "][" + ex + "]");
    }
  }

  /**
   *
   * @throws NamingException
   * @return ConnectionFactory
   */
  protected ConnectionFactory initializeConnectionFactory() throws
      NamingException {
    ConnectionFactory factory = (ConnectionFactory) context.lookup(param.
        getParameterValue(
        ConfigKeyConstants.PARAM_CONNECTION_FACTORY));
    return factory;
  }

  /**
   *
   * @throws ResourceException
   * @throws NamingException
   */
  private void initContext() throws ResourceException, NamingException {

    boolean contextExist = param.isParameterExist(ConfigKeyConstants.
                                                  PARAM_CONTEXT);

    context = (contextExist) ? (Context) config.getContext().getResource(param.
        getParameterValue(ConfigKeyConstants.PARAM_CONTEXT)).getResource(
        Resource.NOT_IN_TRANSACTION) : SparrowUtil.getInitialContext(param);
  }

  /**
   * beginApplication
   */
  public void beginApplication() throws EventNotifierException {
    try {

      jmsConn.start();
      logger.info("JMS Connection [" + resourceName +
                  "] has been established");
    }
    catch (JMSException ex) {
      logger.error("JMSException occured while closing connection", ex);
      throw new EventNotifierException("JMS_INIT_BEGIN_APP_JMS_EXP",
                                       ex.getErrorCode() + "-" +
                                       ex.getMessage());

    }
    catch (Exception ex) {
      throw new EventNotifierException("JMS_INIT_BEGIN_APP_EXP",
                                       ex.getMessage());
    }

  }

  /**
   * endApplication
   */
  public void endApplication() {
    try {
      context.close();
      jmsConn.stop();
      jmsConn.close();
      logger.info("JMS Connectoin [" + resourceName +
                  "] has been disconnected and closed.");
      jmsConn = null;
    }
    catch (Exception ex) {
      logger.error(
          "JMSException/NamingException occured while closing connection", ex);
    }
  }

  /**
   * getResource
   *
   * @return Resource
   */
  public Resource getResource() {
    return resource = (resource == null) ? (resource = new JMSResource()) :
        resource;
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
  public class JMSResource
      implements Resource {

    private SparrowTransactionManager stm = null;

    {
      stm = SparrowTransactionManager.getTransactionManager();
    }

    /**
     *
     * @param destinationName String
     * @throws NamingException
     * @return Destination
     */
    public Destination getDestination(String destinationName) throws
        NamingException {
      Destination dst = null;
      if (context != null) {
        dst = (Destination) context.lookup(destinationName);
      }
      else {
        throw new InitializationException("RESOURCE_INIT_EXP","Destination [" + destinationName +
                                          "] could not be obtained. Reason [context is null]");
      }
      return dst;
    }

    /**
     * getName
     *
     * @return String
     */
    public String getName() {
      return resourceName;
    }

    /**
     *
     * @param transFlag int
     * @throws ResourceException
     * @return Session
     */
    public Session getSession(int transFlag) throws ResourceException {
    	Session s = (Session) getResource(transFlag);
    	//System.out.println("#########--->"+s.hashCode());
    	return s;
    }

    /**
     * getResource
     *
     * @return Object
     */
    public Object getResource(int transFlag) throws ResourceException {
      return (transFlag == Resource.IN_TRANSACTION) ? getXAResource() :
          getNonXAResource();
    }

    /**
     * getType
     *
     * @return ResourceType
     */
    public ConfigParam getParam() {
      return param;
    }

    /**
     *
     * @return Object
     */
    private Object getNonXAResource() {
      JMSSessionWrapper sw = jspm.getJMSSession();
      sw.setName(getName());
      return sw;
    }

    /**
     * getXAResource
     *
     * @return Object
     */
    private Object getXAResource() {
      Object session = null;
      try {
        if (stm.isInTransaction()) {
          SparrowTransaction trans = (SparrowTransaction) stm.getTransaction();
          Object tmp = trans.getResource(getName());
          if (tmp != null) {
            session = tmp;
          }
          else {
            JMSSessionWrapper sw = jspm.getJMSSession();

            sw.setName(getName());
            sw.setTrans(true);
            trans.enlistResource(sw);
            session = sw;
          }
        }
        else {
          session = getResource(Resource.NOT_IN_TRANSACTION);
        }
      }
      catch (Exception e) {
    	  e.printStackTrace();
      }
      return session;
    }

    /**
     * getType
     *
     * @return ResourceType
     */
    public ResourceType getType() {
      return ResourceType.getResourceType("JMS");
    }

    /**
     * getResource
     *
     * @return Object
     */
    public Object getResource() throws ResourceException {
      return (stm.isInTransaction()) ? getResource(Resource.IN_TRANSACTION) :
          getResource(Resource.NOT_IN_TRANSACTION);
    }

  }


  /**
   * getPriority
   *
   * @return int
   */
  public int getPriority() {
    return Sortable.PRIORITY_HIGH;
  }


}
