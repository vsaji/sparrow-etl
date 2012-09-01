package sparrow.etl.core.util;

import java.util.Iterator;
import java.util.Map;

import sparrow.etl.core.fifo.FIFO;


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
public class QueueInfo {

	private final Map queues;

	/**
	 * 
	 * @param queue
	 *            FIFO
	 */
	public QueueInfo(Map queue) {
		this.queues = queue;
	}

	/**
	 * 
	 * @return boolean
	 */
	public boolean isQueueEmpty() {
		for (Iterator it = queues.keySet().iterator(); it.hasNext();) {
			FIFO queue = (FIFO)queues.get(it.next().toString());
			if (!queue.isQueueEmpty()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 
	 * @return int
	 */
	public int getQueueDepth(String qName) {
		return ((FIFO)queues.get(qName)).getQueueDepth();
	}

}
