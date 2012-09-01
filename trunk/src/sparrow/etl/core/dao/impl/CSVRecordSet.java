package sparrow.etl.core.dao.impl;

import sparrow.etl.core.exception.DataException;

public interface CSVRecordSet
    extends RecordSetCacheSupport {

  public abstract boolean isFileFullyRead();

  public abstract void populate() throws DataException;

}
