package sparrow.elt.impl.services;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.text.SimpleDateFormat;
import java.util.Date;

import sparrow.elt.core.config.SparrowServiceConfig;
import sparrow.elt.core.exception.SemaphoreException;
import sparrow.elt.core.log.SparrowLogger;
import sparrow.elt.core.log.SparrowrLoggerFactory;
import sparrow.elt.core.util.ConfigKeyConstants;
import sparrow.elt.core.util.PIDUtil;
import sparrow.elt.core.util.SparrowUtil;


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
 * @author Saji Venugopalan
 * @version 1.0
 */
public class FileBasedSemaphoreService extends AbstractSemaphoreService {

	private final String LOCK_FILE;
	private String processName = null;

	private static final SparrowLogger logger = SparrowrLoggerFactory
			.getCurrentInstance(FileBasedSemaphoreService.class);

	/**
	 * 
	 * @param config
	 *            SparrowServiceConfig
	 */
	public FileBasedSemaphoreService(SparrowServiceConfig config) {
		super(config);

		SparrowUtil.validateParam(
				new String[] { ConfigKeyConstants.PARAM_FILE_PATH },
				"FileBasedSemaphoreService", config.getInitParameter());

		String filePath = config.getInitParameter().getParameterValue(
				ConfigKeyConstants.PARAM_FILE_PATH);
		this.processName = SparrowUtil.performTernary(config.getInitParameter(),
				"overridden.process.name", config.getContext().getProcessId());
		this.LOCK_FILE = filePath + "/" + processName + ".lock";

	}

	/**
	 * beginApplication
	 */
	public void acquireOnStart() throws SemaphoreException {
		logger.info("Checking SEMAPHORE Lock status");
		String processID = getProcessIDFromSemaphoreFile();
		if (processID == null || "".equals(processID)) {
			logger.info("Acquiring SEMAPHORE Lock [" + LOCK_FILE + "]");
			processID = acquire();
			logger.info("Acquired SEMAPHORE Lock for Process ID[" + processID
					+ "]");
		} else {
			String[] prcIdAndDate = processID.split("[~]");
			String exceptionString = "SPEAR Process ["
					+ config.getContext().getProcessId()
					+ "] is currently running and locked by Process Id ["
					+ prcIdAndDate[0] + "], Start Date/Time ["
					+ prcIdAndDate[1] + "]";

			throw new SemaphoreException("SEMAPHORE_BA_LOCKED", exceptionString);
		}
	}

	/**
	 * 
	 * @return boolean
	 */
	private String getProcessIDFromSemaphoreFile() {
		String processID = null;
		File lockFile = new File(LOCK_FILE);
		if (lockFile.exists()) {
			try {
				FileReader fr = new FileReader(lockFile);
				BufferedReader br = new BufferedReader(fr);
				processID = br.readLine();
				processID = (processID != null) ? processID.trim() : "";
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return processID;
	}

	/**
	 * 
	 * @return String
	 */
	private String acquire() throws SemaphoreException {
		File lockOnSemaphoreFile = new File(LOCK_FILE);
		FileChannel channel = null;
		FileLock lock = null;
		String processID = null;
		try {
			channel = new RandomAccessFile(lockOnSemaphoreFile, "rw")
					.getChannel();
			lockOnSemaphoreFile.createNewFile();
			try {
				lock = channel.tryLock();
			} catch (OverlappingFileLockException e) {
				// File is already locked in this thread or virtual machine
				throw new SemaphoreException("SEMAPHORE_UNKNOWN",
						"Unable to create Semaphore lock file [" + LOCK_FILE
								+ "][OverlappingFileLockException]["
								+ e.getMessage() + "]");
			}
			FileWriter writer = new FileWriter(lockOnSemaphoreFile);
			processID = PIDUtil.getPID();
			String lockDate = new SimpleDateFormat("EEE:yyyy-MM-dd:HH:mm:ss")
					.format(new Date());

			if (lock != null) {
				lock.release();
			}

			writer.write(processID + "~" + lockDate);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			System.out.println("Error acquiring lock: " + e.getMessage());
		} finally {
			try {
				channel.close();
			} catch (IOException e) {
			}
		}
		return processID;
	}

	/**
	 * endApplication
	 */
	public void releaseOnEnd() throws SemaphoreException {
		File lockOnSemaphoreFile = new File(LOCK_FILE);
		if (lockOnSemaphoreFile.exists()) {
			logger.info("Releasing SEMAPHORE Lock");
			lockOnSemaphoreFile.delete();
			logger.info("SEMAPHORE Lock File [" + LOCK_FILE + "] Deleted.");
		}
	}

	/**
	 * checkStatus
	 * 
	 * @return String
	 */
	public String checkStatus() {
		return "";
	}

	/**
	 * isProcessTerminationFlagOn
	 * 
	 * @return boolean
	 */
	public boolean isProcessTerminationFlagOn() {
		return !(new File(LOCK_FILE).exists());
	}

}
