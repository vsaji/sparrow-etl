package sparrow.elt.impl.writer;

import sparrow.elt.core.config.SparrowDataWriterConfig;
import sparrow.elt.core.dao.util.TransactionEnabledConnectionProvider;
import sparrow.elt.core.log.SparrowLogger;
import sparrow.elt.core.log.SparrowrLoggerFactory;
import sparrow.elt.impl.dao.DBDataProviderImplTxn;

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
