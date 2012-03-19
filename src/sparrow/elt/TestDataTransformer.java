package sparrow.elt;

import sparrow.elt.core.DataSet;
import sparrow.elt.core.config.SparrowDataTransformerConfig;
import sparrow.elt.core.dao.impl.ResultRow;
import sparrow.elt.core.exception.*;
import sparrow.elt.core.transformer.AbstractDataTransformer;
import sparrow.elt.core.vo.DataOutputHolder;


/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public class TestDataTransformer extends AbstractDataTransformer{

  /**
   *
   * @param config SparrowDataTransformerConfig
   */
  public TestDataTransformer(SparrowDataTransformerConfig config) {
    super(config);
  }

  /**
   * enrichData
   *
   * @param dataSet DataSet
   * @return DataOutputHolder
   */
  public DataOutputHolder enrichData(DataSet dataSet) {
    DataOutputHolder dh = new DataOutputHolder();
    try {
    //Accessing the row
    ResultRow r = dataSet.getDriverRow();
    StringBuffer sb = new StringBuffer();
      sb.append(r.getValue("TRANS_ID")).append(",");
      sb.append(r.getValue("TRANS_VERS_ID")).append(",");
      sb.append(r.getValue("PRODUCT_ID")).append(",");
      sb.append(r.getValue("ODS_ORDER_ID")).append(",");
      sb.append(r.getValue("ODS_ORDER_VERS_ID"));
      dh.addObject("content",sb.toString());
      Thread.sleep(1000);
    }
    catch (Exception ex) {
      ex.printStackTrace();
      try {
        super.markForRejection("Exception Occured", ex);
      }
      catch (RejectionException ex1) {
        ex1.printStackTrace();
      }
    }
    return dh;
  }
}
