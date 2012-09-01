package sparrow.etl.impl.writer;

import sparrow.etl.core.config.SparrowDataWriterConfig;

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */
public class DBDataWriterSupportSelfCommit
    extends GenericDBDataWriter {

  public DBDataWriterSupportSelfCommit(SparrowDataWriterConfig config) {
    super(config);
  }

}
