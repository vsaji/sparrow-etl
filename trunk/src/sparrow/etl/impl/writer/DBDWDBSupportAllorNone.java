package sparrow.etl.impl.writer;

import sparrow.etl.core.config.SparrowDataWriterConfig;
import sparrow.etl.core.dao.util.TransactionEnabledConnectionProvider;
import sparrow.etl.impl.dao.DBDataProviderImplTxn;

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public class DBDWDBSupportAllorNone
    extends DBDWSupportDBSelfCommit {

  /**
   *
   * @param config SparrowDataWriterConfig
   */
  public DBDWDBSupportAllorNone(SparrowDataWriterConfig config) {
    super(config);
  }

  /**
   * initialize
   */
  public void initialize() {
    super.initialize();
    dbdp.setConnectionProvider(TransactionEnabledConnectionProvider.getInstance());
    handler = new DoubleBatchHandler(dbdp);
  }

  /**
   *
   * @return boolean
   */
  public boolean isInTransaction(){
    return true;
  }

}
