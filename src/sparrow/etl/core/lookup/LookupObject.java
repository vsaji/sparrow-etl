package sparrow.etl.core.lookup;

import java.util.Map;

import sparrow.etl.core.dao.impl.RecordSet;
import sparrow.etl.core.dao.impl.ResultRow;
import sparrow.etl.core.exception.DataException;
import sparrow.etl.core.transformer.DriverRowEventListener;


public interface LookupObject {

  public abstract RecordSet getLookupData(ResultRow row, Map resultMap,
                                          DriverRowEventListener eventListener) throws
      DataException;

}
