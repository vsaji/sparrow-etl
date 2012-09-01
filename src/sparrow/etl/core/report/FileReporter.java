package sparrow.etl.core.report;

import java.io.FileOutputStream;
import java.io.IOException;

import sparrow.etl.core.log.SparrowLogger;
import sparrow.etl.core.log.SparrowrLoggerFactory;
import sparrow.etl.core.util.Constants;
import sparrow.etl.core.util.RequestListener;
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
 * @author Saji Venugopalan
 * @version 1.0
 */
public class FileReporter implements RequestListener {

	private boolean fileCreated = false;
	private FileOutputStream fos = null;
	private final String fileName;
	private final String pattern;

	/**
   *
   */
	private static final SparrowLogger logger = SparrowrLoggerFactory
			.getCurrentInstance(FileReporter.class);

	/**
	 * 
	 * @param fileName
	 *            String
	 * @param pattern
	 *            String
	 */
	public FileReporter(String fileName, String pattern) {
		this.fileName = fileName;
		this.pattern = pattern;
	}

	/**
	 * reportError
	 * 
	 * @param rr
	 *            RejectedEntry
	 */
	public void process(Object o) throws Exception {

		RejectedEntry rr = (RejectedEntry) o;
		if (fileCreated) {
			String entry = rr.getEntry().get(rr.REJECTED_ENTRY).toString()
					+ "\n";
			entry = SparrowUtil.replaceTokens(pattern, rr.getEntry(),
					Constants.TOKEN_START, Constants.TOKEN_END);
			fos.write(entry.getBytes());
			rr.close();
			rr = null;
		} else {
			fos = new FileOutputStream(this.fileName, true);
			fileCreated = true;
			process(rr);
		}
	}

	/**
	 * close
	 */
	public void endProcess() {
		try {
			if (fos != null) {
				fos.close();
				fos = null;
			}
		} catch (IOException ex) {
			logger.error("Exception occured while closing fos", ex);
		}

	}

}
