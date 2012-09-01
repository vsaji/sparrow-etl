package sparrow.etl.core.config;

public interface SparrowEventConfig extends SparrowConfig {
  public String getClassName();
  public String getType();
  public String getNotifierName();

}
