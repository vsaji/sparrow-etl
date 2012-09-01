package sparrow.etl.core.util;

import java.util.HashMap;

import sparrow.etl.core.config.ConfigParam;


/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */
/**
 * @author vsaji
 * 
 */
public class FileProcessInfo {

	private final String fileSourceDir;
	private final String failDir;
	private final char fileDelimiter;
	private final String filePattern;
	private final String fileProcessor;
	private final boolean validationRequired;
	private final String rejectionReportType;
	private final String rejectionReportFileName;
	private final String rejectionReportSource;
	private final String postProcess;
	private final String name;
	private final String postProcessDir;
	private final boolean waitForFile;
	private final long pollingInterval;
	private final int pollingCount;
	private final int startLineNumber;
	private final int fetchSize;
	private final String resultWrap;
	private final ConfigParam param;
	private String columnLengthDefinition = null;
	private final boolean fixedLengthFile;
	private final String columnDefinitionValue;
	private final boolean trimValue;
	private final boolean ignoreColMismatch;
	private String rejectionReportFile = null;
	private boolean rejectionReportInfoExist = false;

	private static final HashMap DELIMITER_MAP = new HashMap() {
		{
			put("TAB", "\t");
			put("COMMA", ",");
			put("PIPE", "|");
			put("SEMICOLON", ";");
			put("COLON", ":");
			put("TILDE", "~");
			put("FIXEDLENGTH", "@");
		}
	};

	/**
	 * 
	 * @param param
	 *            ConfigParam
	 */
	public FileProcessInfo(String name, ConfigParam param) {

		SparrowUtil.validateParam(new String[] {
				ConfigKeyConstants.PARAM_FILE_PATH,
				ConfigKeyConstants.PARAM_FILE_DELIMITER,
				ConfigKeyConstants.PARAM_FILE_NAME,
				ConfigKeyConstants.PARAM_COLUMN_DEF,
				ConfigKeyConstants.PARAM_START_LINE_NUM }, "CSVDataProvider",
				param);

		this.param = param;
		this.name = name;

		this.fileSourceDir = param
				.getParameterValue(ConfigKeyConstants.PARAM_FILE_PATH);
		String temp = param
				.getParameterValue(ConfigKeyConstants.PARAM_FILE_DELIMITER);
		if (temp.equalsIgnoreCase("fixedlength")) {
			columnLengthDefinition = param
					.getParameterValue(ConfigKeyConstants.PARAM_COLUMN_LEN_DEF);
			this.fixedLengthFile = true;
			this.fileDelimiter = '@';
		} else {
			temp = DELIMITER_MAP.containsKey(temp.toUpperCase()) ? DELIMITER_MAP
					.get(temp.toUpperCase()).toString()
					: temp;
			this.fileDelimiter = temp.charAt(0);

			this.fixedLengthFile = false;
		}

		this.filePattern = param
				.getParameterValue(ConfigKeyConstants.PARAM_FILE_NAME);

		this.columnDefinitionValue = param
				.getParameterValue(ConfigKeyConstants.PARAM_COLUMN_DEF);

		this.fileProcessor = SparrowUtil.performTernary(param,
				ConfigKeyConstants.PARAM_FILE_PROCESSOR, (String) SparrowUtil
						.getImplConfig("fileprocessor").get("CSV"));

		this.postProcess = SparrowUtil.performTernary(param,
				ConfigKeyConstants.PARAM_POST_PROCESS, Constants.IGNORE);

		this.postProcessDir = param
				.getParameterValue(ConfigKeyConstants.PARAM_POST_PROCESS_DIR);

		this.validationRequired = SparrowUtil.performTernary(param,
				ConfigKeyConstants.PARAM_VALIDATION_RQD, false);

		if (validationRequired) {
			SparrowUtil.validateParam(
					new String[] { ConfigKeyConstants.PARAM_FAIL_DIR },
					"CSVDataProvider", param);
		}

		this.rejectionReportType = SparrowUtil.performTernary(param,
				ConfigKeyConstants.PARAM_REJECTION_REPORT_TYPE, null);

		this.rejectionReportSource = param
				.getParameterValue(ConfigKeyConstants.PARAM_REJECTION_REPORT_SRC);

		this.rejectionReportFileName = param
				.getParameterValue(ConfigKeyConstants.PARAM_REJECTION_REPORT_FILE);

		
		if (rejectionReportFileName != null && rejectionReportSource == null) {
			SparrowUtil.validateParam(new String[] {
					ConfigKeyConstants.PARAM_REJECTION_REPORT_FILE,
					ConfigKeyConstants.PARAM_REJECTION_REPORT_SRC },
					"CSVDataProvider", param);
		} else if (rejectionReportFileName != null
				&& rejectionReportSource != null) {
			this.rejectionReportInfoExist = true;
			this.rejectionReportFile = rejectionReportSource + "/"
					+ rejectionReportFileName;
		}

		this.waitForFile = SparrowUtil.performTernary(param,
				ConfigKeyConstants.PARAM_WAIT_4_FILE, false);

		this.ignoreColMismatch = SparrowUtil.performTernary(param,
				ConfigKeyConstants.PARAM_IGN_COLLEN_MM, false);

		this.pollingInterval = SparrowUtil.performTernaryForLong(param,
				ConfigKeyConstants.PARAM_POLLING_INTERVAL, 0);

		this.pollingCount = SparrowUtil.performTernary(param,
				ConfigKeyConstants.PARAM_POLLING_COUNT, 0);

		this.startLineNumber = SparrowUtil.performTernary(param,
				ConfigKeyConstants.PARAM_START_LINE_NUM, -1);

		this.fetchSize = SparrowUtil.performTernary(param,
				ConfigKeyConstants.PARAM_FETCH_SIZE, 0);

		this.resultWrap = SparrowUtil.performTernary(param,
				ConfigKeyConstants.PARAM_RESULT_WRAP,
				Constants.RESULT_WRAP_CONNECTED);

		this.failDir = param
				.getParameterValue(ConfigKeyConstants.PARAM_FAIL_DIR);

		this.trimValue = SparrowUtil.performTernary(param,
				ConfigKeyConstants.PARAM_TRIM_VALUE, false);
	}

