package sparrow.etl.core.loadbalance;

import sparrow.etl.core.dao.impl.ResultRow;
import sparrow.etl.core.vo.SparrowResultHolder;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public interface RequestAssignerPolicy {

  abstract void assign(ResultRow row);
  abstract void assign(SparrowResultHolder srh);  
  abstract void assign(Object obj,int temp);

}
