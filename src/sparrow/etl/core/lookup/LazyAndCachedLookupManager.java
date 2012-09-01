package sparrow.etl.core.lookup;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import sparrow.etl.core.DataSetHolder;
import sparrow.etl.core.config.DataLookUpConfig;
import sparrow.etl.core.context.SparrowContext;
import sparrow.etl.core.dao.impl.RecordSet;
import sparrow.etl.core.dao.impl.RecordSetImpl_Disconnected;
import sparrow.etl.core.dao.impl.ResultRow;
import sparrow.etl.core.exception.DataException;
import sparrow.etl.core.log.SparrowLogger;
import sparrow.etl.core.log.SparrowrLoggerFactory;
import sparrow.etl.core.transformer.DriverRowEventListener;
import sparrow.etl.core.util.Constants;
import sparrow.etl.core.util.DependentSequenzer;
import sparrow.etl.core.util.SparrowUtil;


/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public class LazyAndCachedLookupManager
    extends DataLookupManager {

  private Map lookupStack = null;
  private LazyAndCacheLookupManager llm;

  private static final SparrowLogger logger = SparrowrLoggerFactory.
      getCurrentInstance(
      LazyAndCachedLookupManager.class);

  /**
   *
   * @param context SparrowContext
   */
  public LazyAndCachedLookupManager(SparrowContext context) {
    super(context);
  }

  /**
   *
   */
  public void initialize() {
    llm = new LazyAndCacheLookupManager();
    DataLookUpConfig config = contxt.getConfiguration().getDataLookUp();
    List lookups = config.getLookups();
    lookupStack = LookupUtils.getLookupDependStacks(lookups);
    lookupObjects = new HashMap(contxt.getConfiguration().getDataLookUp().
                                getLookups().size());
    lookupIndex = LookupUtils.sequenceDependent(lookups);
    lookupMap = LookupUtils.getItemsInMap(lookups);
    initializeLookUps(lookupIndex, lookupMap);
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

    llm.clear();
    llm.setDriverRowEventListener(eventListener);
    llm.setDriverRow(rsltHolder.getDriverRow());

    rsltHolder.getDataSetAsKeyValue().putAll(llm.getResultAsKeyValue());
    rsltHolder.setLookupManager(llm);

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
  protected class LazyAndCacheLookupManager
      implements LookupManager {

    DriverRowEventListener eventListener = null;
    ResultRow driverRow = null;
    Map resultKeyValue = new HashMap();
    Map result = new HashMap();
    Map resultStatus = new HashMap();

    LazyAndCacheLookupManager() {
    }

    /**
     * clear
     */
    public void clear() {
      resultKeyValue.clear();
      result.clear();
      resultStatus.clear();
      eventListener = null;
    }

    /**
     * getLookupResult
     *
     * @param name String
     * @return RecordSet
     */
    public RecordSet getLookupResult(String key) throws DataException {
      return getLookupResult(key, null);
    }

    /**
     *
     * @param key String
     * @param customTokens Map
     * @throws DataException
     * @return RecordSet
     */
    public RecordSet getLookupResult(String key, Map customTokens) throws
        DataException {

      if (result.containsKey(key)) {
        return (RecordSet) result.get(key);
      }

      if (customTokens != null) {
        resultKeyValue.putAll(customTokens);
      }

      RecordSet rs = null;
      String lastKey = null;
      Stack lkupStack = (Stack) lookupStack.get(key);

      for (Iterator iter = lkupStack.iterator(); iter.hasNext(); ) {

        if (lastKey != null) {
          if (Constants.NO_RECORD_EXIST.equals(resultStatus.get(lastKey).
                                               toString())) {
            rs = new RecordSetImpl_Disconnected();
            return rs;
          }
        }

        String stkKey = lastKey = (String) iter.next();

        if (result.containsKey(stkKey)) {
          continue;
        }
        try {
          LookupObject item = (LookupObject) lookupObjects.get(stkKey);

          rs = item.getLookupData(driverRow,
                                  resultKeyValue, eventListener);
          result.put(stkKey, rs);
          resultStatus.put(stkKey,
                           (rs.getRowCount() == 0) ? Constants.NO_RECORD_EXIST :
                           Constants.RECORD_EXIST);
          if (rs.getRowCount() != 0) {
            addResultToGlobalLKUPMap(stkKey, rs, eventListener, resultKeyValue);
          }
          else {
             if(!key.equals(stkKey)){
               logger.warn("[" +
                           SparrowUtil.printDriverValue(driverRow) +
                           "]:Skipping Lookup [" + key + "] as the dependent [" +
                           stkKey +
                           "] return 0 record or caught in exception.");
             }

          }

        }
        catch (Exception exp) {
          logger.error("Exception occured while performing lookup[" + stkKey +
                       "]",
                       exp);
          rs = new RecordSetImpl_Disconnected();
          result.put(stkKey, rs);
          resultStatus.put(stkKey, Constants.NO_RECORD_EXIST);
        }
      }

      return rs;
    }

    /**
     * getLookupResults
     *
     * @return Map
     */
    public Map getLookupResults() throws DataException {
      Map tempRslt = new HashMap();
      for (Iterator it = lookupIndex.iterator(); it.hasNext(); ) {
        String key = (String) it.next();
        RecordSet rs = getLookupResult(key, null);
        tempRslt.put(key, rs);
      }
      return tempRslt;
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
      SparrowUtil.addResultAsKeyValue(row,
                                    Constants.DRIVER, resultKeyValue);

      result.put(Constants.DRIVER,
                 new RecordSetImpl_Disconnected(row));
    }

    /**
     *
     * @return Map
     */
    public Map getResultAsKeyValue() {
      return resultKeyValue;
    }

  }

}