	public int getPollingCount() {
		return pollingCount;
	}

	public String getFileSourceDir() {
		return fileSourceDir;
	}

	public String getRejectionReportType() {
		return rejectionReportType;
	}

	public String getResultWrap() {
		return resultWrap;
	}

	public boolean isValidationRequired() {
		return validationRequired;
	}

	public int getFetchSize() {
		return fetchSize;
	}

	public String getFileProcessor() {
		return fileProcessor;
	}

	public char getFileDelimiter() {
		return fileDelimiter;
	}

	public long getPollingInterval() {
		return pollingInterval;
	}

	public String getFilePattern() {
		return filePattern;
	}

	public String getRejectionReportSource() {
		return rejectionReportSource;
	}

	public int getStartLineNumber() {
		return startLineNumber;
	}

	public boolean isWaitForFile() {
		return waitForFile;
	}

	public ConfigParam getParam() {
		return param;
	}

	public String getFailDir() {
		return failDir;
	}

	public String getColumnLengthDefinition() {
		return columnLengthDefinition;
	}

	public boolean isFixedLengthFile() {
		return fixedLengthFile;
	}

	public String getPostProcessDir() {
		return postProcessDir;
	}

	public String getPostProcess() {
		return postProcess;
	}

	public String getName() {
		return name;
	}

	public String getColumnDefinitionValue() {
		return columnDefinitionValue;
	}

	public boolean isTrimValue() {
		return trimValue;
	}

	public boolean isIgnoreColMismatch() {
		return ignoreColMismatch;
	}

	public String getRejectionReportFileName() {
		return rejectionReportFileName;
	}

	public boolean isRejectionReportInfoExist() {
		return rejectionReportInfoExist;
	}

	public String getRejectionReportFile() {
		return rejectionReportFile;
	}
		
}
