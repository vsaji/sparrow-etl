package sparrow.elt.core.dao.provider;

import sparrow.elt.core.dao.impl.RecordSet;
import sparrow.elt.core.exception.DataException;

public interface DataProviderElement
    extends Cloneable {

  public abstract RecordSet getData() throws DataException;

  public abstract String getName();

  public abstract void close();

 public abstract DataProvider getDataProvider();

  public abstract Object clone() throws CloneNotSupportedException;
}
