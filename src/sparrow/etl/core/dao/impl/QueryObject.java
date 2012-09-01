package sparrow.etl.core.dao.impl;

import java.util.ArrayList;
import java.util.Map;

import sparrow.etl.core.util.CaseInSensitiveMap;
import sparrow.etl.core.util.SparrowUtil;


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
public class QueryObject implements Cloneable {

	private String sql, transformedSQL = null;

	private Map queryParamAsMap = null;

	private ArrayList queryParamAsArray = null;

	private String connectionName, name = null;

	private int fetchSize;

	private String rsWrapType = null;

	private String filter = null;

	private String columns = null;

	private boolean storeProc = true;

	private String useDB = null;

	private static final String[] DMLS = getDMLS();

	// new String[] {
	// "SELECT ", "INSERT ", "DELETE ", "UPDATE ","BEGIN ","BEGIN\n"};
	private String spOutParam = null;

	/**
	 * 
	 */
	public QueryObject() {
		this(null);
	}

	/**
	 * 
	 */
	public QueryObject(String name) {
		// queryParamAsMap = new TreeMap(String.CASE_INSENSITIVE_ORDER);
		queryParamAsMap = new CaseInSensitiveMap();
		queryParamAsArray = new ArrayList();
		this.name = name;
	}

	/**
	 * 
	 * @param sql
	 *            String
	 */
	public void setSQL(String sql) {
		this.sql = sql;
		String tmpSQL = SparrowUtil.strongLeftTrim(sql.toUpperCase());

		for (int i = 0; (i < DMLS.length && storeProc); i++) {
			if (tmpSQL.indexOf(DMLS[i]) == 0) {
				storeProc = false;
				break;
			}
		}
	}

	/**
	 * 
	 * @param qo
	 *            QueryObject
	 */
	public void reset(QueryObject qo) {
		this.connectionName = qo.getConnectionName();
		this.fetchSize = qo.getFetchSize();
		this.rsWrapType = qo.getRsWrapType();
		this.sql = qo.getSQL();
		this.storeProc = qo.isStoreProc();
		queryParamAsMap.clear();
		queryParamAsArray.clear();
	}

	/**
	 * 
	 * @param sql
	 *            String
	 * @param storeProc
	 *            boolean
	 */
	public void setSQL(String sql, boolean storeProc) {
		this.sql = sql;
		this.storeProc = storeProc;
	}

	/**
	 * 
	 * @param connectionName
	 *            String
	 */
	public void setConnectionName(String connectionName) {
		this.connectionName = connectionName;
	}

	/**
	 * 
	 * @param fetchSize
	 *            int
	 */
	public void setFetchSize(int fetchSize) {
		this.fetchSize = fetchSize;
	}

	/**
	 * 
	 * @param rsWrapType
	 *            String
	 */
	public void setRsWrapType(String rsWrapType) {
		this.rsWrapType = rsWrapType;
	}

	/**
	 * 
	 * @param storeProc
	 *            boolean
	 */
	public void setStoreProc(boolean storeProc) {
		this.storeProc = storeProc;
	}

	/**
	 * 
	 * @param transformedSQL
	 *            String
	 */
	public void setTransformedSQL(String transformedSQL) {
		this.transformedSQL = transformedSQL;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}

	public void setColumns(String columns) {
		this.columns = columns;
	}

	public void setSPOutParam(String spOutParam) {
		this.spOutParam = spOutParam;
	}

	/**
	 * 
	 * @return String
	 */
	public String getSQL() {
		return sql;
	}

	/**
	 * 
	 * @return Map
	 */
	public Map getQueryParamAsMap() {
		return queryParamAsMap;
	}

	/**
	 * 
	 * @return ArrayList
	 */
	public ArrayList getQueryParamAsArray() {
		return queryParamAsArray;
	}

	/**
	 * 
	 * @return String
	 */
	public String getConnectionName() {
		return connectionName;
	}

	/**
	 * 
	 * @return int
	 */
	public int getFetchSize() {
		return fetchSize;
	}

	/**
	 * 
	 * @return String
	 */
	public String getRsWrapType() {
		return rsWrapType;
	}

	/**
	 * 
	 * @return boolean
	 */
	public boolean isStoreProc() {
		return storeProc;
	}

	/**
	 * 
	 * @return String
	 */
	public String getTransformedSQL() {
		return transformedSQL;
	}

	public String getName() {
		return name;
	}

	public String getFilter() {
		return filter;
	}

	public String getColumns() {
		return columns;
	}

	public String getSPOutParam() {
		return spOutParam;
	}

	/**
	 * 
	 * @param key
	 *            String
	 * @param value
	 *            Object
	 */
	public void addQueryParam(String key, Object value) {
		queryParamAsMap.put(key, value);
	}

	/**
	 * 
	 * @param value
	 *            Object
	 */
	public void addQueryParam(Object value) {
		queryParamAsArray.add(value);
	}

	/**
	 * 
	 * @return String
	 */
	public String toString() {
		return getSQL();
	}

	/**
	 * 
	 * @param params
	 *            ArrayList
	 */
	void setQueryParam(ArrayList params) {
		this.queryParamAsArray = params;
	}

	/**
	 * 
	 * @param params
	 *            Map
	 */
	void setQueryParam(Map params) {
		this.queryParamAsMap = params;
	}

	/**
	 * 
	 * @throws CloneNotSupportedException
	 * @return Object
	 */
	protected Object clone() throws CloneNotSupportedException {
		QueryObject cln = (QueryObject) super.clone();
		cln.queryParamAsArray = new ArrayList();
		cln.queryParamAsMap = new CaseInSensitiveMap();

		// cln.queryParamAsMap = new TreeMap(String.CASE_INSENSITIVE_ORDER);
		// cln.setQueryParam(new HashMap());
		// cln.setQueryParam(new CaseInSensitiveMap());
		return cln;
	}

	public void reset() {
		queryParamAsArray = new ArrayList();
		queryParamAsMap = new CaseInSensitiveMap();
		transformedSQL = filter = columns = sql = name = connectionName = null;
		storeProc = false;
		fetchSize = 0;

	}

	/**
	 * 
	 * @return QueryObject
	 */
	public QueryObject getCopy() {
		try {
			return (QueryObject) clone();
		} catch (CloneNotSupportedException ex) {
			return null;
		}
	}

	private static final String[] getDMLS() {
		Map dml = SparrowUtil.getImplConfig("dml");
		String dmlS = (String) dml.get("tokens");
		return dmlS.split("[,]");
	}

	public String getUseDB() {
		return useDB;
	}

	public void setUseDB(String useDB) {
		this.useDB = useDB;
	}

}
