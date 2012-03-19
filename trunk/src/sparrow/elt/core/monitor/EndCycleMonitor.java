package sparrow.elt.core.monitor;

import sparrow.elt.core.util.AsyncRequestProcessor;
import sparrow.elt.core.util.RequestListener;


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
public final class EndCycleMonitor implements RequestListener {

	private int MAX_END_CYCLE_COUNT = 0;
	private int CYCLE_COUNTER = 0;
	private CycleMonitor monitor = null;
	private static EndCycleMonitor instance = null;
	private AsyncRequestProcessor endCycleReqProcessor = AsyncRequestProcessor
			.createAsynchProcessor("END_CYCLE_NOTIFIER");
	private Object tempObj = new Object();

	/**
   *
   */
	private EndCycleMonitor() {
		endCycleReqProcessor.registerListener(this);
		endCycleReqProcessor.start();
	}

	/**
	 * 
	 * @return EndCycleMonitor
	 */
	public static EndCycleMonitor getInstance() {
		instance = (instance == null) ? new EndCycleMonitor() : instance;
		return instance;
	}

	/**
	 * 
	 * @param monitor
	 *            CycleMonitor
	 */
	public void setCycleMonitor(CycleMonitor monitor) {
		if (this.monitor == null) {
			this.monitor = monitor;
		}
	}

	/**
	 * 
	 * @param maxCount
	 *            int
	 */
	public void setMaxCount(int maxCount) {
		if (MAX_END_CYCLE_COUNT == 0) {
			MAX_END_CYCLE_COUNT = maxCount;
		}
	}

	/**
   *
   */
	public synchronized void increment() {
		CYCLE_COUNTER++;
		if (CYCLE_COUNTER == MAX_END_CYCLE_COUNT) {
			endCycleReqProcessor.process(tempObj);
		}
	}


	/**
   *
   */
	public synchronized void reset() {
		CYCLE_COUNTER = 0;
	}

	public void endProcess() {
	}

	/**
 * 
 */
	public void process(Object o) throws Exception {
		this.monitor.notifyEndCycle();
	}

}
