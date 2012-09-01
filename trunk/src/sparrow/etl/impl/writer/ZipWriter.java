package sparrow.etl.impl.writer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import sparrow.etl.core.config.SparrowDataWriterConfig;
import sparrow.etl.core.exception.SparrowRuntimeException;
import sparrow.etl.core.log.SparrowLogger;
import sparrow.etl.core.log.SparrowrLoggerFactory;
import sparrow.etl.core.util.ConfigKeyConstants;
import sparrow.etl.core.util.SparrowUtil;


/**
 * 
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author not attributable
 * @version 1.0
 */
public class ZipWriter extends AbstractEOAWriter {

	protected String outFile = null;
	protected String[] files = null;
	protected boolean backupOnExist, conditionExist = false;
	private boolean preserveFile = false;

	private static final SparrowLogger logger = SparrowrLoggerFactory
			.getCurrentInstance(ZipWriter.class);

	/**
	 * 
	 * @param config
	 *            SparrowDataWriterConfig
	 */
	public ZipWriter(SparrowDataWriterConfig config) {
		super(config);
		SparrowUtil.validateParam(new String[] {
				ConfigKeyConstants.PARAM_FILE_NAME,
				ConfigKeyConstants.PARAM_FILE_LIST }, "ZipWriter", config
				.getInitParameter());
	}

	/**
	 * 
	 * @param config
	 * @param baseClass
	 */
	public ZipWriter(SparrowDataWriterConfig config, String baseClass) {
		super(config);
		SparrowUtil.validateParam(
				new String[] { ConfigKeyConstants.PARAM_FILE_NAME }, baseClass,
				config.getInitParameter());
	}

	/**
   *
   */
	public void initialize() {
		outFile = config.getInitParameter().getParameterValue(
				ConfigKeyConstants.PARAM_FILE_NAME);
		outFile = replaceToken(outFile);
		files = getFileNamesFromFileWriter();
		backupOnExist = SparrowUtil.performTernary(config.getInitParameter(),
				ConfigKeyConstants.PARAM_BACK_ON_EXIST, false);
		preserveFile = SparrowUtil.performTernary(config.getInitParameter(),
				ConfigKeyConstants.PARAM_PRESERVE_FILE, false);
		conditionExist = config.getInitParameter().isParameterExist(
				ConfigKeyConstants.PARAM_CONDITION);
		config.getContext().setAttribute(WRITER_NAME, outFile);
	}

	/**
   *
   */
	public void doEndOfProcess() {
		try {

			if (conditionExist) {
				boolean result = super.evaluateCondition(config
						.getInitParameter().getParameterValue(
								ConfigKeyConstants.PARAM_CONDITION));
				if(!result){
					return;
				}
			}

			SparrowUtil.doFileExistAction(outFile, backupOnExist);
			// Create the ZIP file
			ZipOutputStream out = new ZipOutputStream(new FileOutputStream(
					outFile));
			byte[] buf = new byte[1024];

			// Compress the files
			for (int i = 0; i < files.length; i++) {
				FileInputStream in = new FileInputStream(files[i]);
				logger.info("Adding file[" + files[i] + "] into Zip Archive.");
				// Add ZIP entry to output stream.
				out.putNextEntry(new ZipEntry(stripFilePath(files[i])));

				// Transfer bytes from the file to the ZIP file
				int len;
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
				out.closeEntry();
				in.close();
			}
			out.close();
			logger.info("Zip Archive [" + outFile + "] created.");
			handleFilePreservation();
		} catch (Exception e) {
			throw new SparrowRuntimeException(
					"Exception occured while compressing files", e);
		}
	}

	/**
   *
   */
	protected void handleFilePreservation() {
		if (!preserveFile) {
			logger.info("Removing original files.");
			for (int i = 0; i < files.length; i++) {
				new File(files[i]).delete();
			}
		}
	}

	
	/**
	 * 
	 * @param file
	 * @return
	 */
	private String stripFilePath(String file){
		String fSeperator = SparrowUtil.getFileSeperator(file);
		if(fSeperator==null){
			return file;
		}
		return file.substring(file.lastIndexOf(fSeperator)+1);
	}
	
}
