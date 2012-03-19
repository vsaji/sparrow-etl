package sparrow.elt.core.dao.impl;

import sparrow.elt.core.exception.DataException;

public interface RowIterator {

  public abstract boolean hasNext() throws DataException;

  abstract ResultRow next() throws DataException;
}
