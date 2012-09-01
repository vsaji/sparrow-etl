package sparrow.etl.core.lookup;

import sparrow.etl.core.context.SparrowApplicationContext;
import sparrow.etl.core.util.Constants;

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public class DataLookupManagerFactory {

  /**
   *
   */
  private DataLookupManagerFactory() {
  }

  /**
   *
   * @param type String
   * @param context SparrowApplicationContext
   * @return DataLookupManager
   */
  public static final DataLookupManager getDataLookupManager(String type,
      SparrowApplicationContext context) {
    if (Constants.LOAD_TYPE_AUTO.equals(type)) {
      return new DataLookupManager(context);
    }
    else if (Constants.LOAD_TYPE_LAZY.equals(type)) {
      return new LazyDataLookupManager(context);
    }
    else if (Constants.LOAD_TYPE_AUTO_LAZY.equals(type)) {
      return new AutoAndLazyDataLookupManager(context);
    }
    return new DataLookupManager(context);
  }
}
