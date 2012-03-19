/**
 * 
 */
package sparrow.elt.impl.extractor;

import sparrow.elt.core.config.SparrowDataExtractorConfig;
import sparrow.elt.core.dao.impl.ColumnHeader;
import sparrow.elt.core.dao.impl.RecordSetImpl_Disconnected;
import sparrow.elt.core.dao.impl.ResultRow;
import sparrow.elt.core.dao.impl.ResultRowImpl;
import sparrow.elt.core.exception.DataException;
import sparrow.elt.core.extractor.DataExtractor;
import sparrow.elt.core.util.SparrowUtil;
import sparrow.elt.core.vo.DataHolder;

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
