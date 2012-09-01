package sparrow.etl.core.config;

import sparrow.etl.core.util.ConfigKeyConstants;
import sparrow.etl.jaxb.DATAPROVIDERType;

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */
public interface DataProviderConfig
    extends ConfigParam {

  abstract String getName();

  abstract DataProviderType getType();

  abstract String getClassName();

  abstract QueryConfig getQuery();

  abstract boolean isQueryExists();
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
class DataProviderConfigImpl
    extends ConfigParamImpl
    implements DataProviderConfig {

  private final String name;
  private final String className;
  private final QueryConfig query;
  private final DataProviderType type;

  /**
   *
   * @param provider DATAPROVIDERType
   */
  DataProviderConfigImpl(DATAPROVIDERType provider) {
    super(provider.getPARAM());
    this.name = provider.getNAME();
    this.className = provider.getCLASS();

    this.query = (isParameterExist(ConfigKeyConstants.PARAM_QUERY)) ?
        new QueryConfigImpl(this) : null;

    if (this.className == null || this.className.trim().equals("")) {
      String dtype = provider.getTYPE().toUpperCase();

      this.type = (DataProviderType.TYPE_IDENTIFIER.containsKey(dtype)) ?
          (DataProviderType) DataProviderType.TYPE_IDENTIFIER.get(
          dtype) : new DataProviderType(DataProviderType.PROVIDER_OTHER,
                                            DataProviderType.BASE_PROVIDER_CLASS,
                                            this.className);
    }
    else {
      this.type = new DataProviderType(DataProviderType.PROVIDER_OTHER,
                                       DataProviderType.BASE_PROVIDER_CLASS,
                                       this.className);

    }

  }

  public String getName() {
    return this.name;
  }

  public String getClassName() {
    return this.className;
  }

  public QueryConfig getQuery() {
    return this.query;
  }

  public DataProviderType getType() {
    return this.type;
  }

  /**
   * isQueryExists
   *
   * @return boolean
   */
  public boolean isQueryExists() {
    return query != null;
  }

}
