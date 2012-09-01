package sparrow.etl.core.lookup;

import java.util.Map;

import sparrow.etl.core.dao.impl.RecordSet;


public interface LookupResult {

  public RecordSet getLookupResult(String lookupName);

  public Map getLookupResults();

}
