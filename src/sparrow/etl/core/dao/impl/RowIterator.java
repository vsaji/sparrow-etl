package sparrow.etl.core.dao.impl;

import sparrow.etl.core.exception.DataException;

public interface RowIterator {

  public abstract boolean hasNext() throws DataException;

  abstract ResultRow next() throws DataException;
}
