/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sparrow.elt.core.config;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import sparrow.elt.core.exception.InitializationException;
import sparrow.elt.core.util.Constants;
import sparrow.elt.jaxb.DATALOOKUPType;
import sparrow.elt.jaxb.LOOKUPType;


/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */
public interface DataLookUpConfig {
  abstract List getLookups();

  abstract boolean isLookUpExist();

  abstract String getLoadType();
}

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */
class DataLookUpConfigImpl
    implements DataLookUpConfig {

  private List lookups = null;
  private String loadType;

  /**
   *
   * @param datasources DATASOURCESType
   */
  DataLookUpConfigImpl(DATALOOKUPType datalookup) {
    lookups = new ArrayList();
    loadType = Constants.LOAD_TYPE_AUTO;
    if (datalookup != null) {
      loadType = datalookup.getLOADTYPE().trim().toUpperCase();
      this.bind(datalookup);
    }

  }

  /**
   *
   * @return List
   */
  public List getLookups() {
    return lookups;
  }

  /**
   *
   * @param datasource DatasourceConfig
   */
  private void addLookups(LookUpConfig lookup) {
    lookups.add(lookup);
  }

  /**
   *
   */
  private void bind(DATALOOKUPType datalookup) {
    List lkups = datalookup.getLOOKUP();
    if (!lkups.isEmpty()) {
      for (Iterator iter = lkups.iterator(); iter.hasNext(); ) {
        LOOKUPType item = (LOOKUPType) iter.next();
        addLookups(new LookUpConfigImpl(item));

        if (Constants.LOAD_TYPE_LAZY.equals(item.getLOADTYPE()) &&
            item.getDEPENDS() != null) {
          throw new InitializationException(
              "Attribute [DEPENDS] is not supported with LOAD-TYPE[LAZY] - Lookup Name[" +
              item.getNAME() + "]");
        }

        if (Constants.LOAD_TYPE_LAZY.equals(item.getLOADTYPE()) &&
            (!Constants.LOAD_TYPE_AUTO_LAZY.equals(loadType))) {
          loadType = Constants.LOAD_TYPE_AUTO_LAZY;
        }
      }
    }
  }

  /**
   * isLookUpExist
   *
   * @return boolean
   */
  public boolean isLookUpExist() {
    return!lookups.isEmpty();
  }

  /**
   * getLoadType
   *
   * @return String
   */
  public String getLoadType() {
    return loadType;
  }

}
