package sparrow.elt.core.dao.impl;

import sparrow.elt.core.exception.DataException;

public interface CSVRecordSet
    extends RecordSetCacheSupport {

  public abstract boolean isFileFullyRead();

  public abstract void populate() throws DataException;

}
