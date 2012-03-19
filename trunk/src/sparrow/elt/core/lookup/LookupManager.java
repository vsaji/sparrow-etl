package sparrow.elt.core.lookup;

import java.util.Map;

import sparrow.elt.core.dao.impl.RecordSet;
import sparrow.elt.core.exception.DataException;


/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */
public interface LookupManager {

  public abstract RecordSet getLookupResult(String name) throws DataException;

  public abstract RecordSet getLookupResult(String name, Map customToken) throws
      DataException;

  public abstract void clear();

  public abstract Map getLookupResults()  throws DataException;
}
