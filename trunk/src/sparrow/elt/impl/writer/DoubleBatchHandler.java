package sparrow.elt.impl.writer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import sparrow.elt.core.dao.provider.impl.DBDataProvider;
import sparrow.elt.core.exception.DataException;


/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public class DoubleBatchHandler {

  private ArrayList sqlParamCollections = null;
  private final DBDataProvider dbdp;

  /**
   *
   * @param dbdp DBDataProvider
   * @param sql String
   */
  DoubleBatchHandler(DBDataProvider dbdp) {
    this.dbdp = dbdp;
    sqlParamCollections = new ArrayList();
  }

  /**
   *
   * @param query QueryObject
   */
  public void add(List params) {
    for (Iterator it = params.iterator(); it.hasNext(); ) {
      sqlParamCollections.add(it.next());
    }
  }

  /**
   *
   */
  public int[] executeBatch() throws DataException {
    //  System.out.println("["+WRITER_NAME+"]["+hashCode()+"][executeBatch]");
    return dbdp.executeBatch(sqlParamCollections);
  }

  /**
   * reset
   */
  public void reset() {
    //   System.out.println("["+WRITER_NAME+"]["+hashCode()+"][reset]");
    sqlParamCollections.clear();
  }

}
