package sparrow.elt.impl.extractor.jms;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

/**
 *
 * @author vsaji
 *
 */
public class SparrowJMSMessage
    implements Serializable {

  private String message;
  private String messageId;
  private String internalMessageId;
  private boolean loadedFromStore = false;
  private Map headerProps = null;
  private int retryCount = 0;

  private static final long serialVersionUID = 7526472295622776147L;

  /**
   *
   *
   * @param message String
   * @param messageId String
   */
  public SparrowJMSMessage(String message, String messageId, String internalMessageId) {
    this.message = message;
    this.messageId = messageId;
    this.internalMessageId = internalMessageId;
  }

  /**
   *
   * @param message
   * @param messageId
   * @param fileName
   */
  public SparrowJMSMessage(Message msg, String[] props) {
    try {
      String tmsg = ( (TextMessage) msg).getText();
      this.message = tmsg;
      this.messageId = msg.getJMSMessageID();

      if (props != null) {
        headerProps = new HashMap();
        for (int i = 0; i < props.length; i++) {
          headerProps.put(props[i], msg.getStringProperty(props[i]));
        }
      }

    }
    catch (JMSException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /**
   *
   * @param message
   * @param messageId
   * @param fileName
   * @param loadedFromStore
   */
  public SparrowJMSMessage(String message, String messageId, String internalMessageId,
                         boolean loadedFromStore) {
    this.message = message;
    this.messageId = messageId;
    this.internalMessageId = internalMessageId;
    this.loadedFromStore = loadedFromStore;
  }

  /**
   *
   * @return String
   */
  public String getMessageId() {
    return messageId;
  }

  /**
   *
   * @return String
   */
  public String getMessage() {
    return message;
  }

  /**
   *
   * @return int
   */
  public int incrementRetryCount() {
    return retryCount++;
  }

  /**
   *
   * @return int
   */
  public int getRetryCount() {
        return retryCount;
  }

  /**
   *
   * @return boolean
   */
  public boolean isLoadedFromStore() {
    return loadedFromStore;
  }

  /**
   *
   * @return String
   */
  public String getInternalMessageId() {
    return internalMessageId;
  }

  /**
   *
   * @param loadedFromStore boolean
   */
  public void setLoadedFromStore(boolean loadedFromStore) {
    this.loadedFromStore = loadedFromStore;
  }


  /**
   *
   * @param internalMessageId String
   */
  public void setInternalMessageId(String internalMessageId) {
    this.internalMessageId = internalMessageId;
  }

  /**
   *
   * @return
   */
  public Map getHeaderProperties() {
    return headerProps;
  }

  /**
   *
   * @param key
   * @return
   */
  public String getHeaderProperty(String key) {
    return (headerProps != null) ? (String) headerProps.get(key) : null;
  }


  /**
   *
   * @return String
   */
  public String toString(){
    StringBuffer sb = new StringBuffer();
    sb.append("[messageId:").append(message).append("\n");
    sb.append("internalMessageId:").append(internalMessageId).append("\n");
    sb.append("headerProps:").append(headerProps).append("\n");
    sb.append("loadedFromStore:").append(loadedFromStore).append("\n");
    sb.append("message:").append(message).append("]");
    return sb.toString();
  }
}
