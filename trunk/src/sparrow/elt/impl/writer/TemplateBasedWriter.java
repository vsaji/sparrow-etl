/**
 *
 */
package sparrow.elt.impl.writer;

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.LinkedList;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.runtime.RuntimeInstance;
import org.apache.velocity.runtime.parser.node.SimpleNode;

import sparrow.elt.core.config.SparrowDataWriterConfig;
import sparrow.elt.core.context.ContextVariables;
import sparrow.elt.core.exception.DataWriterException;
import sparrow.elt.core.exception.InitializationException;
import sparrow.elt.core.log.SparrowLogger;
import sparrow.elt.core.log.SparrowrLoggerFactory;
import sparrow.elt.core.util.ConfigKeyConstants;
import sparrow.elt.core.util.Constants;
import sparrow.elt.core.util.GenericTokenResolver;
import sparrow.elt.core.util.SparrowUtil;
import sparrow.elt.core.vo.DataOutputHolder;
import sparrow.elt.core.writer.AbstractDataWriter;


/**
 * @author syadav15
 * 
 */
public class TemplateBasedWriter extends AbstractDataWriter {

	private static final SparrowLogger logger = SparrowrLoggerFactory
			.getCurrentInstance(TemplateBasedWriter.class);

	private RuntimeInstance ri = new RuntimeInstance();

	private SparrowDataWriterConfig config;

	private SimpleNode nodeTree;

	private LinkedList outputRecs;

	private String outFileName;

	private int numOfRows = 0;

	private boolean createZeroCountFile = false;

	private String reportType;

	/**
	 * @param config
	 */
	public TemplateBasedWriter(SparrowDataWriterConfig config) {
		super(config);
		this.config = config;
		SparrowUtil.validateParam(new String[] {
				ConfigKeyConstants.PARAM_TMPLT_FILE,
				ConfigKeyConstants.PARAM_FILE_NAME }, "TemplateBasedWriter",
				config.getInitParameter());
		this.numOfRows = SparrowUtil.performTernary(config.getInitParameter(),
				ConfigKeyConstants.PARAM_MAX_ROW, 0);
		this.outFileName = config.getInitParameter().getParameterValue(
				ConfigKeyConstants.PARAM_FILE_NAME);
		this.reportType = config.getInitParameter().getParameterValue(
				ConfigKeyConstants.PARAM_MAX_ROW_EXCEED);
		if (null != reportType) {
			SparrowUtil
					.validateParam(
							new String[] { ConfigKeyConstants.PARAM_CSV_TEMPLATE_FILE },
							"TemplateBasedWriter", config.getInitParameter());
		}

		this.createZeroCountFile = SparrowUtil.performTernary(config
				.getInitParameter(),
				ConfigKeyConstants.PARAM_CREATE_ZEROCOUNT_FILE, false);

		outFileName = replaceToken(outFileName);
		getContext().setAttribute(WRITER_NAME, outFileName);
		logger.info("Output file to be created [" + outFileName + "]. ");

	}

	/**
	 * 
	 */
	public void initialize() {
		this.outputRecs = new LinkedList();
	}

