package sparrow.elt.core.resource;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.Session;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.naming.Context;
import javax.naming.NamingException;

import org.apache.commons.pool.BasePoolableObjectFactory;
import org.apache.commons.pool.impl.StackObjectPool;

import sparrow.elt.core.config.ConfigParam;
import sparrow.elt.core.exception.InitializationException;
import sparrow.elt.core.exception.SparrowRuntimeException;
import sparrow.elt.core.log.SparrowLogger;
import sparrow.elt.core.log.SparrowrLoggerFactory;
import sparrow.elt.core.util.ConfigKeyConstants;
import sparrow.elt.core.util.Constants;
import sparrow.elt.core.util.IObjectPoolLifeCycle;
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
public class JMSSessionPoolManager
    implements IObjectPoolLifeCycle {

  private StackObjectPool pool;
  private final Connection jmsConn;
  private final boolean transacted;
  private final int msgAckType;
  private TopicConnection topicConn;
  private QueueConnection queueConn;

  /**
   *
   */
  private static final SparrowLogger logger = SparrowrLoggerFactory.
      getCurrentInstance(
      JMSSessionPoolManager.class);

  /**
   *
   * @param jmsConn Connection
   */
  public JMSSessionPoolManager(Connection jmsConn) {
    this.jmsConn = jmsConn;
    this.pool = new StackObjectPool(new JMSSessionPoolFactory(this),
                                    5,
                                    5);
    this.transacted = false;
    this.msgAckType = Session.AUTO_ACKNOWLEDGE;
  }

  /**
   *
   * @param jmsFac ConnectionFactory
   * @param jmsType String
   */
  public JMSSessionPoolManager(ConnectionFactory jmsFac, ConfigParam param,
                               Context ctx) {
    try {

      String jmsType = SparrowUtil.performTernary(param, ConfigKeyConstants.
                                                PARAM_JMS_TYPE,
                                                Constants.JMS_TYPE_QUEUE);

      this.transacted = SparrowUtil.performTernary(param, ConfigKeyConstants.
                                                 PARAM_SESS_TRANS, false);

      boolean secureCon = SparrowUtil.performTernary(param,
          ConfigKeyConstants.PARAM_SECURE_CON, false);

      String[] cre = null;

      if (secureCon) {
        cre = getCredential(ctx);
      }

      this.msgAckType = param.isParameterExist(ConfigKeyConstants.
                                               PARAM_MSG_ACK_TYPE) ?
          getAckType(param.getParameterValue(ConfigKeyConstants.
                                             PARAM_MSG_ACK_TYPE)) :
          Session.AUTO_ACKNOWLEDGE;

      if (Constants.JMS_TYPE_TOPIC.equals(jmsType)) {
        this.topicConn = (secureCon) ?
            ( (TopicConnectionFactory) jmsFac).createTopicConnection(cre[0],
            cre[1]) :
            ( (TopicConnectionFactory) jmsFac).createTopicConnection();
        this.jmsConn = this.topicConn;
      }
      else if (Constants.JMS_TYPE_QUEUE.equals(jmsType)) {
        this.queueConn = (secureCon) ?
            ( (QueueConnectionFactory) jmsFac).createQueueConnection(cre[0],
            cre[1])
            : ( (QueueConnectionFactory) jmsFac).createQueueConnection();
        this.jmsConn = this.queueConn;
      }
      else {
        this.jmsConn = (secureCon) ?
            ( (ConnectionFactory) jmsFac).createConnection(cre[0], cre[1]) :
            ( (ConnectionFactory) jmsFac).createConnection();
        this.topicConn = null;
        this.queueConn = null;
      }

      this.pool = new StackObjectPool(new JMSSQueueTopicSessionPoolFactory(this,
          jmsType), 25, 25);
    }
    catch (JMSException ex) {
      throw new InitializationException(
          "Exception occured while initializing connection", ex);
    }

  }

  /**
   *
   * @param ctx Context
   * @return String[]
   */
  private String[] getCredential(Context ctx) {
    String[] cre = new String[2];
    try {
      cre[0] = (String) ctx.getEnvironment().get(Context.SECURITY_PRINCIPAL);
      cre[1] = (String) ctx.getEnvironment().get(Context.SECURITY_CREDENTIALS);
    }
    catch (NamingException ex) {
      ex.printStackTrace();
      return new String[] {
          null, null};
    }
    return cre;
  }

  /**
   *
   * @param ackType String
   * @return int
   */
  private int getAckType(String ackType) {
    if (ackType.equals("dupok")) {
      return Session.DUPS_OK_ACKNOWLEDGE;
    }
    else if (ackType.equals("client")) {
      return Session.CLIENT_ACKNOWLEDGE;
    }
    else {
      return Session.AUTO_ACKNOWLEDGE;
    }
  }

  /**
   *
   * @return Connection
   */
  Connection getConnection() {
    return this.jmsConn;
  }

  /**
   * returned
   *
   * @param o Object
   */
  public void returned(Object o) {
    try {
      pool.returnObject(o);
    }
    catch (Exception ex) {
      throw new SparrowRuntimeException(
          "Exception occured while retruning object to pool [" +
          ex.getMessage() + "]");

    }

  }

  /**
   * getUserObject
   *
   * @param context SparrowContext
   * @return UserObject
   */
  public JMSSessionWrapper getJMSSession() {
    JMSSessionWrapper s = (JMSSessionWrapper) offered();
    return s;
  }

  /**
   * offered
   *
   * @return Object
   */
  public Object offered() {
    Object usrObj = null;
    try {
      usrObj = pool.borrowObject();
    }
    catch (Exception ex) {
      throw new SparrowRuntimeException(
          "Exception occured while borrowing object from pool [" +
          ex.getMessage() + "]");
    }
    return usrObj;
  }

  /**
   * destroy
   */
  public void destroy() {
    pool.clear();
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
  private class JMSSessionPoolFactory
      extends BasePoolableObjectFactory {

    private final IObjectPoolLifeCycle lc;

    /**
     * makeObject
     *
     * @return Object
     */
    public JMSSessionPoolFactory(IObjectPoolLifeCycle lc) {
      this.lc = lc;
    }

    /**
     *
     * @return Object
     */
    public Object makeObject() {
      Session s = null;
      try {
        s = jmsConn.createSession(transacted, Session.AUTO_ACKNOWLEDGE);
        s = new JMSSessionWrapper(s, lc);

      }
      catch (JMSException ex) {
      }
      return s;
    }

    /**
     *
     * @param obj Object
     */
    public void destroyObject(Object obj) throws Exception {
      JMSSessionWrapper s = (JMSSessionWrapper) obj;
      s.closeSession();
      //pool.getNumActive()
      s = null;
    }

    /**
     *
     * @param obj Object
     */
    public void passivateObject(Object obj) {
      JMSSessionWrapper s = (JMSSessionWrapper) obj;
      s.setTrans(false);
      s.setName(null);
    }

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
  private class JMSSQueueTopicSessionPoolFactory
      extends JMSSessionPoolFactory {

    private final IObjectPoolLifeCycle lc;
    private final String jmsType;

    /**
     * makeObject
     *
     * @return Object
     */
    public JMSSQueueTopicSessionPoolFactory(IObjectPoolLifeCycle lc,
                                            String jmsType) {
      super(lc);
      this.lc = lc;
      this.jmsType = jmsType;
    }

    /**
     *
     * @return Object
     */
    public Object makeObject() {
      Session s = null;
      try {
        if (Constants.JMS_TYPE_TOPIC.equals(jmsType)) {
          s = topicConn.createTopicSession(transacted, msgAckType);
        }
        else if (Constants.JMS_TYPE_QUEUE.equals(jmsType)) {
          s = queueConn.createQueueSession(transacted, msgAckType);
          //System.out.println(s.getClass().getName() +"----->"+s.hashCode());          
        }
        else {
          s = jmsConn.createSession(transacted, msgAckType);
        }
        s = new JMSSessionWrapper(s, lc);
      }
      catch (JMSException ex) {
        logger.error("Exception occured while creating session", ex);
      }
      return s;
    }
  }

}
