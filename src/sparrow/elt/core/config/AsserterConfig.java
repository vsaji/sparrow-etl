package sparrow.elt.core.config;

import sparrow.elt.jaxb.ASSERTERype;

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */
public interface AsserterConfig
    extends ConfigParam {

  abstract String getClassName();
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
class AsserterConfigImpl
    extends ConfigParamImpl
    implements AsserterConfig {

  private String className = null;

  AsserterConfigImpl(ASSERTERype asserter) {
    super(asserter.getPARAM());
    this.className = asserter.getCLASS();
  }

  public String getClassName() {
    return this.className;
  }

}
