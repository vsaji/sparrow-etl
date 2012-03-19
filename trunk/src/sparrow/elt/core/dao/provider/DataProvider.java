package sparrow.elt.core.dao.provider;

import sparrow.elt.core.dao.impl.QueryObject;
import sparrow.elt.core.dao.impl.RecordSet;
import sparrow.elt.core.exception.DataException;

public interface DataProvider
    extends Cloneable {


  public abstract void initialize();

  public abstract RecordSet getData() throws DataException;

  public abstract String getName();

  public abstract void destory();

  public abstract Object clone() throws CloneNotSupportedException;

  public abstract QueryObject getQuery();

  public int executeQuery() throws DataException;

}
