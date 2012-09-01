package sparrow.etl.impl.transformer;

import java.util.ArrayList;
import java.util.Iterator;

import sparrow.etl.core.DataSet;
import sparrow.etl.core.config.SparrowDataTransformerConfig;
import sparrow.etl.core.dao.impl.QueryObject;
import sparrow.etl.core.dao.impl.RecordSet;
import sparrow.etl.core.dao.impl.ResultRow;
import sparrow.etl.core.exception.DataException;
import sparrow.etl.core.exception.EnrichDataException;
import sparrow.etl.core.exception.RejectionException;
import sparrow.etl.core.log.SparrowLogger;
import sparrow.etl.core.log.SparrowrLoggerFactory;
import sparrow.etl.core.transformer.AbstractDataTransformer;
import sparrow.etl.core.transformer.DriverRowEventListener;
import sparrow.etl.core.util.SparrowUtil;
import sparrow.etl.core.vo.DataOutputHolder;


/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */
public class DefaultDataTransformer
    extends AbstractDataTransformer {

  private static final SparrowLogger logger = SparrowrLoggerFactory.
      getCurrentInstance(
      DefaultDataTransformer.class);
  private ArrayList emptyLookupValue = new ArrayList();

  private final String key;

  /**
   *
   * @param config SparrowDataTransformerConfig
   */
  public DefaultDataTransformer(SparrowDataTransformerConfig config) {
    super(config);
    super.setDriverRowEventListener(new DriverRowEventListenerImpl());
    key = config.getName();
  }

  /**
   *
   * @throws EnrichDataException
   * @return DataOutputHolder
   */
  public DataOutputHolder enrichData(DataSet dataSet) throws
      EnrichDataException {
    DataOutputHolder dh = new DataOutputHolder();
    dh.setTokenValue(dataSet.getDataSetAsKeyValue());
    if (!emptyLookupValue.isEmpty()) {
      try {
        super.markForRejection("Lookup returns empty value for: " +
                               getTokenAsString());
      }
      catch (RejectionException ex) {
        logger.error(
            "RejectionException exception occure while invoking markForRejection[" +
            SparrowUtil.printDriverValue(dataSet.getDriverRow()) + "]");
      }
    }
    return dh;
  }

  private String getTokenAsString() {
    StringBuffer sb = new StringBuffer();
    for (Iterator it = emptyLookupValue.iterator(); it.hasNext(); ) {
      sb.append(it.next().toString()).append(",");
    }
    sb.deleteCharAt(sb.length() - 1);
    return sb.toString();
  }

  /**
   * initialize
   */
  public void initialize() {
    super.initialize();
    emptyLookupValue.clear();
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
  private class DriverRowEventListenerImpl
      implements DriverRowEventListener {

    public boolean preLookUp(String lookupName, QueryObject query) {
      logger.debug("preLookUp called [" + lookupName + "]");
      return true;
    }

    /**
     * postFinalize
     *
     * @param success boolean
     */
    public void postFinalize(boolean success) {
      logger.debug("postFinalize called [" + success + "]");
    }

    /**
     * postLookUp
     *
     * @param lookupName String
     * @param rs RecordSet
     */
    public String preFilter(String lookupName, String filter) {

      logger.debug("preFilter called [" + lookupName + "] [" + filter +
                   "]");
      return filter;
    }

    /**
     * postQuery
     *
     * @param lookupName String
     * @param rs RecordSet
     */
    public void postLookUp(String lookupName, RecordSet rs) {
      try {
        logger.debug("postLookUp called [" + lookupName + "] [" +
                     rs.getRowCount() +
                     "]");
        if (rs.getRowCount() == 0) {
          emptyLookupValue.add(lookupName);
        }
      }
      catch (DataException ex) {
      }
    }

    /**
     * postWrite
     *
     * @param writerName String
     * @param success boolean
     */
    public void postWrite(String writerName, boolean success) {
      logger.debug("postWrite called [" + writerName + "] [" + success + "]");
    }

    /**
     * preFinalize
     *
     * @return boolean
     */
    public boolean preFinalize() {
      logger.debug("preFinalize called");
      return emptyLookupValue.isEmpty();
    }

    /**
     * preQueue
     *
     * @return boolean
     */
    public boolean preQueue() {
      logger.debug("preQueue called");
      return true;
    }

    /**
     * preWrite
     *
     * @param writerName String
     * @return boolean
     */
    public boolean preWrite(String writerName) {
      logger.debug("preWrite called [" + writerName + "]");
      return true;
    }

    /**
     * clear
     */
    public void clear() {

    }

    /**
     * getSingleLookupResult
     *
     * @param lookupName String
     * @param rs RecordSet
     * @return ResultRow
     */
    public ResultRow getSingleLookupResult(String lookupName, RecordSet rs) {
      try {
        return rs.getFirstRow();
      }
      catch (DataException ex) {
        return null;
      }
    }

  }

}
