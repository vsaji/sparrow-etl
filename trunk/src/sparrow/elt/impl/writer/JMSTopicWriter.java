package sparrow.elt.impl.writer;

import java.util.Iterator;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;

import sparrow.elt.core.config.SparrowDataWriterConfig;
import sparrow.elt.core.vo.MessageHolder;


/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */
public class JMSTopicWriter
    extends GenericMessageWriter {

  protected Topic topic = null;

  /**
   *
   * @param config SparrowDataWriterConfig
   * @param transaction int
   */
  public JMSTopicWriter(SparrowDataWriterConfig config, int transaction) {
    super(config, transaction);
  }

  /**
   *
   */
  public void initialize() {
    super.initialize();
    topic = (Topic) DESTINATION;
  }

  /**
   * sendMessage
   *
   * @param session Session
   * @param messageHolder MessageHolder
   * @return boolean
   */
  public boolean sendMessage(Session session, MessageHolder messageHolder) throws
      JMSException {
    TopicSession tSession = (TopicSession) session;

    for (Iterator it = messageHolder.getMessages().iterator(); it.hasNext(); ) {
      TopicPublisher publisher = tSession.createPublisher(topic);
      Message message = super.resolveMessageType(it.next(), session,
                                                 messageHolder.getMessageType());
      super.setProperties(message, messageHolder.getHeaderProperties());
      publisher.send(message);
    }
    return true;
  }
}
