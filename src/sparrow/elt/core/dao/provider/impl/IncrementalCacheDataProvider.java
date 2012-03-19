package sparrow.elt.core.dao.provider.impl;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import sparrow.elt.core.config.SparrowDataProviderConfig;
import sparrow.elt.core.dao.impl.RecordSet;
import sparrow.elt.core.dao.impl.RecordSetImpl_Disconnected;
import sparrow.elt.core.dao.provider.DataProvider;
import sparrow.elt.core.exception.DataException;
import sparrow.elt.core.exception.InitializationException;
import sparrow.elt.core.util.Constants;


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
public class IncrementalCacheDataProvider extends CacheDataProvider implements
		IncrementalCacheProvider {

	DBDataProvider dbProvider;

	private String sqlWithoutWhere = null;

	private String unchangedSQL = null;

	private String sqlWithDPName = null;

	private String sqlWithoutColNames = null;

	private boolean tokenExists = false;

	private final SingletonDataLoader sdl = new SingletonDataLoader();

	private static final String COL_TOKEN = "#COLS#";

	private String whereClause = null;

	/**
	 * 
	 * @param provider
	 *            DataProvider
	 * @param config
	 *            SparrowDataProviderConfig
	 */
	public IncrementalCacheDataProvider(DataProvider prvider,
			SparrowDataProviderConfig config) {
		super(prvider, config);

		if (provider instanceof DBDataProvider) {
			dbProvider = (DBDataProvider) provider;
			if (dbProvider.getQuery().isStoreProc()) {
				throw new InitializationException(
						"Data Provider ["
								+ prvider.getName()
								+ "]: Cache Type: [incremental] is not supported with Store Procedure or PL/SQL block");
			}
		} else {
			throw new InitializationException(
					"Data Provider ["
							+ prvider.getName()
							+ "]: Cache Type: [incremental] cannot applied for non DB data provider");
		}

		init();

	}

	/**
	 * 
	 * @throws CloneNotSupportedException
	 * @return Object
	 */
	public Object clone() throws CloneNotSupportedException {
		IncrementalCacheDataProvider clone = (IncrementalCacheDataProvider) super
				.clone();
		clone.dbProvider = (DBDataProvider) clone.provider;
		return clone;
	}

	/**
	 * 
	 */
	private void init() {
		unchangedSQL = dbProvider.getQuery().getSQL();
		String tempSql = unchangedSQL.toUpperCase();
		tokenExists = (unchangedSQL.indexOf(Constants.VARIABLE_IDENTIFIER) != -1 || unchangedSQL
				.indexOf("?") != -1);

		boolean whereConditionExist = (tempSql.indexOf("WHERE") != -1);

		String sql = (whereConditionExist) ? unchangedSQL.substring(0, tempSql
				.indexOf("WHERE")) : unchangedSQL;

		whereClause = (whereConditionExist) ? unchangedSQL.substring(tempSql
				.indexOf("WHERE") + 5) : null;

		this.sqlWithoutWhere = sql;

		// sql = (whereConditionExist) ?
		// sql + " WHERE " + FILTER_TOKEN + " " + whereClause :
		// sql + " " + FILTER_TOKEN;

		dbProvider.getQuery().setRsWrapType(Constants.RESULT_WRAP_DISCONNECTED);
		sqlWithDPName = replaceTableName(unchangedSQL);
		sqlWithoutColNames = replaceColumnNamesWithToken(sqlWithDPName);
	}

	private String replaceColumnNamesWithToken(String sql) {
		String sqlInUpperCase = sql.toUpperCase();
		String fromOnwards = sql.substring(sqlInUpperCase.indexOf("FROM"));
		String sqlWithNewColumns = "SELECT " + COL_TOKEN + " " + fromOnwards;
		return sqlWithNewColumns;
	}

	/**
	 * 
	 * @param unchangedSQL
	 *            String
	 * @return String
	 */
	private String replaceTableName(String unchangedSQL) {
		String sqlInUpperCase = unchangedSQL.toUpperCase();
		String beforeSelect = unchangedSQL.substring(0, sqlInUpperCase
				.indexOf("FROM") + 5);
		String afterTable = unchangedSQL.substring(sqlInUpperCase
				.indexOf("WHERE"));
		String newSQL = beforeSelect + " " + dbProvider.getName() + " "
				+ afterTable;
		return newSQL;
	}

	/**
	 * 
	 * @throws DataException
	 */
	public void loadData() throws DataException {

		// this result 0 record and helps to create empty memory table.
		dbProvider.getQuery().setSQL(sqlWithoutWhere + " WHERE 1=2");

		RecordSet rs = dbProvider.getData();

		try {
			createTable(rs.getColumnHeaders());
			rs.close();
			rs = null;
		} catch (SQLException ex) {
			throw new DataException(
					"SQLException occured while creating memory table:"
							+ getName(), ex);
		}

	}

	/**
	 * 
	 * @param columns
	 *            String
	 * @param params
	 *            Map
	 * @throws DataException
	 * @return RecordSet
	 */
	public RecordSet getData(String columns, Map params) throws DataException {
		RecordSet rs = null;

		Map parm = (params == null) ? dbProvider.getQuery()
				.getQueryParamAsMap() : params;

		// logger.info("[" + getName() + "][" + hashCode() + "]
		// SingletonDataLoader [" +
		// sdl.hashCode() + "]");

		try {
			rs = loadFromCache(columns, parm);
			if (rs.getRowCount() == 0) {
				rs = sdl.loadFromDB(this, columns, parm); // loadFromDB(parm);
				// logger.info("["+getName()+"]["+hashCode()+"] Loaded from DB
				// ["+rs.getRowCount()+"]");
			}
			// else{
			// logger.info("["+getName()+"]["+hashCode()+"]sdl[" +
			// sdl.hashCode() + "] Found in Cache ["+rs.getRowCount()+"]");
			// }
		} catch (Exception ex) {
			throw new DataException(ex);
		}
		return rs;
	}

	/**
	 * 
	 * @param whereCondition
	 *            String
	 * @param param
	 *            Map
	 * @param columns
	 *            String
	 * @return RecordSet
	 */
	public RecordSet applyFilter(String whereCondition, Map param,
			String columns) {
		RecordSet rs = loadFromCache(whereCondition, param, columns);
		try {
			if (rs.getRowCount() == 0) {
				rs = sdl.loadFromDB(this, whereCondition, param, columns);
			}
		} catch (DataException ex) {
			ex.printStackTrace();
			rs = new RecordSetImpl_Disconnected();
		}

		return rs;
	}

	/**
	 * 
	 * @param whereCondition
	 *            String
	 * @param param
	 *            Map
	 * @param columns
	 *            String
	 * @return RecordSet
	 */
	public RecordSet loadFromCache(String whereCondition, Map param,
			String columns) {
		return super.applyFilter(whereCondition, param, columns);
	}

	/**
	 * 
	 * @param param
	 *            Map
	 * @throws Exception
	 * @return RecordSet
	 */
	public RecordSet loadFromCache(String cols, Map param) throws Exception {

		String sql = (cols == null) ? sqlWithDPName : sqlWithoutColNames
				.replaceFirst(COL_TOKEN, cols);

		RecordSet rs = (tokenExists) ? (RecordSet) super.queryWithParam(sql,
				param) : super.queryWithOutParam(sql);

		return rs;
	}

	/**
	 * 
	 * @param whereCondition
	 *            String
	 * @param param
	 *            Map
	 * @param columns
	 *            String
	 * @return RecordSet
	 */
	RecordSet loadFromCache(String whereCondition, List param, String columns) {
		return super.applyFilter(whereCondition, param, columns);
	}

	/**
	 * 
	 * @return RecordSet
	 */
	public RecordSet getData() throws DataException {

		if (getQuery().getFilter() == null && whereClause != null) {
			getQuery().setFilter(whereClause);
		}
		RecordSet rs = superGetData();

		try {
			if (rs.getRowCount() == 0) {
				rs = sdl.loadFromDB(this, getQuery());
			}
		} catch (DataException ex) {
			ex.printStackTrace();
			rs = new RecordSetImpl_Disconnected();
		} catch (Exception ex) {
			ex.printStackTrace();
			rs = new RecordSetImpl_Disconnected();
		} finally {
			resetQueryObject_();
		}

		return rs;
	}

	/**
	 * 
	 * @throws DataException
	 * @return RecordSet
	 */
	public RecordSet superGetData() throws DataException {
		return super.getData();
	}

	/**
	 * 
	 */
	protected void resetQueryObject() {
		// empty implementation to avoid the super.getData() to clean the query
		// object
	}

	/**
	 * 
	 */
	private void resetQueryObject_() {
		super.resetQueryObject();
	}

	/**
	 * 
	 */
	public DataProvider getDataProvider() {
		return super.provider;
	}

}
