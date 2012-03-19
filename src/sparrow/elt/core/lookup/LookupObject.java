package sparrow.elt.core.lookup;

import java.util.Map;

import sparrow.elt.core.dao.impl.RecordSet;
import sparrow.elt.core.dao.impl.ResultRow;
import sparrow.elt.core.exception.DataException;
import sparrow.elt.core.transformer.DriverRowEventListener;


public interface LookupObject {

  public abstract RecordSet getLookupData(ResultRow row, Map resultMap,
                                          DriverRowEventListener eventListener) throws
      DataException;

}
