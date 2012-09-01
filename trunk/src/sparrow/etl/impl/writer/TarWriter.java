package sparrow.etl.impl.writer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

import sparrow.etl.core.config.SparrowDataWriterConfig;
import sparrow.etl.core.exception.SparrowRuntimeException;
import sparrow.etl.core.log.SparrowLogger;
import sparrow.etl.core.log.SparrowrLoggerFactory;
import sparrow.etl.core.util.ConfigKeyConstants;
import sparrow.etl.core.util.SparrowUtil;
import sparrow.etl.core.util.tar.TarArchive;
import sparrow.etl.core.util.tar.TarEntry;


/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public class TarWriter
    extends ZipWriter {

  private static final SparrowLogger logger = SparrowrLoggerFactory.
      getCurrentInstance(TarWriter.class);

  private boolean compress = true;

  /**
   *
   * @param config SparrowDataWriterConfig
   */
  public TarWriter(SparrowDataWriterConfig config) {
    super(config, "TarWriter");
  }

  /**
   *
   */
  public void initialize() {
    super.initialize();
    compress = SparrowUtil.performTernary(config.getInitParameter(),
                                        ConfigKeyConstants.PARAM_COMPRESS, true);
  }

  /**
   *
   * @param flag int
   */
  public void doEndOfProcess() {
    try {
      SparrowUtil.doFileExistAction(outFile, backupOnExist);

      OutputStream os = (compress) ?
          (OutputStream)new GZIPOutputStream(new FileOutputStream(outFile)) :
          new FileOutputStream(outFile);

      TarArchive archive = new TarArchive(os, (512 * 20));

      for (int i = 0; i < files.length; i++) {
        File f = new File(files[i]);
        TarEntry entry = new TarEntry(f);
        entry.setName(f.getName());
        entry.setUnixTarFormat();
        logger.info("Adding file[" + files[i] + "] into Tar Archive.");
        archive.writeEntry(entry, true);
      }
      archive.closeArchive();
      os.close();
      handleFilePreservation();
      logger.info("Tar Archive [" + outFile + "] created.");
    }
    catch (Exception e) {
      throw new SparrowRuntimeException(
          "Exception occured while compressing files", e);
    }
  }

}
