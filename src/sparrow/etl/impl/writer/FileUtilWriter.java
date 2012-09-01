package sparrow.etl.impl.writer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.channels.FileChannel;

import java.io.PrintWriter;

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
public class FileUtilWriter extends AbstractEOAWriter {

	private static final SparrowLogger logger = SparrowrLoggerFactory
			.getCurrentInstance(FileUtilWriter.class);

	private static final int RENAME = 1;
	private static final int DELETE = 2;
	private static final int CREATE_FILE = 3;
	private static final int COPY_FILE = 4;
	private static final int MOVE_FILE = 5;
	private static final int CREATE_DIR = 7;
	private static final int COPY_DIR = 8;

	private static final String _RENAME_FILE = "rename";
	private static final String _DELETE_FILE = "delete";
	private static final String _CREATE_FILE = "create.file";
	private static final String _COPY_FILE = "copy.file";
	private static final String _CREATE_DIR = "create.dir";
	private static final String _COPY_DIR = "copy.dir";
	private static final String _MOVE_FILE = "move.file";

	private static final String FILEWITER = "FileUtilWriter";

	private int ACTION = 0;

	private String fileName = null;
	private String fromName = null;
	private String toName = null;
	private String dirName = null;
	private String fromDir = null;
	private String toDir = null;

	/**
	 * 
	 * @param config
	 *            SparrowDataWriterConfig
	 */
	public FileUtilWriter(SparrowDataWriterConfig config) {
		super(config);
		SparrowUtil.validateParam(
				new String[] { ConfigKeyConstants.PARAM_ACTION }, FILEWITER,
				config.getInitParameter());
		String action = config.getInitParameter().getParameterValue(
				ConfigKeyConstants.PARAM_ACTION);

		fileName = checkAndReplace(ConfigKeyConstants.PARAM_FILE_NAME);
		fromName = checkAndReplace(ConfigKeyConstants.PARAM_FROM_NAME);
		toName = checkAndReplace(ConfigKeyConstants.PARAM_TO_NAME);
		dirName = checkAndReplace(ConfigKeyConstants.PARAM_DIR_NAME);
		fromDir = checkAndReplace(ConfigKeyConstants.PARAM_FROM_DIR);
		toDir = checkAndReplace(ConfigKeyConstants.PARAM_TO_DIR);

		if (_RENAME_FILE.equals(action)) {
			SparrowUtil.validateParam(new String[] {
					ConfigKeyConstants.PARAM_FROM_NAME,
					ConfigKeyConstants.PARAM_TO_NAME }, FILEWITER, config
					.getInitParameter());
			ACTION = RENAME;
			config.getContext().setAttribute(WRITER_NAME, toName);
		}

		else if (_DELETE_FILE.equals(action)) {
			SparrowUtil.validateParam(
					new String[] { ConfigKeyConstants.PARAM_FILE_NAME },
					FILEWITER, config.getInitParameter());
			ACTION = DELETE;
		}

		else if (_CREATE_FILE.equals(action)) {
			SparrowUtil.validateParam(
					new String[] { ConfigKeyConstants.PARAM_FILE_NAME },
					FILEWITER, config.getInitParameter());
			ACTION = CREATE_FILE;
			config.getContext().setAttribute(WRITER_NAME, fileName);
		}

		else if (_COPY_FILE.equals(action)) {
			SparrowUtil.validateParam(new String[] {
					ConfigKeyConstants.PARAM_FROM_NAME,
					ConfigKeyConstants.PARAM_TO_NAME }, FILEWITER, config
					.getInitParameter());
			ACTION = COPY_FILE;
			config.getContext().setAttribute(WRITER_NAME, toName);
		} else if (_MOVE_FILE.equals(action)) {
			SparrowUtil.validateParam(new String[] {
					ConfigKeyConstants.PARAM_FROM_NAME,
					ConfigKeyConstants.PARAM_TO_NAME }, FILEWITER, config
					.getInitParameter());
			ACTION = MOVE_FILE;
			config.getContext().setAttribute(WRITER_NAME, toName);
		} else if (_COPY_DIR.equals(action)) {
			SparrowUtil.validateParam(new String[] {
					ConfigKeyConstants.PARAM_FROM_DIR,
					ConfigKeyConstants.PARAM_TO_DIR }, FILEWITER, config
					.getInitParameter());
			ACTION = COPY_DIR;
		} else if (_CREATE_DIR.equals(action)) {
			SparrowUtil.validateParam(
					new String[] { ConfigKeyConstants.PARAM_DIR_NAME },
					FILEWITER, config.getInitParameter());
			ACTION = CREATE_DIR;
		} else {
			throw new SparrowRuntimeException("Undefined action type [" + action
					+ "]");
		}

	}

