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
public interface SparrowNotifierConfig
    extends SparrowConfig {
  public abstract String getClassName();

  public abstract String getType();
}
