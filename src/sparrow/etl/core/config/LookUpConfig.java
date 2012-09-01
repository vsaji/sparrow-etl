/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sparrow.etl.core.config;

import sparrow.etl.core.exception.InitializationException;
import sparrow.etl.jaxb.LOOKUPType;

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */
public interface LookUpConfig
    extends ConfigParam, DependentIndexingSupport {

  abstract String getClassName();
  abstract String getDataProvider();
  abstract String getFilter();
  abstract String getColumns();
  abstract String getLoadType();

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
class LookUpConfigImpl
    extends ConfigParamImpl
    implements LookUpConfig {

  private final String name;
  private final String className;
  private final String depends;
  private final String dataProvider;
  private final String filter;
  private final String columns;
  private final String loadType;

  LookUpConfigImpl(LOOKUPType lookup) {
    super(lookup.getPARAM());
    this.name = lookup.getNAME();
    if (name.equalsIgnoreCase("driver")) {
      throw new InitializationException("Keyword [driver] cannot be used as look-up identifier.");
    }
    this.className = (lookup.getCLASS() == null) ?
        "sparrow.etl.impl.lookup.ProxyLookupObject" : lookup.getCLASS();
    this.depends = lookup.getDEPENDS();
    this.dataProvider = lookup.getDATAPROVIDER();
    this.filter = lookup.getFILTER();
    this.columns = lookup.getCOLUMNS();
    this.loadType = lookup.getLOADTYPE().toUpperCase();
  }

  public String getName() {
    return name;
  }

  public String getClassName() {
    return className;
  }

  public String getDepends() {
    return depends;
  }

  public String getDataProvider() {
    return dataProvider;
  }

  /**
   * getFilter
   *
   * @return String
   */
  public String getFilter() {
    return filter;
  }

  /**
   * getColumns
   *
   * @return String
   */
  public String getColumns() {
    return columns;
  }

  /**
   * getLoadType
   *
   * @return String
   */
  public String getLoadType() {
    return loadType;
  }

}
