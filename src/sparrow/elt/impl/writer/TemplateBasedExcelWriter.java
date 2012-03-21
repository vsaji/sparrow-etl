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
import java.util.ArrayList;
import java.util.List;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.runtime.RuntimeInstance;
import org.apache.velocity.runtime.parser.node.SimpleNode;

import sparrow.elt.core.config.SparrowDataWriterConfig;
import sparrow.elt.core.exception.DataWriterException;
import sparrow.elt.core.exception.InitializationException;
import sparrow.elt.core.util.ConfigKeyConstants;
import sparrow.elt.core.util.Constants;
import sparrow.elt.core.util.SparrowUtil;
import sparrow.elt.core.vo.DataOutputHolder;
import sparrow.elt.core.writer.AbstractDataWriter;


/**
 * @author syadav15
 * 
 */
public class TemplateBasedExcelWriter extends AbstractDataWriter {

	private RuntimeInstance ri = new RuntimeInstance();

	private SparrowDataWriterConfig config;

	private SimpleNode nodeTree;

	private List outputRecs;

	private String tmpltFile;
	
	private int numOfRows =0;

	/**
	 * @param config
	 */
	public TemplateBasedExcelWriter(SparrowDataWriterConfig config) {
		super(config);
		this.config = config;
		SparrowUtil.validateParam(new String[] {
				ConfigKeyConstants.PARAM_TMPLT_FILE,
				ConfigKeyConstants.PARAM_FILE_NAME },
				"TemplateBasedExcelWriter", config.getInitParameter());
		this.outputRecs = new ArrayList();
		this.numOfRows = SparrowUtil.performTernary(config.getInitParameter(), ConfigKeyConstants.PARAM_MAX_ROW, 0);
	}

	/**
	 * 
	 */
	public void initialize() {
		Reader r = null;

		try {
			tmpltFile = replaceToken(config.getInitParameter()
					.getParameterValue(ConfigKeyConstants.PARAM_TMPLT_FILE));

			r = new FileReader(tmpltFile);
			nodeTree = ri.parse(r, "sparrowexcelwriter");

		} catch (Exception ex) {
			ex.printStackTrace();
			throw new InitializationException("EXCELWRITER_INIT_EXP",
					"Exception occured while reading the template file ["
							+ tmpltFile + "]");

		} finally {
			try {
				r.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			r = null;
		}
	}

	/**
	 * 
	 */
	public int writeData(DataOutputHolder data, int statusCode)
			throws DataWriterException {
		outputRecs.add(data.getObject(KEY_NAME));
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

		String outFileName = config.getInitParameter().getParameterValue(
				ConfigKeyConstants.PARAM_FILE_NAME);
	
		int numOfFiles = (int) Math.round(outputRecs.size() / numOfRows);

		if (outputRecs.size() % numOfRows != 0) {
			++numOfFiles;
		}

		List[] colls = new ArrayList[numOfFiles];
		
		String filePath = outFileName
				.substring(0, outFileName.lastIndexOf("/"));
		String fileName = outFileName
				.substring(outFileName.lastIndexOf("/") + 1);
		String fileNamePart[] = fileName.split("[.]");
		String postAppendfileName[] = new String[numOfFiles];

		/** ******************************************************************** */
		
		for (int i = 0; i < numOfFiles; i++) {
			colls[i] = new ArrayList(numOfRows);
			postAppendfileName[i] = filePath + "/" + fileNamePart[0] + "" + i
					+ "." + fileNamePart[1];
			fill(colls[i]);

			if (numOfRows >= outputRecs.size()) {
				colls[++i] = outputRecs;
				postAppendfileName[i] = filePath + "/" + fileNamePart[0] + ""
						+ i + "." + fileNamePart[1];
				break;
			} 
		}
		/** ******************************************************************** */
		try {
			
			if (numOfFiles == 1) {
				postAppendfileName[0] = outFileName;
			}
			
			VelocityContext vars = new VelocityContext();
			
			for (int i = 0; i < numOfFiles; i++) {
				wr = new BufferedWriter(new FileWriter(postAppendfileName[i]));
				vars.put("_records", colls[i]);
				ri.render(vars, wr, "sparrowexcelwriter", nodeTree);
				wr.flush();
			}
			
		} catch (IOException ex) {
			ex.printStackTrace();
		
		} finally {
			try {
				wr.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * 
	 * @param list
	 */
	private void fill(List list) {
		for (int i = 0; i < numOfRows; i++) {
			list.add(outputRecs.remove(0));
		}
	}

}
