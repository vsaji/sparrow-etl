package sparrow.etl.core.config;

public interface SparrowDataTransformerConfig
    extends SparrowConfig {

  public abstract int getPoolSize();

  public abstract DataTransformerType getTransformerType();

}
