package sparrow.elt.core.util;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.Date;

import sparrow.elt.core.context.ContextVariables;
import sparrow.elt.core.context.SparrowContext;
import sparrow.elt.core.dao.impl.ColumnHeader;
import sparrow.elt.core.exception.DataException;
import sparrow.elt.core.log.SparrowLogger;
import sparrow.elt.core.log.SparrowrLoggerFactory;
import sparrow.elt.core.report.FileReporter;
import sparrow.elt.core.report.RejectedEntry;


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
public abstract class FileProcessor {

	protected static final SparrowLogger logger = SparrowrLoggerFactory
			.getCurrentInstance(FileProcessor.class);
	protected final FileProcessInfo fileProcessInfo;
	protected final SparrowContext context;

	protected AsyncRequestProcessor arp = null;

	private GenericTokenResolver fileNameResolver;

	/**
	 * 
	 * @param info
	 *            FileProcessInfo
	 */
	protected FileProcessor(SparrowContext context, FileProcessInfo info) {
		this.context = context;
		fileNameResolver = getFileNameResolver();
		fileProcessInfo = info;
		String errorReportType = info.getRejectionReportType();

		if (Constants.TYPE_FILE.equals(errorReportType)) {

			String outputPattern = "[${" + RejectedEntry.REPORT_SOURCE_NAME
					+ "}][${" + RejectedEntry.REJECT_REASON + "}][${"
					+ RejectedEntry.REJECTED_ENTRY + "}]\n";

			String fileName = (info.isRejectionReportInfoExist()) ? SparrowUtil
					.evaluateAndReplace(info.getRejectionReportFile(),fileNameResolver) : SparrowUtil
					.constructOutputFileName(info.getRejectionReportSource(),
							info.getName(), ".error");

			arp = AsyncRequestProcessor
					.createAsynchProcessor(Constants.REJECTION_SERVICE);
			arp.registerListener(info.getName(), new FileReporter(fileName,
					outputPattern));
			arp.start();

			context.setAttribute(info.getName()
					+ ContextVariables.CSV_ROW_REJECT_FILE, fileName);
		}
	}

	/**
	 * 
	 * @return FileProcessInfo
	 */
	public final FileProcessInfo getFileProcessInfo() {
		return fileProcessInfo;
	}

	/**
	 * 
	 * @param files
	 *            File[]
	 * @return File[]
	 */
	public File[] sort(File[] files) throws IOException {
		return files;
	}

	/**
	 * 
	 * @return Resolver
	 */
	public GenericTokenResolver getFileNameResolver() {
		return GenericTokenResolver.getInstance(context);
	}

	/**
	 * 
	 * @param row
	 *            String
	 * @param lineNumber
	 *            int
	 * @throws Exception
	 */
	protected void reportRejection(String requestFor, RejectedEntry re)
			throws Exception {
		if (arp != null) {
			arp.process(requestFor, re);
		}
	}

	/**
	 * 
	 * @param file
	 *            File
	 * @return String[]
	 */
	public abstract boolean validate(File file) throws IOException;

	public abstract void setReader(Reader r) throws Exception;

	public abstract ColumnHeader getHeader() throws DataException;

	public abstract Object[] getRow() throws DataException;

	public abstract Object getUnprocessedRow() throws DataException;

	public abstract void close() throws Exception;

}
