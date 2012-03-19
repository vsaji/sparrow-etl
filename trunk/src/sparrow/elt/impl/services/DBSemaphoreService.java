package sparrow.elt.impl.services;

import sparrow.elt.core.config.SparrowServiceConfig;
import sparrow.elt.core.dao.impl.RecordSet;
import sparrow.elt.core.dao.impl.ResultRow;
import sparrow.elt.core.dao.provider.DataProviderElement;
import sparrow.elt.core.dao.provider.impl.DBDataProviderElement;
import sparrow.elt.core.exception.DataException;
import sparrow.elt.core.exception.ProviderNotFoundException;
import sparrow.elt.core.exception.SemaphoreException;
import sparrow.elt.core.log.SparrowLogger;
import sparrow.elt.core.log.SparrowrLoggerFactory;
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
public class DBSemaphoreService extends AbstractSemaphoreService {

	private String terminationStatus = null;
	private String postStartStatus = null;
	private String endStatus = null;
	private String preStartStatus = null;
	private String dpCheckStatus = null;
	private String dpUpateStatus = null;

	private static final SparrowLogger logger = SparrowrLoggerFactory
			.getCurrentInstance(DBSemaphoreService.class);

	/**
	 * 
	 * @param config
	 *            SparrowServiceConfig
	 */
	public DBSemaphoreService(SparrowServiceConfig config) {
		super(config);
		initialize();
	}

	/**
	 * initialize
	 * 
	 * @param config
	 *            SparrowServiceConfig
	 */
	public void initialize() {
		
		SparrowUtil.validateParam(new String[] { "post.start.status",
				"end.status","check.status",
				"update.status" }, "DBSemaphoreService", config
				.getInitParameter());
		
		terminationStatus = config.getInitParameter().getParameterValue(
				"termination.status");
		postStartStatus = config.getInitParameter().getParameterValue(
				"post.start.status");
		endStatus = config.getInitParameter().getParameterValue("end.status");
		preStartStatus = endStatus;
		dpCheckStatus = config.getInitParameter().getParameterValue(
				"check.status");
		dpUpateStatus = config.getInitParameter().getParameterValue(
				"update.status");

	}

	/**
	 * beginApplication
	 */
	public void acquireOnStart() throws SemaphoreException {
		try {
			logger.info("Checking SEMAPHORE Status");
			String currentState = checkStatus();
			logger.info("SEMAPHORE Status [" + currentState + "]");

			if (preStartStatus.equals(currentState)) {
				DBDataProviderElement dbPE = (DBDataProviderElement) config
						.getContext().getDataProviderElement(dpUpateStatus);
				dbPE.getQuery().addQueryParam(postStartStatus);
				dbPE.executeQuery();
				dbPE.close();
			} else {
				SemaphoreException e = new SemaphoreException(
						"SEMAPHORE_BA_LOCKED",
						"Application could not start. SEMAPHORE STATUS ["
								+ currentState + "]");
				throw e;
			}
		} catch (ProviderNotFoundException ex) {
			SemaphoreException e = new SemaphoreException(
					"SEMAPHORE_BA_PROVIDER_NOT_FOUND", ex.getMessage());
			throw e;
		} catch (DataException ex) {
			SemaphoreException e = new SemaphoreException(
					"SEMAPHORE_BA_DATA_EXP", ex.getMessage());
			throw e;
		}
	}

	/**
	 * endApplication
	 */
	public void releaseOnEnd() throws SemaphoreException {
		try {
			updateStatus(endStatus);
		} catch (Exception ex) {
			SemaphoreException e = new SemaphoreException("SEMAPHORE_EA_EXP",
					ex.getMessage());
			throw e;
		}
	}

	/**
	 * checkStatus
	 * 
	 * @return String
	 */
	public String checkStatus() throws ProviderNotFoundException, DataException {

		DataProviderElement dpe = config.getContext().getDataProviderElement(
				dpCheckStatus);
		RecordSet rs = dpe.getData();
		ResultRow row = rs.getFirstRow();
		String currentState = row.getValue(0);
		rs.close();
		dpe.close();

		if (currentState != null && !currentState.trim().equals("")) {
			currentState = currentState.trim();
		} else {
			throw new DataException("SEMAPHORE_STATUS_EMP_OR_NULL",
					"Application could not start. SEMAPHORE STATUS [empty or null]");
		}

		return currentState;
	}

	/**
	 * updateStatus
	 * 
	 * @param status
	 *            String
	 */
	public void updateStatus(String status) throws DataException {
		DBDataProviderElement dbPE = (DBDataProviderElement) config
				.getContext().getDataProviderElement(dpUpateStatus);
		dbPE.getQuery().addQueryParam(status);
		dbPE.executeQuery();
		dbPE.close();
	}

	/**
	 * isProcessTerminationFlagOn
	 * 
	 * @return boolean
	 */
	public boolean isProcessTerminationFlagOn() throws SemaphoreException {
		boolean termination = false;
		if (SparrowUtil.isNotNullAndEmpty(terminationStatus)) {
			try {
				String currentStatus = checkStatus();
				termination = (currentStatus.equals(terminationStatus));
			} catch (DataException ex) {
				throw new SemaphoreException("SEMAPHORE_UNKNOWN",
						"DataException occured while checking the semaphore status");
			} catch (ProviderNotFoundException ex) {
				throw new SemaphoreException("SEMAPHORE_UNKNOWN",
						"ProviderNotFoundException occured while checking the semaphore status");
			}
		}
		return termination;
	}
}
