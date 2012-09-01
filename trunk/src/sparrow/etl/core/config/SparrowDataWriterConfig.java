package sparrow.etl.core.config;

public interface SparrowDataWriterConfig
    extends SparrowConfig {

  public abstract String getDataWriterClass();

  public abstract String getOnError();

  public abstract String getDepends();

  public abstract String getTriggerEvent();

}
