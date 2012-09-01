package sparrow.etl.core.resource;

import java.io.Serializable;
import javax.jms.BytesMessage;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.Session;
import javax.jms.StreamMessage;
import javax.jms.TemporaryQueue;
import javax.jms.TemporaryTopic;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicSubscriber;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

import sparrow.etl.core.transaction.XAResourceWrapper;


public class JMSXASession
    implements XAResourceWrapper, Session {

  private Session s = null;
  private String name = null;
  private boolean inTrans = false;

  public JMSXASession(String name, Session s) {
    this.s = s;
    this.name = name;
    this.inTrans = true;
  }

  /**
   * doClose
   */
  public void doClose() {
  }

  /**
   * doCommit
   */
  public void doCommit() {
    try {
      s.commit();
    }
    catch (JMSException ex) {
    }

  }

  /**
   * doRollback
   */
  public void doRollback() {
    try {
      s.rollback();
    }
    catch (JMSException ex) {
    }
  }

  /**
   * getName
   *
   * @return String
   */
  public String getName() {
    return name;
  }

  /**
   * isCloseFlagged
   *
   * @return boolean
   */
  public boolean isCloseFlagged() {
    return false;
  }

  /**
   * commit
   *
   * @param xid Xid
   * @param boolean1 boolean
   */
  public void commit(Xid xid, boolean boolean1) {
    doCommit();
  }

  /**
   * end
   *
   * @param xid Xid
   * @param int1 int
   */
  public void end(Xid xid, int int1) {
  }

  /**
   * forget
   *
   * @param xid Xid
   */
  public void forget(Xid xid) {
  }

  /**
   * getTransactionTimeout
   *
   * @return int
   */
  public int getTransactionTimeout() {
    return 0;
  }

  /**
   * isSameRM
   *
   * @param xAResource XAResource
   * @return boolean
   */
  public boolean isSameRM(XAResource xAResource) {
    return xAResource.equals(this);
  }

  /**
   * prepare
   *
   * @param xid Xid
   * @return int
   */
  public int prepare(Xid xid) {
    return 0;
  }

  /**
   * recover
   *
   * @param int0 int
   * @return Xid[]
   */
  public Xid[] recover(int int0) {
    return null;
  }

  /**
   * rollback
   *
   * @param xid Xid
   */
  public void rollback(Xid xid) {
    doRollback();
  }

  /**
   * setTransactionTimeout
   *
   * @param int0 int
   * @return boolean
   */
  public boolean setTransactionTimeout(int int0) {
    return false;
  }

  /**
   * start
   *
   * @param xid Xid
   * @param int1 int
   */
  public void start(Xid xid, int int1) {
  }

  /**
   * createBytesMessage
   *
   * @return BytesMessage
   */
  public BytesMessage createBytesMessage() throws JMSException {
    return s.createBytesMessage();
  }

  /**
   * createMapMessage
   *
   * @return MapMessage
   */
  public MapMessage createMapMessage() throws JMSException {
    return s.createMapMessage();
  }

  /**
   * createMessage
   *
   * @return Message
   */
  public Message createMessage() throws JMSException {
    return s.createMessage();
  }

  /**
   * createObjectMessage
   *
   * @return ObjectMessage
   */
  public ObjectMessage createObjectMessage() throws JMSException {
    return s.createObjectMessage();
  }

  /**
   * createObjectMessage
   *
   * @param serializable Serializable
   * @return ObjectMessage
   */
  public ObjectMessage createObjectMessage(Serializable serializable) throws
      JMSException {
    return s.createObjectMessage(serializable);
  }

  /**
   * createStreamMessage
   *
   * @return StreamMessage
   */
  public StreamMessage createStreamMessage() throws JMSException {
    return s.createStreamMessage();
  }

  /**
   * createTextMessage
   *
   * @return TextMessage
   */
  public TextMessage createTextMessage() throws JMSException {
    return s.createTextMessage();
  }

  /**
   * createTextMessage
   *
   * @param string String
   * @return TextMessage
   */
  public TextMessage createTextMessage(String string) throws JMSException {
    return s.createTextMessage(string);
  }

  /**
   * getTransacted
   *
   * @return boolean
   */
  public boolean getTransacted() throws JMSException {
    return s.getTransacted();
  }

  /**
   * getAcknowledgeMode
   *
   * @return int
   */
  public int getAcknowledgeMode() throws JMSException {
    return s.getAcknowledgeMode();
  }

  /**
   * commit
   */
  public void commit() {
    if (!inTrans) {
      doCommit();
    }
  }

  /**
   * rollback
   */
  public void rollback() {
    if (!inTrans) {
      doRollback();
    }
  }

  /**
   * close
   */
  public void close() {
  }

  /**
   * recover
   */
  public void recover() {
  }

  /**
   * getMessageListener
   *
   * @return MessageListener
   */
  public MessageListener getMessageListener() throws JMSException {
    return s.getMessageListener();
  }

  /**
   * setMessageListener
   *
   * @param messageListener MessageListener
   */
  public void setMessageListener(MessageListener messageListener) throws
      JMSException {
    s.setMessageListener(messageListener);
  }

  /**
   * run
   */
  public void run() {
    s.run();
  }

  /**
   * createProducer
   *
   * @param destination Destination
   * @return MessageProducer
   */
  public MessageProducer createProducer(Destination destination) throws
      JMSException {
    return s.createProducer(destination);
  }

  /**
   * createConsumer
   *
   * @param destination Destination
   * @return MessageConsumer
   */
  public MessageConsumer createConsumer(Destination destination) throws
      JMSException {
    return s.createConsumer(destination);
  }

  /**
   * createConsumer
   *
   * @param destination Destination
   * @param string String
   * @return MessageConsumer
   */
  public MessageConsumer createConsumer(Destination destination, String string) throws
      JMSException {
    return s.createConsumer(destination, string);
  }

  /**
   * createConsumer
   *
   * @param destination Destination
   * @param string String
   * @param boolean2 boolean
   * @return MessageConsumer
   */
  public MessageConsumer createConsumer(Destination destination, String string,
                                        boolean boolean2) throws JMSException {
    return s.createConsumer(destination, string, boolean2);
  }

  /**
   * createQueue
   *
   * @param string String
   * @return Queue
   */
  public Queue createQueue(String string) throws JMSException {
    return s.createQueue(string);
  }

  /**
   * createTopic
   *
   * @param string String
   * @return Topic
   */
  public Topic createTopic(String string) throws JMSException {
    return s.createTopic(string);
  }

  /**
   * createDurableSubscriber
   *
   * @param topic Topic
   * @param string String
   * @return TopicSubscriber
   */
  public TopicSubscriber createDurableSubscriber(Topic topic, String string) throws
      JMSException {
    return s.createDurableSubscriber(topic, string);
  }

  /**
   * createDurableSubscriber
   *
   * @param topic Topic
   * @param string String
   * @param string2 String
   * @param boolean3 boolean
   * @return TopicSubscriber
   */
  public TopicSubscriber createDurableSubscriber(Topic topic, String string,
                                                 String string2,
                                                 boolean boolean3) throws
      JMSException {
    return s.createDurableSubscriber(topic, string, string2, boolean3);
  }

  /**
   * createBrowser
   *
   * @param queue Queue
   * @return QueueBrowser
   */
  public QueueBrowser createBrowser(Queue queue) throws JMSException {
    return s.createBrowser(queue);
  }

  /**
   * createBrowser
   *
   * @param queue Queue
   * @param string String
   * @return QueueBrowser
   */
  public QueueBrowser createBrowser(Queue queue, String string) throws
      JMSException {
    return s.createBrowser(queue, string);
  }

  /**
   * createTemporaryQueue
   *
   * @return TemporaryQueue
   */
  public TemporaryQueue createTemporaryQueue() throws JMSException {
    return s.createTemporaryQueue();
  }

  /**
   * createTemporaryTopic
   *
   * @return TemporaryTopic
   */
  public TemporaryTopic createTemporaryTopic() throws JMSException {
    return s.createTemporaryTopic();
  }

  /**
   * unsubscribe
   *
   * @param string String
   */
  public void unsubscribe(String string) throws JMSException {
    s.unsubscribe(string);
  }
}
