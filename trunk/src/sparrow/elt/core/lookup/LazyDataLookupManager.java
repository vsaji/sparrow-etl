package sparrow.elt.core.lookup;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import sparrow.elt.core.DataSetHolder;
import sparrow.elt.core.config.DataLookUpConfig;
import sparrow.elt.core.context.SparrowContext;
import sparrow.elt.core.dao.impl.RecordSet;
import sparrow.elt.core.dao.impl.RecordSetImpl_Disconnected;
import sparrow.elt.core.dao.impl.ResultRow;
import sparrow.elt.core.exception.DataException;
import sparrow.elt.core.log.SparrowLogger;
import sparrow.elt.core.log.SparrowrLoggerFactory;
import sparrow.elt.core.transformer.DriverRowEventListener;
import sparrow.elt.core.util.Constants;
import sparrow.elt.core.util.SparrowUtil;


/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public class LazyDataLookupManager
    extends DataLookupManager {

  private Map lookupStack = null;
  private LazyLookupManager llm;

  private static final SparrowLogger logger = SparrowrLoggerFactory.
      getCurrentInstance(
      LazyDataLookupManager.class);

  /**
   *
   * @param context SparrowContext
   */
  public LazyDataLookupManager(SparrowContext context) {
    super(context);
  }

  /**
   *
   */
  public void initialize() {
    llm = new LazyLookupManager();
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
  protected class LazyLookupManager
      implements LookupManager {

    DriverRowEventListener eventListener = null;
    ResultRow driverRow = null;
    Map resultKeyValue = new HashMap();
    Map result = new HashMap();
    Map resultStatus = new HashMap();

    LazyLookupManager() {

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
    public RecordSet getLookupResult(String key, Map customToken) throws
        DataException {

      Map tmpResultStatus = new HashMap();

      if (customToken != null) {
        resultKeyValue.putAll(customToken);
      }

      RecordSet rs = null;
      String lastKey = null;
      Stack lkupStack = (Stack) lookupStack.get(key);

      for (Iterator iter = lkupStack.iterator(); iter.hasNext(); ) {

        if (lastKey != null) {
          if (Constants.NO_RECORD_EXIST.equals(tmpResultStatus.get(lastKey).
                                               toString())) {
            rs = new RecordSetImpl_Disconnected();
            return rs;
          }
        }

        String stkKey = lastKey = (String) iter.next();
        try {
          LookupObject item = (LookupObject) lookupObjects.get(stkKey);

          rs = item.getLookupData(driverRow,
                                  resultKeyValue, eventListener);
          result.put(stkKey, rs);
          tmpResultStatus.put(stkKey,
                              (rs.getRowCount() == 0) ?
                              Constants.NO_RECORD_EXIST :
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
          tmpResultStatus.put(stkKey, Constants.NO_RECORD_EXIST);
        }
      }
      return rs;
    }

    /**
     *
     * @throws DataException
     * @return Map
     */
    public Map getLookupResults() throws DataException {
      Map tempRslt = new HashMap();
      for (Iterator it = lookupIndex.iterator(); it.hasNext(); ) {
        String key = (String) it.next();
        RecordSet rs = getCacheLookupResult(key, null);
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

      // result.put(Constants.DRIVER,
      //           new RecordSetImpl_Disconnected(row));
    }

    /**
     *
     * @return Map
     */
    public Map getResultAsKeyValue() {
      return resultKeyValue;
    }

    /**
     *
     * @param key String
     * @param customTokens Map
     * @throws DataException
     * @return RecordSet
     */
    private RecordSet getCacheLookupResult(String key, Map customTokens) throws
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
            logger.warn("[" +
                        SparrowUtil.printDriverValue(driverRow) +
                        "]:Skipping Lookup [" + key + "] as the dependent [" +
                        stkKey +
                        "] return 0 record or caught in exception.");
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
  }

}
