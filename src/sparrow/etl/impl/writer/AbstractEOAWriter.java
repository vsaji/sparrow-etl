package sparrow.etl.impl.writer;

import sparrow.etl.core.config.SparrowDataWriterConfig;
import sparrow.etl.core.exception.DataWriterException;
import sparrow.etl.core.util.Constants;
import sparrow.etl.core.vo.DataOutputHolder;
import sparrow.etl.core.writer.AbstractDataWriter;

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
public abstract class AbstractEOAWriter extends AbstractDataWriter {

	public AbstractEOAWriter(SparrowDataWriterConfig config) {
		super(config);
	}

	/**
	 * writeData
	 * 
	 * @param data
	 *            DataOutputHolder
	 * @param statusCode
	 *            int
	 * @return int
	 */
	public int writeData(DataOutputHolder data, int statusCode)
			throws DataWriterException {
		return 0;
	}

	/**
	 * 
	 * @param flag
	 *            int
	 */
	public void endOfProcess(int flag) {
		if ((config.getTriggerEvent().equals(Constants.END_APP) && Constants.EP_END_APP == flag)
				|| (config.getTriggerEvent().equals(Constants.END_PROCESS) && Constants.EP_NO_RECORD == flag)) {
			doEndOfProcess();
		}
	}

	/**
   *
   */
	public void beginApplication() {
		if (config.getTriggerEvent().equals(Constants.BEGIN_APP)) {
			doEndOfProcess();
		}
	}

	/**
   *
   */
	public void endCycle() {
		if (config.getTriggerEvent().equals(Constants.END_CYCLE)) {
			doEndOfProcess();
		}
	}

	public void beginCycle() {
		if (config.getTriggerEvent().equals(Constants.BEGIN_CYCLE)) {
			doEndOfProcess();
		}
	}

	public abstract void doEndOfProcess();

	/**
	 * 
	 * @param cndton
	 * @return
	 */
	protected boolean evaluateCondition(String condition) {
		
		String cndton = replaceToken(condition.trim());

		if(logger.isDebugEnabled()){
			logger.debug("Evaluating Condition ["+condition+"]["+cndton+"]");
		}

		
		boolean result = false;
		String[] operand = null;
		/**************************************/
		if (cndton.indexOf(">=") > 0) {
			operand = splitOperand(cndton, ">=");
			result = processExpression(operand, ">=");
		} else if (cndton.indexOf("<=") > 0) {
			operand = splitOperand(cndton, "<=");
			result = processExpression(operand, "<=");
		} else if (cndton.indexOf(">") > 0) {
			operand = splitOperand(cndton, ">");
			result = processExpression(operand, ">");
		} else if (cndton.indexOf("<") > 0) {
			operand = splitOperand(cndton, "<");
			result = processExpression(operand, "<");
		} else if (cndton.indexOf("=") > 0) {
			operand = splitOperand(cndton, "=");
			result = processExpression(operand, "=");
		} else {
			result = Boolean.valueOf(cndton).booleanValue();
		}
		
		if(logger.isDebugEnabled()){
			logger.debug("Condition result ["+result+"]");
		}
		
		return result;
	}

	/**
	 * 
	 * @param temp
	 * @param opr
	 * @return
	 */
	private boolean processExpression(String temp[], String opr) {

		long rhsValue = 0, lhsVal = 0;

		boolean check = false;

		lhsVal = Integer.parseInt(temp[0].trim());

		rhsValue = Integer.parseInt(temp[1].trim());

		if (">=".equals(opr)) {
			check = (lhsVal >= rhsValue);
		} else if ("<=".equals(opr)) {
			check = (lhsVal <= rhsValue);
		} else if (">".equals(opr)) {
			check = (lhsVal > rhsValue);
		} else if ("<".equalsIgnoreCase(opr)) {
			check = (lhsVal < rhsValue);
		} else if ("=".equals(opr)) {
			check = String.valueOf(lhsVal).equals(String.valueOf(rhsValue));

		}
		return check;
	}

	/**
	 * 
	 * @param expr
	 * @param op
	 * @return
	 */
	private String[] splitOperand(String expr, String op) {
		String temp[] = expr.split(op);
		return temp;
	}

}
