package sparrow.etl.core.config;


/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public interface SparrowDataLookupConfig extends SparrowConfig{

  public abstract String getClassName();
  public abstract String getFilter();
  public abstract String getColumns();
  public abstract String getDataProvider();

}
