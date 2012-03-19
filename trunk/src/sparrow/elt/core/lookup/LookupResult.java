package sparrow.elt.core.lookup;

import java.util.Map;

import sparrow.elt.core.dao.impl.RecordSet;


public interface LookupResult {

  public RecordSet getLookupResult(String lookupName);

  public Map getLookupResults();

}
