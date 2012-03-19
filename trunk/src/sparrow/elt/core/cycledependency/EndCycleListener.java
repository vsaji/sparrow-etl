package sparrow.elt.core.cycledependency;

import sparrow.elt.core.config.ConfigParam;
import sparrow.elt.core.context.SparrowApplicationContext;
import sparrow.elt.core.log.SparrowLogger;
import sparrow.elt.core.log.SparrowrLoggerFactory;
import sparrow.elt.core.util.Sortable;
import sparrow.elt.core.util.SparrowUtil;

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
public class EndCycleListener implements CycleEventListener {

	private boolean endCycle = true;
	private boolean ignoreCycleStrategy = false;

	private static final SparrowLogger logger = SparrowrLoggerFactory
			.getCurrentInstance(EndCycleListener.class);

	/**
	 * 
	 * @param param
	 *            ConfigParam
	 * @param context
	 *            SparrowApplicationContext
	 */
	public EndCycleListener(ConfigParam param, SparrowApplicationContext context) {
		this.ignoreCycleStrategy = SparrowUtil
				.performTernary(context.getConfiguration().getModule(),
						"ignore.cycle.strategy", false);
	}

	/**
	 * beginCycle
	 */
	public void beginCycle() {
		if (!ignoreCycleStrategy) {
			logger.info("[--- BEGIN CYCLE ---]");
		}
		endCycle = false;
	}

	/**
	 * endCycle
	 */
	public void endCycle() {
		if (!ignoreCycleStrategy) {
			logger.info("[--- END CYCLE ---]");
		}
		endCycle = true;
	}

	/**
	 * checkDependency
	 * 
	 * @return boolean
	 */
	public boolean checkDependency() {
		boolean rtnVal = (ignoreCycleStrategy) ? false : (!endCycle);
		// System.out.println("=========>"+ignoreCycleStrategy);
		// System.out.println("=========>"+rtnVal);
		return rtnVal;
	}

	/**
	 * getName
	 * 
	 * @return String
	 */
	public String getName() {
		return "EndCycleCheckDependant";
	}

	/**
	 * isProcessTerminationRequired
	 * 
	 * @return boolean
	 */
	public boolean isProcessTerminationRequired() {
		return false;
	}

	/**
	 * getStatusDescription
	 * 
	 * @return String
	 */
	public String getStatusDescription() {
		return "Last cycle has" + ((endCycle) ? " been" : "n't been")
				+ " completed";
	}

	/**
	 * getPriority
	 * 
	 * @return int
	 */
	public int getPriority() {
		return Sortable.PRIORITY_LOW;
	}

}
