package sparrow.etl.core.transformer;

import sparrow.etl.core.util.IObjectPoolLifeCycle;

public interface TransformerLCEvent {

  public abstract void setOLC(IObjectPoolLifeCycle olc);

  public abstract void finalizeObject();

  public abstract void returnObject();

  public abstract void initialize();

  public abstract void staticInitialize();

  public abstract void destroy();
}
