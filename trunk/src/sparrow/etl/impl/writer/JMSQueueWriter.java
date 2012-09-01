package sparrow.etl.impl.writer;

import java.util.Iterator;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;

import sparrow.etl.core.config.SparrowDataWriterConfig;
import sparrow.etl.core.resource.JMSResourceInitializer;
import sparrow.etl.core.vo.MessageHolder;


/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */
public class JMSQueueWriter
    extends GenericMessageWriter {

  protected Queue queue = null;

  /**
   *
   * @param config SparrowDataWriterConfig
   * @param transaction int
   */
  public JMSQueueWriter(SparrowDataWriterConfig config, int transaction) {
    super(config, transaction);
  }

  /**
   *
   */
  public void initialize() {
    super.initialize();
    queue = (Queue) DESTINATION;
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
    QueueSession qSession = (QueueSession) session;

    for (Iterator it = messageHolder.getMessages().iterator(); it.hasNext(); ) {
      QueueSender sender = qSession.createSender(queue);
      Message message = super.resolveMessageType(it.next(), session,
                                                 messageHolder.getMessageType());
      super.setProperties(message, messageHolder.getHeaderProperties());
      sender.send(message);
    }
    return true;
  }
}
