package sparrow.elt.impl.writer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.jms.BytesMessage;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.StreamMessage;
import javax.jms.TextMessage;

import sparrow.elt.core.config.ConfigParam;
import sparrow.elt.core.config.SparrowDataWriterConfig;
import sparrow.elt.core.exception.DataWriterException;
import sparrow.elt.core.exception.InitializationException;
import sparrow.elt.core.exception.ResourceException;
import sparrow.elt.core.log.SparrowLogger;
import sparrow.elt.core.log.SparrowrLoggerFactory;
import sparrow.elt.core.resource.JMSResourceInitializer;
import sparrow.elt.core.resource.Resource;
import sparrow.elt.core.resource.JMSResourceInitializer.JMSResource;
import sparrow.elt.core.util.ConfigKeyConstants;
import sparrow.elt.core.util.Constants;
import sparrow.elt.core.util.SparrowUtil;
import sparrow.elt.core.vo.DataOutputHolder;
import sparrow.elt.core.vo.MessageHolder;
import sparrow.elt.core.writer.AbstractDataWriter;


/**
 *
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * <p>
 * Company:
 * </p>
 *
 * @author Saji Venugopalan
 * @version 1.0
 */
public class GenericMessageWriter
    extends AbstractDataWriter {

  protected static final String CUSTOM_KEY = "custom";

  protected final Resource RESOURCE;

  protected final String DESTINATION_NAME;

  protected final String RESOURCE_NAME;

  protected final Destination DESTINATION;

  protected final DefaultMessageHandler dmh;

  private final int TRANSACTION;

  private String[] KEY_NAMES;

  private int KEY_NAMES_LEN;

  private int[] MSG_TYPE;

  private HashMap MSG_HEADER_PROPERTY;

  protected final boolean printMessage;

  private static final SparrowLogger logger = SparrowrLoggerFactory
      .getCurrentInstance(GenericMessageWriter.class);

  /**
   *
   * @param config
   *            SparrowDataWriterConfig
   */
  protected GenericMessageWriter(SparrowDataWriterConfig config, int transaction) {
    super(config);
    try {

      String destName = config.getInitParameter().getParameterValue(
          ConfigKeyConstants.PARAM_DEST_NAME);

      String[] res_dest = destName.split("[@]");

      this.RESOURCE_NAME = res_dest[0];
      this.DESTINATION_NAME = res_dest[1];
      this.TRANSACTION = transaction;

      dmh = getMessageHandler();
      dmh.init();

      if (RESOURCE_NAME == null) {
        throw new InitializationException("MW_RESOURCE_NAME_FOUND",
                                          "param [resource] is not specified.");
      }

      printMessage = SparrowUtil.performTernary(config.getInitParameter(),
                                              "print.message", false);

      RESOURCE = config.getContext().getResource(RESOURCE_NAME);
      DESTINATION = ( (JMSResource) RESOURCE)
          .getDestination(DESTINATION_NAME);
    }
    catch (Exception ex) {
      String expCode = (ex instanceof JMSException) ? "MW_SESSION_EX"
          : ex.getMessage();
      throw ( (ex instanceof InitializationException) ?
             (InitializationException) ex
             : new InitializationException(expCode, ex));
    }
  }

  /**
   *
   * @return DefaultMessageHandler
   */
  private DefaultMessageHandler getMessageHandler() {
    DefaultMessageHandler dmh;
    String message = config.getInitParameter().getParameterValue(
        "message.1");

    if (message != null) {
      dmh = new TemplateMessageHandler();
    }
    else if (KEY_NAME != null) {
      dmh = (KEY_NAME.split("[,]").length > 1) ? new DefaultMessageHandler()
          : new ConditionalMessageHandler();
    }
    else {
      throw new InitializationException(
          "Param [key.name] or [message] must be specified.");
    }
    return dmh;
  }

  /**
   *
   */
  private void initMessageProps() {
    KEY_NAMES = (KEY_NAME.indexOf(",") != -1) ? KEY_NAME.split("[,]")
        : new String[] {
        KEY_NAME};

    KEY_NAMES_LEN = KEY_NAMES.length;

    MSG_HEADER_PROPERTY = new HashMap(KEY_NAMES.length);
    readMsgHeaderProperty();

    MSG_TYPE = new int[KEY_NAMES.length];
    readMsgType();
  }

  /**
   *
   */
  private void readMsgHeaderProperty() {
    ConfigParam cp = config.getInitParameter();
    for (int j = 0; j < KEY_NAMES_LEN; j++) {
      MSG_HEADER_PROPERTY.put(KEY_NAMES[j], readProperties(KEY_NAMES[j]
          + ".msg.property", cp));
    }
  }

  /**
   *
   * @param propPattern
   *            String
   * @param cp
   *            ConfigParam
   * @return Map
   */
  private Map readProperties(String propPattern, ConfigParam cp) {

    HashMap props = new HashMap();
    String properties = null;
    for (int i = 1; (properties = cp.getParameterValue(propPattern + "."
        + i)) != null; i++) {
      String[] keyVal = properties.split("[=]");
      props.put(keyVal[0], keyVal[1]);
    }
    return props;

  }

  /**
   *
   */
  private void readMsgType() {
    String type = null;
    ConfigParam cp = config.getInitParameter();
    for (int j = 0; j < KEY_NAMES_LEN; j++) {
      type = cp.getParameterValue(KEY_NAMES[j] + ".msg.type");
      MSG_TYPE[j] = (type == null) ? Constants.MESSAGE_TYPE_TEXT
          : Integer.parseInt(type);
    }
  }

  /**
   * writeData
   *
   * @param data
   *            DataOutputHolder
   */
  public int writeData(DataOutputHolder data, int statusCode) throws
      DataWriterException {

    if (KEY_NAME != null && data.getMessageHolder(KEY_NAME) == null) {
      logger.debug("Skipping writer [" + WRITER_NAME
                   + "] - MessageHolder is null for key.name [" + KEY_NAME
                   + "]");
      return STATUS_SUCCESS;
    }

    return dmh.writeData(data, statusCode);
  }

  /**
   *
   * @param messageHolder
   *            MessageHolder
   * @param type
   *            int
   * @return boolean
   */
  protected void sendMessage(Session session, MessageHolder messageHolder,
                             String keyName, int msgType) throws JMSException {

    Map headerProp = (messageHolder.getHeaderProperties() == null) ?
        (HashMap) MSG_HEADER_PROPERTY
        .get(keyName)
        : messageHolder.getHeaderProperties();

    MessageProducer sender = session.createProducer(DESTINATION);
    for (Iterator it = messageHolder.getMessages().iterator(); it.hasNext(); ) {
      Object msg = it.next();
      Message message = resolveMessageType(msg, session, msgType);
      setProperties(message, headerProp);
      sender.send(message);

      if (logger.isDebugEnabled()) {
        logger.debug("[" + WRITER_NAME + "]: Message prepared for sending.[" +
                     ( (msg != null && printMessage) ? msg.toString() :
                      "Message might be null or PARAM[print.message] is not enabled") +
                     "]");
      }

    }
    sender.close();
  }

  /**
   *
   * @param session
   *            Session
   * @param exception
   *            boolean
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
   * @param r
   *            Resource
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
   * @param message
   *            Message
   * @param properties
   *            Map
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
   * @param message
   *            Object
   * @param messageType
   *            int
   * @return Message
   */
  protected Message resolveMessageType(Object message, Session session,
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

  /**
   *
   * <p>
   * Title:
   * </p>
   * <p>
   * Description:
   * </p>
   * <p>
   * Copyright: Copyright (c) 2004
   * </p>
   * <p>
   * Company:
   * </p>
   *
   * @author not attributable
   * @version 1.0
   */
  private class DefaultMessageHandler {

    void init() {
      initMessageProps();
    }

    /**
     *
     * @param data
     *            DataOutputHolder
     * @param statusCode
     *            int
     * @throws DataWriterException
     * @return int
     */
    int writeData(DataOutputHolder data, int statusCode) throws
        DataWriterException {
      // long start = System.currentTimeMillis();
      boolean exception = false;
      Session session = null;
      try {
        session = getSession();
        // System.out.println("getSession
        // :"+(System.currentTimeMillis()-start));
        //System.out.println("********>" + session.hashCode());
        for (int i = 0; i < KEY_NAMES_LEN; i++) {
          MessageHolder messageHolder = data
              .getMessageHolder(KEY_NAMES[i]);
          int msgType = (messageHolder.getMessageType() == 0) ? MSG_TYPE[i]
              : messageHolder.getMessageType();
          sendMessage(session, messageHolder, KEY_NAMES[i], msgType);
        }

      }
      catch (JMSException ex) {
        exception = true;
        throw new DataWriterException(
            "Exception occured while sending message", ex);
      }
      catch (Exception ex) {
        exception = true;
        throw new DataWriterException(
            "Exception occured while sending message", ex);
      }
      finally {
        if (session != null) {
          // start = System.currentTimeMillis();
          closeSession(session, exception);
          // System.out.println("closeSession:"+(System.currentTimeMillis()-start));

        }
      }
      // System.out.println("Insd
      // AMW:"+(System.currentTimeMillis()-start));
      return STATUS_SUCCESS;
    }
  }

  /**
   *
   * <p>
   * Title:
   * </p>
   * <p>
   * Description:
   * </p>
   * <p>
   * Copyright: Copyright (c) 2004
   * </p>
   * <p>
   * Company:
   * </p>
   *
   * @author not attributable
   * @version 1.0
   */
  private class TemplateMessageHandler
      extends DefaultMessageHandler {

    MessageHolder messageHolder = null;

    String[] messages;

    /**
     *
     */
    void init() {
      KEY_NAMES = new String[] {
          CUSTOM_KEY};
      KEY_NAMES_LEN = 1;
      ConfigParam cp = config.getInitParameter();
      messageHolder = new MessageHolder();
      messageHolder
          .setHeaderProperties(readProperties("msg.property", cp));
      messageHolder.setMessageType(Constants.MESSAGE_TYPE_TEXT);
      ArrayList al = new ArrayList();
      String msg;
      for (int i = 1; (msg = cp.getParameterValue("message." + i)) != null; i++) {
        al.add(msg);
      }
      messages = (String[]) al.toArray(new String[al.size()]);
      al.clear();
      al = null;
    }

    /**
     *
     * @param data
     *            DataOutputHolder
     * @param statusCode
     *            int
     * @throws DataWriterException
     * @return int
     */
    int writeData(DataOutputHolder data, int statusCode) throws
        DataWriterException {
      MessageHolder msgHolder = new MessageHolder();
      msgHolder.setHeaderProperties(messageHolder.getHeaderProperties());
      msgHolder.setMessageType(messageHolder.getMessageType());
      for (int i = 0; i < messages.length; i++) {
        // messages[i] = SparrowUtil.replace(messages[i], "${", "@{");
        // msgHolder.addMessage(TemplateInterpreter.eval(messages[i],
        // data.getTokenValue()));
        msgHolder.addMessage(SparrowUtil.replaceTokens(messages[i], data
            .getTokenValue()));
      }
      data.addMessageHolder(CUSTOM_KEY, msgHolder);
      return super.writeData(data, statusCode);
    }

  }

  /**
   *
   * <p>
   * Title:
   * </p>
   * <p>
   * Description:
   * </p>
   * <p>
   * Copyright: Copyright (c) 2004
   * </p>
   * <p>
   * Company:
   * </p>
   *
   * @author not attributable
   * @version 1.0
   */
  private class ConditionalMessageHandler
      extends DefaultMessageHandler {

    int writeData(DataOutputHolder data, int statusCode) throws
        DataWriterException {

      if (data.getMessageHolder(KEY_NAME) == null) {
        logger.debug("Skipping writer [" + WRITER_NAME
                     + "] - MessageHolder is null for key.name [" + KEY_NAME
                     + "]");

        return STATUS_SUCCESS;
      }
      else {
        return super.writeData(data, statusCode);
      }
    }
  }

}
