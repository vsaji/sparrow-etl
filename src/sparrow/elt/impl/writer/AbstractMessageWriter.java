package sparrow.elt.impl.writer;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.StreamMessage;
import javax.jms.TextMessage;

import sparrow.elt.core.config.SparrowDataWriterConfig;
import sparrow.elt.core.exception.DataWriterException;
import sparrow.elt.core.exception.InitializationException;
import sparrow.elt.core.exception.ResourceException;
import sparrow.elt.core.log.SparrowLogger;
import sparrow.elt.core.log.SparrowrLoggerFactory;
import sparrow.elt.core.resource.Resource;
import sparrow.elt.core.util.ConfigKeyConstants;
import sparrow.elt.core.util.Constants;
import sparrow.elt.core.vo.DataOutputHolder;
import sparrow.elt.core.vo.MessageHolder;
import sparrow.elt.core.writer.AbstractDataWriter;


/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */
public abstract class AbstractMessageWriter
    extends AbstractDataWriter {

  protected final Resource RESOURCE;

  protected final String DESTINATION_NAME;
  protected final String RESOURCE_NAME;
  private final int TRANSACTION;
  private final String[] KEY_NAMES;

  private static final SparrowLogger logger = SparrowrLoggerFactory.
      getCurrentInstance(
      AbstractMessageWriter.class);

  /**
   *
   * @param config SparrowDataWriterConfig
   */
  protected AbstractMessageWriter(SparrowDataWriterConfig config, int transaction) {
    super(config);
    try {

      String destName = config.getInitParameter().getParameterValue(
          ConfigKeyConstants.PARAM_DEST_NAME);
      String[] res_dest = destName.split("[@]");

      this.RESOURCE_NAME = res_dest[0];
      this.DESTINATION_NAME = res_dest[1];
      this.TRANSACTION = transaction;

      if (KEY_NAME != null) {
        KEY_NAMES = (KEY_NAME.indexOf(",") != -1) ? KEY_NAME.split("[,]") :
            new String[] {
            KEY_NAME};
      }
      else {
        throw new InitializationException("param [key.name] is not specified");
      }

      if (RESOURCE_NAME == null) {
        throw new InitializationException("MW_RESOURCE_NAME_FOUND",
                                          "param [resource] is not specified.");
      }

      RESOURCE = config.getContext().getResource(RESOURCE_NAME);
    }
    catch (Exception ex) {
      String expCode = (ex instanceof JMSException) ? "MW_SESSION_EX" :
          ex.getMessage();
      throw ( (ex instanceof InitializationException) ?
             (InitializationException) ex :
             new InitializationException(expCode, ex));
    }
  }

  /**
   * writeData
   *
   * @param data DataOutputHolder
   */
  public int writeData(DataOutputHolder data, int statusCode) throws
      DataWriterException {

    //   long start = System.currentTimeMillis();
    boolean exception = false;
    Session session = null;
    try {
      session = getSession();
      //  System.out.println("getSession :"+(System.currentTimeMillis()-start));
      for (int i = 0; i < KEY_NAMES.length; i++) {
        MessageHolder messageHolder = data.getMessageHolder(KEY_NAMES[i]);
        sendMessage(session, messageHolder);
      }

    }
    catch (JMSException ex) {
      exception = true;
      throw new DataWriterException("Exception occured while sending message",
                                    ex);
    }
    catch (Exception ex) {
      exception = true;
      throw new DataWriterException("Exception occured while sending message",
                                    ex);
    }
    finally {
      if (session != null) {
//        start = System.currentTimeMillis();
        closeSession(session, exception);
//        System.out.println("closeSession:"+(System.currentTimeMillis()-start));

      }
    }
//     System.out.println("Insd AMW:"+(System.currentTimeMillis()-start));
    return STATUS_SUCCESS;
  }

  /**
   *
   * @param messageHolder MessageHolder
   * @param type int
   * @return boolean
   */
  public abstract boolean sendMessage(Session session,
                                      MessageHolder messageHolder) throws
      JMSException;

  /**
   *
   * @param session Session
   * @param exception boolean
   */
  protected void closeSession(Session session, boolean exception) {
    try {
      if (exception) {
        session.rollback();
      }
      else {
        session.commit();
      }
      session.close();
      session = null;
    }
    catch (JMSException ex1) {
      ex1.printStackTrace();
      logger.error("JMSException occured while commit/closing session ",
                   ex1);
    }

  }

  /**
   * getSession
   *
   * @param r Resource
   * @return Session
   */
  protected Session getSession() {
    try {
      return (Session) RESOURCE.getResource(TRANSACTION);
    }
    catch (ResourceException ex) {
      ex.printStackTrace();
      return null;
    }
  }

  /**
   *
   * @param message Message
   * @param properties Map
   */
  protected final void setProperties(Message message, Map properties) throws
      JMSException {
    if (properties != null && !properties.isEmpty()) {
      Set set = properties.keySet();
      String key = null;
      for (Iterator it = set.iterator(); it.hasNext(); ) {
        key = it.next().toString();
        message.setStringProperty(key, properties.get(key).toString());
      }
    }
  }

  /**
   *
   * @param message Object
   * @param messageType int
   * @return Message
   */
  public Message resolveMessageType(Object message, Session session,
                                    int messageType) throws JMSException {
    Message msg = null;
    switch (messageType) {
      case Constants.MESSAGE_TYPE_TEXT:
        msg = session.createTextMessage();
        ( (TextMessage) msg).setText(message.toString());
        break;
      case Constants.MESSAGE_TYPE_OBJECT:
        msg = session.createObjectMessage();
        ( (ObjectMessage) msg).setObject( (Serializable) message);
        break;
      case Constants.MESSAGE_TYPE_MAP:
        msg = session.createMapMessage();
        Map m = (Map) message;
        Iterator it = m.keySet().iterator();
        String key = null;
        for (; it.hasNext(); ) {
          key = it.next().toString();
          ( (MapMessage) msg).setObject(key, m.get(key));
        }
        break;
      case Constants.MESSAGE_TYPE_BYTE:
        msg = session.createBytesMessage();
        ( (BytesMessage) msg).writeObject(message);
        break;
      case Constants.MESSAGE_TYPE_STREAM:
        msg = session.createStreamMessage();
        ( (StreamMessage) msg).writeObject(message);
        break;
    }
    return msg;
  }

}
