package sparrow.etl.core.dao.impl;

import java.util.List;

import sparrow.etl.core.exception.DataException;


public interface RecordSetCacheSupport
    extends RecordSet {

  public abstract List getResult() throws DataException;

  public abstract void setResult(List result);

  public abstract void addRow(ColumnHeader ch, Object[] row);

  public abstract void addRow(ResultRow row);

}
