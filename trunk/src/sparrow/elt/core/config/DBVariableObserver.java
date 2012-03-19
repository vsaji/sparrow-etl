package sparrow.elt.core.config;

import sparrow.elt.core.dao.impl.ResultRow;

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */
public interface DBVariableObserver{

  public void populateVariable(String dpName,ResultRow rr);

}
