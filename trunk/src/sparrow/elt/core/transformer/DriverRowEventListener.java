package sparrow.elt.core.transformer;

import sparrow.elt.core.dao.impl.QueryObject;
import sparrow.elt.core.dao.impl.RecordSet;
import sparrow.elt.core.dao.impl.ResultRow;

public interface DriverRowEventListener {

  public abstract void clear();

  public abstract boolean preLookUp(String lookupName, QueryObject query);

  public abstract void postLookUp(String lookupName, RecordSet rs);

  public abstract boolean preQueue();

  public abstract ResultRow getSingleLookupResult(String lookupName,RecordSet rs);

  public abstract boolean preWrite(String writerName);

  public abstract void postWrite(String writerName, boolean success);

  public abstract boolean preFinalize();

  public abstract String preFilter(String lookupName, String filter);

  public abstract void postFinalize(boolean success);

}
