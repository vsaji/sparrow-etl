package sparrow.etl.core.config;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import sparrow.etl.core.util.TokenResolver;
import sparrow.etl.jaxb.ASSERTERype;
import sparrow.etl.jaxb.CYCLEDEPENDENCIESype;
import sparrow.etl.jaxb.MODULEType;


/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */
public interface ModuleConfig
    extends ConfigParam {

  abstract String getName();

  abstract String getProcessId();

  abstract String getDescription();

  abstract LoadBalancerConfig getLoadBalancer();

  abstract List getCycleDependencyAsserters();
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
class ModuleConfigImpl
    extends ConfigParamImpl
    implements ModuleConfig {

  private final String name;
  private final String description;
  private final String processId;
  private List asserters = null;
  private final LoadBalancerConfig loadBalancer;

  static ConfigParam MODULE_PARAM;

  static final TokenResolver MODULE_PROP = new TokenResolver() {
    public String getTokenValue(String token) {
      return MODULE_PARAM.getParameterValue(token.substring(token.indexOf("@")+1));
    }
  };

  static final String TOKEN_IDENTIFIER = "${module@";

  /**
   *
   * @param module MODULEType
   * @param parameter ConfigParam
   */
  ModuleConfigImpl(MODULEType module) {
    super(module);
    this.name = module.getNAME();
    this.description = module.getDESCRIPTION();
    this.processId = module.getPROCESSID();
    this.asserters = new ArrayList();
    this.loadBalancer = new LoadBalancerConfigImpl(module.getLOADBALANCE());
    MODULE_PARAM = this;
    if (module.getCYCLEDEPENDENCIES() != null) {
      bind(module.getCYCLEDEPENDENCIES());
    }
  }

  public String getName() {
    return this.name;
  }

  public String getDescription() {
    return this.description;
  }

  /**
   *
   * @return List
   */
  public List getCycleDependencyAsserters() {
    return asserters;
  }

  public LoadBalancerConfig getLoadBalancer() {
    return this.loadBalancer;
  }

  /**
   *
   * @param assertters
   */
  private void addAsserters(AsserterConfig assertters) {
    asserters.add(assertters);
  }

  /**
   *
   */
  private void bind(CYCLEDEPENDENCIESype cycleDeps) {
    List assrtrs = cycleDeps.getASSERTER();
    if (!assrtrs.isEmpty()) {
      for (Iterator iter = assrtrs.iterator(); iter.hasNext(); ) {
        ASSERTERype item = (ASSERTERype) iter.next();
        addAsserters(new AsserterConfigImpl(item));
      }
    }
  }

  /**
   * getProcessId
   *
   * @return String
   */
  public String getProcessId() {
    return processId;
  }

  /**
   *
   * @return boolean
   */
  protected boolean isModuleConfig(){
    return true;
  }

}
