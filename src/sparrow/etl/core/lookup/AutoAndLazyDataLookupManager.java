package sparrow.etl.core.lookup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import sparrow.etl.core.DataSetHolder;
import sparrow.etl.core.config.DataLookUpConfig;
import sparrow.etl.core.config.LookUpConfig;
import sparrow.etl.core.context.SparrowContext;
import sparrow.etl.core.dao.impl.RecordSet;
import sparrow.etl.core.dao.impl.ResultRow;
import sparrow.etl.core.exception.DataException;
import sparrow.etl.core.transformer.DriverRowEventListener;
import sparrow.etl.core.util.Constants;


/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 */
public class AutoAndLazyDataLookupManager
    extends DataLookupManager {

  protected Map lookupTypeMap = new HashMap();
  protected List autoLookupIndex = new ArrayList();
  protected LazyAndAutoLookupManager lalm;

  /**
   *
   * @param context SparrowContext
   */
  public AutoAndLazyDataLookupManager(SparrowContext context) {
    super(context);
  }

  /**
   *
   */
  public void initialize() {

    DataLookUpConfig config = contxt.getConfiguration().getDataLookUp();

    super.resultStatus = new HashMap();

    List lookups = config.getLookups();
    this.lookupObjects = new HashMap(contxt.getConfiguration().getDataLookUp().
                                     getLookups().size());
    this.autoLookupIndex = LookupUtils.sequenceDependentForAutoLookup(lookups);
    this.lookupMap = LookupUtils.getItemsInMap(lookups);

    super.setAutoLookupIndex(autoLookupIndex);
    super.initializeLookUps(LookupUtils.sequenceDependent(lookups), lookupMap);

    this.populateLookupTypeMap(lookupMap);
  }

  /**
   *
   * @param lookups Map
   */
  private void populateLookupTypeMap(Map lookups) {
    Set keys = lookups.keySet();
    for (Iterator it = keys.iterator(); it.hasNext(); ) {
      String key = it.next().toString();
      LookUpConfig lkp = (LookUpConfig) lookups.get(key);
      lookupTypeMap.put(lkp.getName(), lkp.getLoadType());
    }
  }

  /**
   *
   * @param rsltHolder DataSetHolder
   * @param eventListener DriverRowEventListener
   * @throws DataException
   */
  public void loadLookupResult(DataSetHolder rsltHolder,
                               DriverRowEventListener eventListener) throws
      DataException {

    super.loadLookupResult(rsltHolder, eventListener);
    lalm.setDriverRowEventListener(eventListener);
    lalm.setDriverRow(rsltHolder.getDriverRow());

  }

  /**
   *
   * @return AutoLookupManager
   */
  protected AutoLookupManager getLookupManager() {
    return ((lalm==null) ? (lalm = new LazyAndAutoLookupManager()) : lalm);
  }

  /**
   *
   * <p>Title: </p>
   * <p>Description: </p>
   * <p>Copyright: Copyright (c) 2004</p>
   * <p>Company: </p>
   * @author not attributable
   * @version 1.0
   */
  protected class LazyAndAutoLookupManager
      extends AutoLookupManager {

    DriverRowEventListener eventListener = null;
    ResultRow driverRow = null;

    /**
     *
     */
    LazyAndAutoLookupManager() {
      super();
    }

    /**
     * clear
     */
    public void clear() {
      super.clear();
      eventListener = null;
      driverRow = null;
    }

    /**
     * getLookupResult
     *
     * @param name String
     * @return RecordSet
     */
    public RecordSet getLookupResult(String name) throws
        DataException {
      return getLookupResult(name, null);
    }

    /**
     * getLookupResult
     *
     * @param name String
     * @param customToken Map
     * @return RecordSet
     */
    public RecordSet getLookupResult(String name, Map customToken) throws
        DataException {
      String type = lookupTypeMap.get(name).toString();
      if (Constants.LOAD_TYPE_AUTO.equals(type)) {
        return (RecordSet) lookupResults.get(name);
      }
      else {
        if (customToken != null) {
          resultKeyValue.putAll(customToken);
        }
        LookupObject item = (LookupObject) lookupObjects.get(name);
        RecordSet rs = item.getLookupData(driverRow,
                                          resultKeyValue, eventListener);
        lookupResults.put(name, rs);
        resultStatus.put(name,
                         (rs.getRowCount() == 0) ? Constants.NO_RECORD_EXIST :
                         Constants.RECORD_EXIST);
        if (rs.getRowCount() != 0) {
          addResultToGlobalLKUPMap(name, rs, eventListener, resultKeyValue);
        }
        return rs;
      }
    }

    /**
     *
     * @param eventListener DriverRowEventListener
     */
    public void setDriverRowEventListener(DriverRowEventListener
                                          eventListener) {
      this.eventListener = eventListener;
    }

    /**
     *
     * @param row ResultRow
     */
    public void setDriverRow(ResultRow row) {
      this.driverRow = row;
    }
  }

}
