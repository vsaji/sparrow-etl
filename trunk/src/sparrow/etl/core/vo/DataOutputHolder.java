package sparrow.etl.core.vo;

import java.util.HashMap;
import java.util.Map;

import sparrow.etl.core.dao.impl.ResultRow;


/**
 *
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */
public class DataOutputHolder {

  private HashMap dbParamMap = null;
  private HashMap messageMap = null;
  private HashMap objectMap = null;
  private HashMap stringMap = null;
  private ResultRow driverRow = null;
  private Map tokenValue = null;

  /**
   *
   */
  public DataOutputHolder() {
  }

  /**
   *
   * @param key String
   * @return Object
   */
  public Object getObject(String key) {
    return getObjectMap().get(key);
  }

  /**
   *
   * @param key String
   * @return Object
   */
  public String getString(String key) {
    return (String) getStringMap().get(key);
  }

  /**
   *
   * @return Map
   */
  public Map getTokenValue() {
    return tokenValue;
  }

  public ResultRow getDriverRow() {
    return driverRow;
  }

  /**
   *
   * @param key String
   * @return DBParamHolder
   */
  public DBParamHolder getDBParamHolder(String key) {
    return (DBParamHolder) getDBParam().get(key);
  }

  /**
   *
   * @param key String
   * @return boolean
   */
  public boolean isMessageHolderExist(String key) {
    return getMessages().containsKey(key);
  }

  /**
   *
   * @param key String
   * @return boolean
   */
  public boolean isObjectExist(String key) {
    return getObjectMap().containsKey(key);
  }

  /**
   *
   * @param key String
   * @return boolean
   */
  public boolean isStringExist(String key) {
    return getStringMap().containsKey(key);
  }

  /**
   *
   * @param key String
   * @return boolean
   */
  public boolean isDBParamHolderExist(String key) {
    return getDBParam().containsKey(key);
  }

  /**
   *
   * @param key String
   * @param dbParam DBParamHolder
   */
  public void addDBParamHolder(String key, DBParamHolder dbParam) {
    getDBParam().put(key, dbParam);
  }

  /**
   *
   * @param key String
   * @return MessageHolder
   */
  public MessageHolder getMessageHolder(String key) {
    return (MessageHolder) getMessages().get(key);
  }

  /**
   *
   * @param key String
   * @param message MessageHolder
   */
  public void addMessageHolder(String key, MessageHolder message) {
    getMessages().put(key, message);
  }

  /**
   *
   * @param key String
   * @param object Object
   */
  public void addObject(String key, Object object) {
    getObjectMap().put(key, object);
  }

  /**
   *
   * @param key String
   * @param object Object
   */
  public void addString(String key, String value) {
    getStringMap().put(key, value);
  }

  /**
   *
   * @param tokenValue Map
   */
  public void setTokenValue(Map tokenValue) {
    this.tokenValue = tokenValue;
  }

  public void setDriverRow(ResultRow driverRow) {
    this.driverRow = driverRow;
  }

  /**
   *
   * @return Map
   */
  private Map getDBParam() {
    return (dbParamMap != null) ? dbParamMap : (dbParamMap = new HashMap());
  }

  /**
   *
   * @return Map
   */
  private Map getMessages() {
    return (messageMap != null) ? messageMap : (messageMap = new HashMap());
  }

  /**
   *
   * @return Map
   */
  private Map getObjectMap() {
    return (objectMap != null) ? objectMap : (objectMap = new HashMap());
  }

  /**
   *
   * @return Map
   */
  private Map getStringMap() {
    return (stringMap != null) ? stringMap : (stringMap = new HashMap());
  }

  /**
   *
   */
  public void destroy() {
    if (dbParamMap != null) {
      dbParamMap.clear();
      dbParamMap = null;
    }
    if (messageMap != null) {
      messageMap.clear();
      messageMap = null;
    }
    if (objectMap != null) {
      objectMap.clear();
      objectMap = null;
    }
    if (stringMap != null) {
      stringMap.clear();
      stringMap = null;
    }
    if (driverRow != null) {
      driverRow.destroy();
      driverRow = null;
    }
    if (tokenValue != null) {
      tokenValue.clear();
      tokenValue = null;
    }
  }
}