	/**
	 * doEndOfProcess
	 */
	public void doEndOfProcess() {

		String condition = config.getInitParameter().getParameterValue(
				"condition");
		boolean conditionResult = false;

		if (condition != null) {
			conditionResult = super.evaluateCondition(condition);
		}

		switch (ACTION) {
		case RENAME:

			if (condition != null) {
				if (conditionResult) {
					new File(fromName).renameTo(new File(toName));
					logger.info("File/Dir renamed FROM[" + fromName
							+ "] -> TO[" + toName + "]");
				}
			} else {
				new File(fromName).renameTo(new File(toName));
				logger.info("File/Dir renamed FROM[" + fromName + "] -> TO["
						+ toName + "]");
			}
			break;

		case DELETE:
			if (condition != null) {
				if (conditionResult) {
					new File(fileName).delete();
				}
			} else {
				new File(fileName).delete();
			}
			break;

		case CREATE_FILE:
			if (condition != null) {
				if (conditionResult) {
					createFile();
				}
			} else {
				createFile();
			}
			break;
		case COPY_FILE:
			if (condition != null) {
				if (conditionResult) {
					copyFile(fromName, toName);
					logger.info("File content has been copied FROM[" + fromName
							+ "] -> TO[" + toName + "]");

				}
			} else {
				copyFile(fromName, toName);
				logger.info("File content has been copied FROM[" + fromName
						+ "] -> TO[" + toName + "]");

			}

			break;
		case MOVE_FILE:
			if (condition != null) {
				if (conditionResult) {
					new File(fromName).renameTo(new File(toName));
					logger.info("File has been moved FROM[" + fromName
							+ "] -> TO[" + toName + "]");
				}
			} else {
				new File(fromName).renameTo(new File(toName));
				logger.info("File has been moved FROM[" + fromName + "] -> TO["
						+ toName + "]");
			}
			break;
		case COPY_DIR:
			if (condition != null) {
				if (conditionResult) {
					copyDir(fromDir, toDir);
				}
			} else {
				copyDir(fromDir, toDir);
			}
			logger.info("File/Dir(s) has been copied FROM [" + fromDir
					+ "] -> TO [" + toDir + "]");
			break;
		case CREATE_DIR:
			if (condition != null) {
				if (conditionResult) {
					new File(dirName).mkdir();
					logger
							.info("Direcotry [" + dirName
									+ "] has been created.");
				}
			} else {
				new File(dirName).mkdir();
				logger.info("Direcotry [" + dirName + "] has been created.");
			}
			break;
		default:
			logger.error("Action [" + ACTION + "] not recognized");
			break;
		}
	}

	/**
	 * 
	 */
	private void createFile() {
		try {
			File cF = new File(fileName);
			cF.createNewFile();

			if (config.getInitParameter().isParameterExist("file.content")) {
				PrintWriter writer = new PrintWriter(new FileOutputStream(cF));
				String content = config.getInitParameter().getParameterValue(
						"file.content");
				writer.write(replaceToken(content));
				writer.flush();
				writer.close();
				writer = null;
			}
		} catch (IOException ex) {
			throw new SparrowRuntimeException("File could not be created ["
					+ fileName + "]", ex);
		}
		logger.info("File [" + fileName + "] has been created.");
	}

	/**
	 * 
	 * @param fromDir
	 *            String
	 * @param toDir
	 *            String
	 */
	private void copyDir(String fromDir, String toDir) {

		File srcPath = new File(fromDir);
		File dstPath = new File(toDir);

		if (srcPath.isDirectory()) {

			if (!dstPath.exists()) {

				dstPath.mkdir();

			}

			String files[] = srcPath.list();

			for (int i = 0; i < files.length; i++) {
				copyDir(srcPath + "/" + files[i], dstPath + "/" + files[i]);

			}
		} else {
			if (!srcPath.exists()) {
				throw new SparrowRuntimeException(
						"Dirty read encountered. Source file[" + fromDir
								+ "] not exisit");
			} else {
				copyFile(fromDir, toDir);
			}
		}

	}

	/**
	 * 
	 * @param fromName
	 *            String
	 * @param toName
	 *            String
	 */
	private void copyFile(String fromName, String toName) {

		final String fileName = fromName
				.substring(fromName.lastIndexOf("/") + 1);
		if (fileName.indexOf("*") != -1) {
			File target = new File(toName);

			if (target.exists()) {
				if (!target.isDirectory()) {
					throw new SparrowRuntimeException(
							"File Copy operation filed. Target directory does not exist or is not a direcotry. Target["
									+ target + "]");
				} else {
					String sourceDir = fromName.substring(0, fromName
							.lastIndexOf("/"));
					File source = new File(sourceDir);

					String fles[] = source.list(new FilenameFilter() {
						public boolean accept(File dir, String name) {
							return SparrowUtil.matchString(fileName, name);
						}
					});

					for (int i = 0; i < fles.length; i++) {
						logger.info("Copying [" + sourceDir + "/" + fles[i]
								+ "] -> [" + toName + "/" + fles[i] + "]");
						copyFile(sourceDir + "/" + fles[i], toName + "/"
								+ fles[i]);
					}
					return;
				}
			} else {
				throw new SparrowRuntimeException(
						"File Copy operation filed. Target directory/file does not exist. Target["
								+ target + "]");
			}

		}

		FileChannel inChannel = null;
		FileChannel outChannel = null;
		try {
			inChannel = new FileInputStream(fromName).getChannel();
			outChannel = new FileOutputStream(toName).getChannel();

			inChannel.transferTo(0, inChannel.size(), outChannel);
		} catch (IOException e) {
			throw new SparrowRuntimeException("File could not be copied ["
					+ fromName + "]", e);
		} finally {
			try {
				if (inChannel != null) {
					inChannel.close();
				}
				if (outChannel != null) {
					outChannel.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * 
	 * @param paramName
	 *            String
	 * @return String
	 */
	private String checkAndReplace(String paramName) {
		String fileName = config.getInitParameter()
				.getParameterValue(paramName);
		fileName = (fileName != null) ? getFileNameFromFileWriter(fileName)
				: fileName;
		return fileName;
	}
}
