package sparrow.etl.impl.writer;

import java.io.File;

import sparrow.etl.core.config.SparrowDataWriterConfig;
import sparrow.etl.core.exception.ResourceException;
import sparrow.etl.core.exception.SparrowRuntimeException;
import sparrow.etl.core.log.SparrowLogger;
import sparrow.etl.core.log.SparrowrLoggerFactory;
import sparrow.etl.core.resource.FTPClientSession;
import sparrow.etl.core.util.ConfigKeyConstants;
import sparrow.etl.core.util.SparrowUtil;


/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public class FTPWriter
    extends AbstractEOAWriter {

  protected String[] files = null;
  protected String resource = null;
  protected String destDir = null;
  protected int ftpMode = 1;

  private static final SparrowLogger logger = SparrowrLoggerFactory.
      getCurrentInstance(FTPWriter.class);

  /**
   *
   * @param config SparrowDataWriterConfig
   */
  public FTPWriter(SparrowDataWriterConfig config) {
    super(config);
    SparrowUtil.validateParam(new String[] {ConfigKeyConstants.PARAM_DEST_DIR,
                            ConfigKeyConstants.PARAM_FILE_LIST,
                            ConfigKeyConstants.PARAM_RESOURCE}
                            , "FTPWriter",
                            config.getInitParameter());

  }

  /**
   *
   */
  public void initialize() {
    files = getFileNamesFromFileWriter();
    destDir = config.getInitParameter().getParameterValue(
        ConfigKeyConstants.PARAM_DEST_DIR);
    resource = config.getInitParameter().getParameterValue(
        ConfigKeyConstants.PARAM_RESOURCE);
    String mode = SparrowUtil.performTernary(config.getInitParameter(),
                                           ConfigKeyConstants.PARAM_FTP_MODE, "binary");
    ftpMode = ("binary".equals(mode)) ? FTPClientSession.BINARY :
        FTPClientSession.ASCII;
  }

  /**
   *
   */
  public void doEndOfProcess() {
    FTPClientSession ftp = null;

    try {
      ftp = (FTPClientSession) config.getContext().getResource(resource).
          getResource();
    }
    catch (ResourceException ex) {
      throw new SparrowRuntimeException(ex);
    }
    ftp.open();
    ftp.changeDirectory(destDir);
    ftp.changeMode(ftpMode);

    for (int i = 0; i < files.length; i++) {
      File file = new File(files[i]);
      ftp.upload(file);
    }
    ftp.close();
  }
}
