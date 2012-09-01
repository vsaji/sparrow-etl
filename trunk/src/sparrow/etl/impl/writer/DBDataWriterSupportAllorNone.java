package sparrow.etl.impl.writer;

import sparrow.etl.core.config.SparrowDataWriterConfig;
import sparrow.etl.core.dao.util.TransactionEnabledConnectionProvider;
import sparrow.etl.core.log.SparrowLogger;
import sparrow.etl.core.log.SparrowrLoggerFactory;
import sparrow.etl.impl.dao.DBDataProviderImplTxn;

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */
public class DBDataWriterSupportAllorNone
    extends GenericDBDataWriter {

  private static final SparrowLogger logger = SparrowrLoggerFactory.
      getCurrentInstance(
      DBDataWriterSupportAllorNone.class);

  /**
   *
   * @param config SparrowDataWriterConfig
   */
  public DBDataWriterSupportAllorNone(SparrowDataWriterConfig config) {
    super(config);
  }

  /**
   * initialize
   */
  public void initialize() {
    super.initialize();
    logger.debug("Initialize():" + WRITER_NAME);
    dbdp.setConnectionProvider(TransactionEnabledConnectionProvider.getInstance());
  }

}
