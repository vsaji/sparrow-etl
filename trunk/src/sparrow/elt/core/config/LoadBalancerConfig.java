package sparrow.elt.core.config;

import sparrow.elt.core.util.Constants;
import sparrow.elt.core.util.SparrowUtil;
import sparrow.elt.jaxb.LOADBALANCEType;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */

public interface LoadBalancerConfig
    extends ConfigParam {

  abstract String getPolicy();

  abstract String getClassName();

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
class LoadBalancerConfigImpl
    extends ConfigParamImpl
    implements LoadBalancerConfig {

  private final String className, policy;

  LoadBalancerConfigImpl(LOADBALANCEType loadBalance) {
    super( (loadBalance != null) ? loadBalance.getPARAM() : null);
    if (loadBalance != null) {
      this.policy = loadBalance.getPOLICY();
      this.className = (loadBalance.getCLASS() == null) ?
          (String) SparrowUtil.getImplConfig("lbpolicy").get(this.policy) :
          loadBalance.getCLASS();;
    }
    else {
      this.className = (String) SparrowUtil.getImplConfig("lbpolicy").get(
          Constants.DEFAULT_LB_POLICY);
      this.policy = Constants.DEFAULT_LB_POLICY;
    }
  }

  public String getClassName() {
    return this.className;
  }

  public String getPolicy() {
    return this.policy;
  }

}
