package sparrow.etl.core.lookup;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import sparrow.etl.core.DataSetHolder;
import sparrow.etl.core.config.ConfigParam;
import sparrow.etl.core.config.DataLookUpConfig;
import sparrow.etl.core.config.LookUpConfig;
import sparrow.etl.core.config.SparrowDataLookupConfig;
import sparrow.etl.core.context.SparrowApplicationContext;
import sparrow.etl.core.context.SparrowContext;
import sparrow.etl.core.dao.impl.RecordSet;
import sparrow.etl.core.dao.impl.RecordSetImpl_Disconnected;
import sparrow.etl.core.dao.impl.ResultRow;
import sparrow.etl.core.exception.DataException;
import sparrow.etl.core.log.SparrowLogger;
import sparrow.etl.core.log.SparrowrLoggerFactory;
import sparrow.etl.core.transformer.DriverRowEventListener;
import sparrow.etl.core.util.CaseInSensitiveMap;
import sparrow.etl.core.util.Constants;
import sparrow.etl.core.util.SparrowUtil;


/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */

public class DataLookupManager {

  protected Map lookupMap;
  protected Map lookupObjects;
  protected Map resultStatus;
  protected List lookupIndex;

  protected AutoLookupManager lm;

  protected final SparrowApplicationContext contxt;

  private static final SparrowLogger logger = SparrowrLoggerFactory.
      getCurrentInstance(
      DataLookupManager.class);

  /**
   *
   * @param context SparrowApplicationContext
   */
  public DataLookupManager(SparrowContext context) {
    this.contxt = (SparrowApplicationContext) context;
    lm = getLookupManager();
  }

  /**
   *
   * @param context SparrowApplicationContext
   */
  public void initialize() {

    DataLookUpConfig config = contxt.getConfiguration().getDataLookUp();
    resultStatus = new HashMap();

    List lookups = config.getLookups();
    this.lookupObjects = new HashMap(contxt.getConfiguration().getDataLookUp().
                                     getLookups().size());
    this.lookupIndex = LookupUtils.sequenceDependent(lookups);
    this.lookupMap = LookupUtils.getItemsInMap(lookups);
    this.initializeLookUps(lookupIndex, lookupMap);

  }


  /**
   *
   * @param lkpNames List
   */
  protected void setAutoLookupIndex(List lkpNames){
    this.lookupIndex = lkpNames;
  }

  /**
   *
   * @return AutoLookupManager
   */
  protected AutoLookupManager getLookupManager(){
    return new AutoLookupManager();
  }

  /**
   *
   * @param rsltHolder DataSetHolder
   * @throws DataException
   */
  public void loadLookupResult(DataSetHolder rsltHolder,
                               DriverRowEventListener eventListener) throws
      DataException {

    //   long start = System.currentTimeMillis();
    lm.clear();
    resultStatus.clear();
    Map result = lm.getLookupResults();
    Map resultKeyValue = lm.getResultAsKeyValue();

    SparrowUtil.addResultAsKeyValue(rsltHolder.getDriverRow(),
                                  Constants.DRIVER, resultKeyValue);

    result.put(Constants.DRIVER,
               new RecordSetImpl_Disconnected(rsltHolder.getDriverRow()));
    resultStatus.put(Constants.DRIVER, Constants.RECORD_EXIST);

    for (Iterator iter = lookupIndex.iterator(); iter.hasNext(); ) {
      String key = iter.next().toString();
      try {
        LookUpConfig config = (LookUpConfig) lookupMap.get(key);
        LookupObject item = (LookupObject) lookupObjects.get(key);
        if ( (config.getDepends() == null ||
              config.getDepends().trim().equals(""))) {
          RecordSet rs = item.getLookupData(rsltHolder.getDriverRow(),
                                            resultKeyValue, eventListener);
          result.put(key, rs);
          resultStatus.put(key,
                           (rs.getRowCount() == 0) ? Constants.NO_RECORD_EXIST :
                           Constants.RECORD_EXIST);

          if (rs.getRowCount() != 0) {
            addResultToGlobalLKUPMap(key, rs, eventListener, resultKeyValue);
          }

        }
        else {
          String status = (String) resultStatus.get(config.getDepends());

          if (Constants.RECORD_EXIST.equals(status)) {
            RecordSet rs = item.getLookupData(rsltHolder.getDriverRow(),
                                              resultKeyValue, eventListener);
            result.put(key, rs);
            resultStatus.put(key,
                             (rs.getRowCount() == 0) ?
                             Constants.NO_RECORD_EXIST :
                             Constants.RECORD_EXIST);
            if (rs.getRowCount() != 0) {
              addResultToGlobalLKUPMap(key, rs, eventListener, resultKeyValue);
            }
          }
          else {
            logger.warn("[" +
                        SparrowUtil.printDriverValue(rsltHolder.getDriverRow()) +
                        "]:Skipping Lookup [" + key + "] as the dependent [" +
                        config.getDepends() +
                        "] return 0 record or caught in exception.");
            resultStatus.put(key, Constants.NO_RECORD_EXIST);
            result.put(key, new RecordSetImpl_Disconnected());
          }
        }
      }
      catch (Exception exp) {
        logger.error("Exception occured while performing lookup[" + key + "]",
                     exp);
        result.put(key, new RecordSetImpl_Disconnected());
        resultStatus.put(key, Constants.NO_RECORD_EXIST);

      }
    }
    rsltHolder.getDataSetAsKeyValue().putAll(resultKeyValue);
    rsltHolder.setLookupManager(lm);
    // System.out.println("Total Lookup time:"+(System.currentTimeMillis()-start));
  }

