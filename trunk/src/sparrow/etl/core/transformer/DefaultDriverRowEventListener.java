package sparrow.etl.core.transformer;

import sparrow.etl.core.dao.impl.QueryObject;
import sparrow.etl.core.dao.impl.RecordSet;
import sparrow.etl.core.dao.impl.ResultRow;
import sparrow.etl.core.exception.DataException;
import sparrow.etl.core.log.SparrowLogger;
import sparrow.etl.core.log.SparrowrLoggerFactory;

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */
class DefaultDriverRowEventListener  implements DriverRowEventListener {

  protected static final SparrowLogger logger = SparrowrLoggerFactory.
    getCurrentInstance(
    DefaultDriverRowEventListener.class);

  /**
   *
   */
  public DefaultDriverRowEventListener() {
  }

  /**
   *
   * @param lookupName String
   * @param query QueryObject
   * @return boolean
   */
  public boolean preLookUp(String lookupName, QueryObject query) {
     if (logger.isDebugEnabled()) {
       logger.log("preLookUp called [" + lookupName + "]", logger.DEBUG);
     }
     return true;
   }

   /**
    * postFinalize
    *
    * @param success boolean
    */
   public void postFinalize(boolean success) {
     if (logger.isDebugEnabled()) {
       logger.log("postFinalize called [" + success + "]", logger.DEBUG);
     }
   }

   /**
    *
    * @param lookupName String
    * @param filter String
    * @return String
    */
   public String preFilter(String lookupName, String filter) {
     if (logger.isDebugEnabled()) {
       logger.log("preFilter called [" + lookupName + "] [" + filter +
                  "]", logger.DEBUG);
     }
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
       if (logger.isDebugEnabled()) {
         logger.log("postLookUp called [" + lookupName + "] [" +
                    rs.getRowCount() +
                    "]", logger.DEBUG);
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
     if (logger.isDebugEnabled()) {
       logger.log("postWrite called [" + writerName + "] [" + success + "]",
                  logger.DEBUG);
     }
   }

   /**
    * preFinalize
    *
    * @return boolean
    */
   public boolean preFinalize() {
     if (logger.isDebugEnabled()) {
       logger.log("preFinalize called", logger.DEBUG);
     }
     return true;
   }

   /**
    * preQueue
    *
    * @return boolean
    */
   public boolean preQueue() {
     if (logger.isDebugEnabled()) {
       logger.log("preQueue called", logger.DEBUG);
     }
     return true;
   }

   /**
    * preWrite
    *
    * @param writerName String
    * @return boolean
    */
   public boolean preWrite(String writerName) {
     if (logger.isDebugEnabled()) {
       logger.log("preWrite called [" + writerName + "]", logger.DEBUG);
     }
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
