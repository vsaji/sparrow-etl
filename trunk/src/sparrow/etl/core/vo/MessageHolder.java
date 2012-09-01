package sparrow.etl.core.vo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */
public class MessageHolder {

  private List messages;
  private int messageType;
  private Map headerProperties = null;
  public void setMessageType(int messageType) {
    this.messageType = messageType;
  }


  /**
   *
   * @param message Object
   * @param headerKey String
   * @param headerValue String
   */
  public MessageHolder(final Object message, final Map headerProps) {
    messages = new ArrayList() {
      {
        add(message);
      }
    };

    if (headerProps != null && !headerProps.isEmpty()) {
      headerProperties = new HashMap(headerProps);
    }

  }

  public MessageHolder() {
    messages = new ArrayList();
  }

  public void addMessage(Object message) {
    messages.add(message);
  }

  public List getMessages() {
    return messages;
  }

  public void setMessage(List messages) {
    this.messages = messages;
  }

  public void addHeaderProperty(String key, String value) {
    getPropertyStore().put(key, value);
  }

  public void setHeaderProperties(Map headerProperties){
    this.headerProperties = headerProperties;
  }

  public String getHeaderProperty(String key) {
    return getPropertyStore().get(key).toString();
  }

  public Map getHeaderProperties() {
    return headerProperties;
  }

  public int getMessageType() {
    return messageType;
  }

  private Map getPropertyStore() {
    return (headerProperties != null) ? headerProperties :
        (headerProperties = new HashMap());
  }
}