  /**
   *
   * @param lkpName String
   * @param rs RecordSet
   * @param eventListener DriverRowEventListener
   * @param resultKeyValue Map
   * @throws DataException
   */
  protected final void addResultToGlobalLKUPMap(String lkpName, RecordSet rs,
                                                DriverRowEventListener
                                                eventListener,
                                                Map resultKeyValue) throws
      DataException {

    ResultRow rr = (rs.getRowCount() > 1) ?
        eventListener.getSingleLookupResult(lkpName, rs) : rs.getFirstRow();

    SparrowUtil.addResultAsKeyValue(rr, lkpName,
                                  resultKeyValue);
  }

  /**
   *
   * @param lookup LookUpConfig
   */
  protected void initializeLookUps(Collection lookupNames, Map lookupsInMap) {

    for (Iterator iter = lookupNames.iterator(); iter.hasNext(); ) {
      String lookupName = (String) iter.next();
      LookUpConfig lookup = (LookUpConfig) lookupsInMap.get(lookupName);
      lookupObjects.put(lookupName, SparrowUtil.createObject(lookup.getClassName(),
          SparrowDataLookupConfig.class, new SpearDataLookupConfigImpl(lookup,
          contxt)));
    }

  }

  /**
   *
   * <p>Title: </p>
   * <p>Description: </p>
   * <p>Copyright: Copyright (c) 2004</p>
   * <p>Company: </p>
   * @author Saji Venugopalan
   * @version 1.0
   */
  private class SpearDataLookupConfigImpl
      implements SparrowDataLookupConfig {

    private final LookUpConfig item;
    private final SparrowContext context;

    SpearDataLookupConfigImpl(LookUpConfig item, SparrowContext context) {
      this.item = item;
      this.context = context;
    }

    /**
     * getInitParameter
     *
     * @return ConfigParam
     */
    public ConfigParam getInitParameter() {
      return item;
    }

    /**
     * getContext
     *
     * @return SparrowContext
     */
    public SparrowContext getContext() {
      return context;
    }

    /**
     * getName
     *
     * @return String
     */
    public String getName() {
      return item.getName();
    }

    /**
     * getClassName
     *
     * @return String
     */
    public String getClassName() {
      return item.getClassName();
    }

    /**
     * getColumns
     *
     * @return String
     */
    public String getColumns() {
      return item.getColumns();
    }

    /**
     * getDataProvider
     *
     * @return String
     */
    public String getDataProvider() {
      return item.getDataProvider();
    }

    /**
     * getFilter
     *
     * @return String
     */
    public String getFilter() {
      return item.getFilter();
    }
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
  protected class AutoLookupManager
      implements LookupManager {

    final Map lookupResults;
    // final Map resultKeyValue = new TreeMap(String.CASE_INSENSITIVE_ORDER);
    final Map resultKeyValue = new CaseInSensitiveMap();

    AutoLookupManager() {
      lookupResults = new HashMap();
    }

    /**
     * clear
     */
    public void clear() {
      lookupResults.clear();
      resultKeyValue.clear();
    }

    public Map getResultAsKeyValue() {
      return this.resultKeyValue;
    }

    /**
     * getLookupResult
     *
     * @param name String
     * @return RecordSet
     */
    public RecordSet getLookupResult(String name) throws DataException {
      return (RecordSet) lookupResults.get(name);
    }

    /**
     * getLookupResults
     *
     * @return Map
     */
    public Map getLookupResults() throws DataException {
      return lookupResults;
    }

    /**
     * getLookupResult
     *
     * @param name String
     * @param customToken Map
     * @return RecordSet
     */
    public RecordSet getLookupResult(String name, Map customToken) throws DataException{
      return getLookupResult(name);
    }

    /**
     * getNonCacheLookupResult
     *
     * @param lookupName String
     * @param customToken Map
     * @return RecordSet
     */
    public RecordSet getNonCacheLookupResult(String lookupName, Map customToken)  throws DataException{
      return getLookupResult(lookupName);
    }

    /**
     * getNonCacheLookupResult
     *
     * @param lookupName String
     * @return RecordSet
     */
    public RecordSet getNonCacheLookupResult(String lookupName) throws DataException{
      return getLookupResult(lookupName);
    }

  }

}
