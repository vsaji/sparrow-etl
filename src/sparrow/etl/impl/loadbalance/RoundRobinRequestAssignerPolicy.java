package sparrow.etl.impl.loadbalance;

import java.util.Map;

import sparrow.etl.core.config.SparrowConfig;
import sparrow.etl.core.dao.impl.ResultRow;


/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */
public class RoundRobinRequestAssignerPolicy
    extends AbstractRequestAssignerPolicy {


  /**
   *
   * @param fifos Map
   * @param config SparrowConfig
   */
  public RoundRobinRequestAssignerPolicy(Map fifos,
                                         SparrowConfig config) {
    super(fifos, config);
  }

  /**
   * assign
   *
   * @param row ResultRow
   */
  public void assign(ResultRow row) {
   super.assign(row,0);
  }

}
