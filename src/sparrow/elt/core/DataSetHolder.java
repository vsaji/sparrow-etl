package sparrow.elt.core;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import sparrow.elt.core.dao.impl.RecordSet;
import sparrow.elt.core.dao.impl.ResultRow;
import sparrow.elt.core.exception.DataException;
import sparrow.elt.core.lookup.LookupManager;
import sparrow.elt.core.util.CaseInSensitiveMap;
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
public class DataSetHolder
    implements DataSet {

  private ResultRow driverRow = null;
  private Map tokenKeyValue = null;
  private LookupManager lkupMan;

  /**
   *
   */
  DataSetHolder() {
  }

  /**
   *
   * @throws DataException
   * @return Map
   */
  public Map getLookupResults() throws DataException {
    return lkupMan.getLookupResults();
  }

  /**
   *
   * @param driverRow ResultRow
   */
  void setDriverRow(ResultRow driverRow) {
    this.driverRow = driverRow;
  }

  /**
   *
   * @return ResultRow
   */
  public ResultRow getDriverRow() {
    return driverRow;
  }

  /**
   *
   * @return Map
   */
  public Map getDataSetAsKeyValue() {
    return (tokenKeyValue != null) ? tokenKeyValue :
        (tokenKeyValue = new CaseInSensitiveMap());
//    return (tokenKeyValue!=null) ? tokenKeyValue : (tokenKeyValue=new HashMap());
//    return (tokenKeyValue != null) ? tokenKeyValue :(tokenKeyValue = new CaseInSensitiveMap());

  }

  /**
   *
   * @param subQueryName String
   * @return RecordSet
   */
  public RecordSet getLookupResult(String lookupName) throws DataException {
    return lkupMan.getLookupResult(lookupName);
  }

  /**
   *
   */
  public void destroy() {
    if (lkupMan != null) {

      /**      Map temp = null;
            if ( (temp = lkupMan.getLookupResults()) != null) {
              Set keySet = temp.keySet();
              for (Iterator it = keySet.iterator(); it.hasNext(); ) {
                try {
                  String key = (String) it.next();
                  RecordSet rs = (RecordSet) temp.get(key);
                  rs.close();
                }
                catch (DataException ex) {
                }
              }
            }
       **/
      lkupMan.clear();
      lkupMan = null;
    }

    tokenKeyValue = null;
  }

  /**
   *
   * @param lkupMan LookupManager
   */
  public void setLookupManager(LookupManager lkupMan) {
    this.lkupMan = lkupMan;
  }

  /**
   * getLookupResult
   *
   * @param lookupName String
   * @param customToken Map
   * @return RecordSet
   */
  public RecordSet getLookupResult(String lookupName, Map customToken) throws
      DataException {
    return lkupMan.getLookupResult(lookupName, customToken);
  }

  /**
   *
   * @param dataSet DataSet
   * @return String
   */
  public String getDataSetAsXML() {
    HashMap hm = new HashMap();

    Map mp = getDataSetAsKeyValue();
    Set key = mp.keySet();

    for (Iterator it = key.iterator(); it.hasNext(); ) {
      String ky = it.next().toString();
      String[] splt = ky.split("[$]");
      String group = splt[0];
      String columnName = splt[1];

      StringBuffer sb = null;
      if (hm.containsKey(group)) {
        sb = (StringBuffer) hm.get(group);
      }
      else {
        sb = new StringBuffer();
        sb.append("<" + group + ">");
        hm.put(group, sb);
      }
      sb.append("<" + columnName +
          ">").append(SparrowUtil.escapeXML((mp.get(ky)!=null)?mp.get(ky).toString():null)).append("</" +
          columnName + ">");
    }

    StringBuffer rtnString = new StringBuffer("<dataset>");
    key = hm.keySet();
    for (Iterator it = key.iterator(); it.hasNext(); ) {
      String ky = it.next().toString();
      StringBuffer sb = (StringBuffer) hm.get(ky);
      sb.append("</" + ky + ">");
      rtnString.append(sb.toString());
    }
    rtnString.append("</dataset>");
    return rtnString.toString();
  }

}
