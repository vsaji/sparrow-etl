package sparrow.elt.core.transaction;

import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;

/**
 * object factory for TransDataSource so that it may be loaded into a JNDI registry
 */
public class SparrowDataSourceObjectFactory
    implements ObjectFactory {
  public Object getObjectInstance(Object obj, Name name, Context nameCtx,
                                  Hashtable environment) throws Exception {
    Reference ref = (Reference) obj;
    if (ref.getClassName().equals(SparrowDataSource.class.getName())) {
      String nm = getProperty(ref, "name");
      return SparrowDataSource.getSpearDataSource(nm);
    }
    else {
      return null;
    }
  }

  protected String getProperty(Reference ref, String s) {
    RefAddr addr = ref.get(s);
    if (addr == null) {
      return null;
    }
    return (String) addr.getContent();
  }
}
