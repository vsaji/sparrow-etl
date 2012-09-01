package sparrow.etl.core.vo;

import java.util.ArrayList;
import java.util.Date;
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
public class DBParamHolder {

  private HashMap paramAsKeyValue;
  private ArrayList paramAsList;

  public DBParamHolder() {
    paramAsKeyValue = new HashMap();
    paramAsList = new ArrayList();
  }

  public void addParam(String key, Object value) {
    paramAsKeyValue.put(key, value);
  }

  public void addParam(Object value) {
    if (value instanceof Date) {
      value = new java.sql.Timestamp( ( (Date) value).getTime());
    }
    paramAsList.add(value);
  }

  public void addParam(List value) {
    paramAsList.addAll(value);
  }

  public void addBatchParam(List value) {
    paramAsList.add(value);
  }


  public void addParam(Map value) {
    paramAsKeyValue.putAll(value);
  }

  public HashMap getParamMap() {
    return paramAsKeyValue;
  }

  public ArrayList getParamList() {
    return paramAsList;
  }
}
