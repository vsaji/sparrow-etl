/**
 * 
 */
package sparrow.etl.impl.extractor;

import sparrow.etl.core.config.SparrowDataExtractorConfig;
import sparrow.etl.core.dao.impl.ColumnHeader;
import sparrow.etl.core.dao.impl.RecordSetImpl_Disconnected;
import sparrow.etl.core.dao.impl.ResultRow;
import sparrow.etl.core.dao.impl.ResultRowImpl;
import sparrow.etl.core.exception.DataException;
import sparrow.etl.core.extractor.DataExtractor;
import sparrow.etl.core.util.SparrowUtil;
import sparrow.etl.core.vo.DataHolder;

/**
 * @author vsaji
 * 
 */
public class VirtualDataExtractor implements DataExtractor {

	private int rowCount = 1;

	/**
	 * 
	 */
	public VirtualDataExtractor(SparrowDataExtractorConfig config) {
		this.rowCount = SparrowUtil.performTernary(config.getInitParameter(),
				"row.count", 1);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sparrow.elt.core.extractor.DataExtractor#destroy()
	 */
	public void destroy() throws DataException {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sparrow.elt.core.extractor.DataExtractor#initialize()
	 */
	public void initialize() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sparrow.elt.core.extractor.DataExtractor#loadData()
	 */
	public DataHolder loadData() throws DataException {
		RecordSetImpl_Disconnected rd = new RecordSetImpl_Disconnected();
		ColumnHeader ch = new ColumnHeader(new String[] { "COL1", "COL2",
				"COL3" });
		for (int i = 0; i < rowCount; i++) {
			ResultRow r = new ResultRowImpl(ch, new Object[] { "VAL1" + i,
					"VAL2" + i, "VAL3" + i });
			rd.addRow(r);
		}
		DataHolder dh = new DataHolder();
		dh.setData(rd);
		return dh;
	}
}
