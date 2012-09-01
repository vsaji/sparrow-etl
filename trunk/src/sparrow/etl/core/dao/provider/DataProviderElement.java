package sparrow.etl.core.dao.provider;

import sparrow.etl.core.dao.impl.RecordSet;
import sparrow.etl.core.exception.DataException;

public interface DataProviderElement
    extends Cloneable {

  public abstract RecordSet getData() throws DataException;

  public abstract String getName();

  public abstract void close();

 public abstract DataProvider getDataProvider();

  public abstract Object clone() throws CloneNotSupportedException;
}