	/**
	 * 
	 */
	public void initializeTemplate(String tmpltFile) {
		Reader r = null;
		try {
			r = new FileReader(tmpltFile);
			nodeTree = ri.parse(r, "spearexcelwriter");

			if (logger.isDebugEnabled()) {
				logger.debug("Velocity Template [" + tmpltFile
						+ "] has been initialized.");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new InitializationException("EXCELWRITER_INIT_EXP",
					"Exception occured while reading the template file ["
							+ tmpltFile + "]");

		} finally {
			try {
				if (null != r) {
					r.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			r = null;

		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sparrow.elt.core.writer.AbstractDataWriter#writeData(sparrow.elt.core.vo.DataOutputHolder,
	 *      int)
	 */
	public int writeData(DataOutputHolder data, int statusCode)
			throws DataWriterException {
		Object value = data.getObject(KEY_NAME);
		if (value != null) {
			outputRecs.add(value);
		}
		return STATUS_SUCCESS;
	}

	/**
	 * 
	 * @param flag
	 *            int
	 */
	public void endOfProcess(int flag) {
		if (Constants.EP_END_APP == flag) {
			doEndOfProcess();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sparrow.elt.impl.writer.AbstractEOAWriter#doEndOfProcess()
	 */
	public void doEndOfProcess() {
		Writer wr = null;
		String tmpltFile;

		int outRecsSize = outputRecs.size();

		getContext().setAttribute(WRITER_NAME + ContextVariables.RECORD_COUNT,
				new Integer(outRecsSize));

		VelocityContext vars = new VelocityContext(getContext().getAttributes());
		vars.put("_util", GenericTokenResolver.getInstance(getContext()));
		try {

			// Zero Count File
			if (outRecsSize == 0 && createZeroCountFile == true) {

				tmpltFile = replaceToken(config.getInitParameter()
						.getParameterValue(ConfigKeyConstants.PARAM_TMPLT_FILE));
				initializeTemplate(tmpltFile);

				wr = new BufferedWriter(new FileWriter(outFileName));
				ri.render(vars, wr, "spearexcelwriter", nodeTree);
				wr.flush();
				logger.info("New Output file [" + outFileName
						+ "] has been created from template [" + tmpltFile
						+ "] ");
			}

			// Creates one Excel Report
			else if (numOfRows >= outRecsSize) {

				tmpltFile = replaceToken(config.getInitParameter()
						.getParameterValue(ConfigKeyConstants.PARAM_TMPLT_FILE));
				initializeTemplate(tmpltFile);

				wr = new BufferedWriter(new FileWriter(outFileName));
				vars.put("_records", outputRecs);
				ri.render(vars, wr, "spearcsvwriter", nodeTree);
				wr.flush();
				logger.info("New Output file [" + outFileName
						+ "] has been created from template [" + tmpltFile
						+ "] ");
			}

			// Creates more than one Excel Report
			else if (numOfRows < outRecsSize && reportType == null) {

				tmpltFile = replaceToken(config.getInitParameter()
						.getParameterValue(ConfigKeyConstants.PARAM_TMPLT_FILE));
				initializeTemplate(tmpltFile);

				int numOfFiles = calcNumberOfFile(outRecsSize);

				LinkedList[] colls = null;
				String postAppendfileName[] = null;
				if (numOfFiles != 0) {
					colls = new LinkedList[numOfFiles];
					postAppendfileName = new String[numOfFiles];
					/** ********************************************************************* */
					if (numOfFiles == 1) {
						colls[0] = outputRecs;
						postAppendfileName[0] = outFileName;
					} else {
						String filePath = outFileName.substring(0, outFileName
								.lastIndexOf("/"));
						String fileName = outFileName.substring(outFileName
								.lastIndexOf("/") + 1);
						String fileNamePart[] = fileName.split("[.]");

						for (int i = 0; i < numOfFiles; i++) {
							colls[i] = new LinkedList();
							postAppendfileName[i] = filePath + "/"
									+ fileNamePart[0] + "" + i + "."
									+ fileNamePart[1];
							if (i == numOfFiles - 1) {
								colls[i] = outputRecs;
							} else {
								fill(colls[i]);
							}
						}
					}
				}
				/** ******************************************************************** */
				for (int i = 0; i < numOfFiles; i++) {
					wr = new BufferedWriter(new FileWriter(
							postAppendfileName[i]));
					vars.put("_records", colls[i]);
					ri.render(vars, wr, "spearexcelwriter", nodeTree);
					wr.flush();
					logger.info("New Output file [" + postAppendfileName[i]
							+ "] has been created from template [" + tmpltFile
							+ "] ");

				}
			}
			
				// Creates CSV Report
			 else if (numOfRows < outRecsSize
					&& "switch.to.csv".equalsIgnoreCase(reportType)) {

				tmpltFile = replaceToken(config.getInitParameter()
						.getParameterValue(
								ConfigKeyConstants.PARAM_CSV_TEMPLATE_FILE));
				initializeTemplate(tmpltFile);

				wr = new BufferedWriter(new FileWriter(outFileName));
				vars.put("_records", outputRecs);
				ri.render(vars, wr, "spearcsvwriter", nodeTree);
				wr.flush();
				logger.info("New Output file [" + outFileName
						+ "] has been created from template [" + tmpltFile
						+ "] ");
			}

		} catch (IOException e) {
			e.printStackTrace();

		} finally {
			try {
				if (null != wr) {
					wr.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * 
	 * @return int
	 */
	private int calcNumberOfFile(int outRecsSize) {
		int numOfFiles = 1;
		if (numOfRows != 0) {
			numOfFiles = (int) Math.round(outRecsSize / numOfRows);
			if (outRecsSize % numOfRows != 0) {
				++numOfFiles;
			}
		}
		logger.info("Expected number of output files [" + numOfFiles + "]");
		return numOfFiles;
	}

	/**
	 * 
	 * @param list
	 */
	private void fill(LinkedList list) {
		for (int i = 0; i < numOfRows; i++) {
			list.add(outputRecs.removeFirst());
		}
	}
}
