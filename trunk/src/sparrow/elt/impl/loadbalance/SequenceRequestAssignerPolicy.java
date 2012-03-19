package sparrow.elt.impl.loadbalance;

import java.util.Map;

import sparrow.elt.core.config.SparrowConfig;
import sparrow.elt.core.dao.impl.ResultRow;
import sparrow.elt.core.fifo.FIFO;
import sparrow.elt.core.util.ConfigKeyConstants;
import sparrow.elt.core.util.SparrowUtil;
import sparrow.elt.core.vo.SparrowResultHolder;


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
public class SequenceRequestAssignerPolicy extends
		AbstractRequestAssignerPolicy {

	private final String[] colNames;
	private final SimpleHashCodeResolver shcr;
	private static final int DEFAULT_HASH_CODE = 17071979;

	/**
	 * 
	 * @param fifos
	 *            Map
	 * @param config
	 *            SparrowConfig
	 */
	public SequenceRequestAssignerPolicy(Map fifos, SparrowConfig config) {
		super(fifos, config);

		SparrowUtil.validateParam(
				new String[] { ConfigKeyConstants.PARAM_COLUMN_NAME },
				"SequenceRequestAssignerPolicy", config.getInitParameter());

		String colNms = config.getInitParameter().getParameterValue(
				ConfigKeyConstants.PARAM_COLUMN_NAME);
		this.colNames = colNms.split("[,]");
		shcr = (colNames.length > 1) ? new MultiColumnHashCodeResolver()
				: new SimpleHashCodeResolver();
	}

	/**
	 * assign
	 * 
	 * @param row
	 *            ResultRow
	 */
	public void assign(ResultRow row) {
		FIFO fifo = findQueue(row);		
		fifo.produce(row);
	}

	/**
	 * 
	 */
	public void assign(SparrowResultHolder srh) {
		FIFO fifo = findQueue(srh.getDataOutputHolder().getDriverRow());
		fifo.produce(srh);
	}
	
	
	/**
	 * 
	 * @param rr
	 * @return
	 */
	private FIFO findQueue(ResultRow rr){
		int hashCode = shcr.getValueHashCode(rr);
		int qNum = Math.abs(hashCode % QSIZE);
		//System.out.println("Queue Number : "+qNum);
		return fifoQueues[qNum];
	}

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
	private class SimpleHashCodeResolver {
		/**
		 * 
		 * @param row
		 *            ResultRow
		 * @return int
		 */
		public int getValueHashCode(ResultRow row) {
			int hashCode = DEFAULT_HASH_CODE;
			try {
				String value = row.getValue(colNames[0]);
				if (value != null) {
					hashCode = value.hashCode();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return hashCode;
		}
	}

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
	private class MultiColumnHashCodeResolver extends SimpleHashCodeResolver {
		/**
		 * 
		 * @param row
		 *            ResultRow
		 * @return int
		 */
		public int getValueHashCode(ResultRow row) {
			int hashCode = DEFAULT_HASH_CODE;
			String value = "";
			try {
				for (int i = 0; i < colNames.length; i++) {
					value += row.getValue(colNames[i]);
				}
				hashCode = value.hashCode();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return hashCode;
		}
	}

}
