package sparrow.etl.impl.transformer;

import sparrow.etl.core.DataSet;
import sparrow.etl.core.config.SparrowDataTransformerConfig;
import sparrow.etl.core.dao.impl.QueryObject;
import sparrow.etl.core.dao.impl.RecordSet;
import sparrow.etl.core.transformer.AbstractDataTransformer;
import sparrow.etl.core.transformer.DriverRowEventListener;
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
public class DummyDataTransformer
    extends AbstractDataTransformer
    implements DriverRowEventListener {

  public DummyDataTransformer(SparrowDataTransformerConfig config) {
    super(config);
    super.setDriverRowEventListener(this);
  }

  /**
   * enrichData
   *
   * @param dataSet DataSet
   * @return DataOutputHolder
   */
  public DataOutputHolder enrichData(DataSet dataSet) {
    DataOutputHolder dh = new DataOutputHolder();
    dh.setTokenValue(dataSet.getDataSetAsKeyValue());
    return dh;
  }

  /**
   * clear
   */
  public void clear() {
  }

  /**
   * postFilter
   *
   * @param lookupName String
   * @param rs RecordSet
   */
  public String preFilter(String lookupName, String filter) {
    return filter;
  }

  /**
   * postFinalize
   *
   * @param success boolean
   */
  public void postFinalize(boolean success) {
  }

  /**
   * postLookUp
   *
   * @param lookupName String
   * @param rs RecordSet
   */
  public void postLookUp(String lookupName, RecordSet rs) {
  }

  /**
   * postWrite
   *
   * @param writerName String
   * @param success boolean
   */
  public void postWrite(String writerName, boolean success) {
  }

  /**
   * preFinalize
   *
   * @return boolean
   */
  public boolean preFinalize() {
    return true;
  }

  /**
   * preLookUp
   *
   * @param lookupName String
   * @param query QueryObject
   * @return boolean
   */
  public boolean preLookUp(String lookupName, QueryObject query) {
    return true;
  }

  /**
   * preQueue
   *
   * @return boolean
   */
  public boolean preQueue() {
    return true;
  }

  /**
   * preWrite
   *
   * @param writerName String
   * @return boolean
   */
  public boolean preWrite(String writerName) {
    return true;
  }
}
