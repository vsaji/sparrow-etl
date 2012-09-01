package sparrow.etl.core.dao.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import sparrow.etl.core.dao.impl.QueryObject;
import sparrow.etl.core.lang.function.Expression;
import sparrow.etl.core.lang.function.ExpressionResolverFactory;
import sparrow.etl.core.lang.function.FunctionUtil;
import sparrow.etl.core.util.Constants;
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
public class QueryExecutionStrategy {

	public static final int STRATEGY_LIST_ALONE = 0;

	public static final int STRATEGY_MAP_ALONE = 1;

	public static final int STRATEGY_LIST_MAP = 2;

	public static final int STRATEGY_NONE = 3;

	private static final HashMap hm = new HashMap();

	private static final String PARAM_LIST = "PL";

	private static final String PARAM_TOKEN = "PT";

	protected String sql = null;

	protected int strategy = STRATEGY_NONE;

	protected final String orgSql, name;

	protected final boolean replaceTokenExist;

	protected final boolean functionExists;

	protected final Expression[] functions;

	/**
	 *
	 * @param name
	 *            String
	 * @param sql
	 *            String
	 */
	private QueryExecutionStrategy(String name, String sql) {
		this.sql = sql;
		this.orgSql = sql;
		this.name = name;
		this.replaceTokenExist = (sql.indexOf(Constants.REPLACE_TOKEN_START) != -1);
		this.functionExists = (sql.indexOf(Constants.FUNCTION_TOKEN) != -1);

		if (functionExists) {
			List temp = new ArrayList();
			this.sql = FunctionUtil.resolveFunctions(sql, temp);
			this.functions = getFunctions(temp);
		} else {
			this.functions = null;
		}
	}

	/**
	 *
	 * @param functions
	 * @return
	 */
	private Expression[] getFunctions(List functions) {
		Expression[] e = new Expression[functions.size()];
		for (int i = 0; i < functions.size(); i++) {
			String element = (String) functions.get(i);
			e[i] = ExpressionResolverFactory.resolveExpression(element);
		}
		return e;
	}

	/**
	 *
	 * @return int
	 */
	public int getStrategy() {
		return STRATEGY_NONE;
	}

	/**
	 *
	 * @param values
	 * @return
	 */
	public String implementStrategy(Map values) {
		String transSQL = (functionExists) ? FunctionUtil.executeFunction(sql, values,functions) : sql;
		transSQL = (replaceTokenExist) ? replaceToken(transSQL, values) : sql;
		return transSQL;
	}

	/**
	 *
	 * @param sql
	 *            String
	 * @return QueryExecutionStrategy
	 */
	public static final QueryExecutionStrategy getStrategy(String ky, String sql) {
		QueryExecutionStrategy qes = null;
		String key = ky + "_" + sql.hashCode();
		// System.out.println("Get Strategy->[" + key + "]");
		if (hm.containsKey(key)) {
			qes = (QueryExecutionStrategy) hm.get(key);
		} else {
			// System.out.println("Adding->[" + ky + "]");
			qes = createStrategy(ky, sql, getStrategyID(sql));
			hm.put(key, qes);
		}

		return qes;
	}

	/**
	 *
	 * @param sql
	 *            String
	 * @param strategyId
	 *            int
	 * @return QueryExecutionStrategy
	 */
	private static final QueryExecutionStrategy createStrategy(String name,
			String sql, int strategyId) {
		switch (strategyId) {
		case STRATEGY_LIST_ALONE:
			return new ListAloneStrategy(name, sql);
		case STRATEGY_NONE:
			return new QueryExecutionStrategy(name, sql);
		case STRATEGY_MAP_ALONE:
			return new MapAloneStrategy(name, sql);
		case STRATEGY_LIST_MAP:
			return new ListAndMapStrategy(name, sql);
		}

		return null;
	}

	/**
	 *
	 * @param map
	 *            Map
	 * @return String
	 */
	protected final String replaceToken(Map keyValue) {
		return SparrowUtil.replaceTokens(sql, keyValue,
				Constants.REPLACE_TOKEN_START, Constants.TOKEN_END);
	}

	/**
	 *
	 * @param keyValue
	 * @return
	 */
	protected final String replaceToken(String tSQL, Map keyValue) {
		return SparrowUtil.replaceTokens(tSQL, keyValue,
				Constants.REPLACE_TOKEN_START, Constants.TOKEN_END);
	}

	/**
	 *
	 * @param sql
	 *            String
	 * @return int
	 */
	private static final int getStrategyID(String sql) {
		if (sql.indexOf("?") != -1
				&& sql.indexOf(Constants.VARIABLE_IDENTIFIER) == -1) {
			return STRATEGY_LIST_ALONE;
		} else if (sql.indexOf("?") == -1
				&& sql.indexOf(Constants.VARIABLE_IDENTIFIER) != -1) {
			return STRATEGY_MAP_ALONE;
		} else if (sql.indexOf("?") != -1
				&& sql.indexOf(Constants.VARIABLE_IDENTIFIER) != -1) {
			return STRATEGY_LIST_MAP;
		} else {
			return STRATEGY_NONE;
		}
	}

	/**
	 *
	 * @param qo
	 *            QueryObject
	 */
	public void implementStrategy(QueryObject qo) {
    String transSQL = (functionExists) ? FunctionUtil.executeFunction(sql, qo
				.getQueryParamAsMap(),functions): qo.getSQL();
		transSQL = (replaceTokenExist) ? replaceToken(transSQL, qo
				.getQueryParamAsMap()) : transSQL;
		qo.setTransformedSQL(transSQL);
	}

	/**
	 *
	 * @param sql
	 *            String
	 * @return boolean
	 */
	protected boolean validateSQL(QueryObject qo) {
		return (qo.getSQL() != null) ? orgSql.equals(qo.getSQL()) : true;
	}

	/**
	 *
	 * @return String
	 */
	public final String getSQL() {
		return sql;
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
	public static class ListAloneStrategy extends QueryExecutionStrategy {

		ListAloneStrategy(String name, String sql) {
			super(name, sql);
		}

		/**
		 *
		 * @return int
		 */
		public int getStrategy() {
			return STRATEGY_LIST_ALONE;
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
	 * @author Saji Venugopalan
	 * @version 1.0
	 */
	public static class MapAloneStrategy extends QueryExecutionStrategy {

		final List tokens;

		MapAloneStrategy(String name, String sql) {
			super(name, sql);
			QueryObject qo = new QueryObject();
			qo.setSQL(super.sql);
			this.tokens = DBUtil.getTokenParamAsList(qo);
			super.sql = qo.getSQL();
			qo = null;
		}

		/**
		 *
		 * @return List
		 */
		public List getToken() {
			return tokens;
		}

		/**
		 *
		 * @return int
		 */
		public int getStrategy() {
			return STRATEGY_MAP_ALONE;
		}

		/**
		 * implementStrategy
		 *
		 * @param paramList
		 *            List
		 * @param paramMap
		 *            Map
		 */
		public void implementStrategy(QueryObject qo) {

			if (!validateSQL(qo)) {
				getStrategy(qo.getName() + "_W1=2", qo.getSQL())
						.implementStrategy(qo);
				return;
			}

			ArrayList temp = new ArrayList();
			Map keyValue = qo.getQueryParamAsMap();

			String transSQL = this.sql;

			if (functionExists) {
				transSQL = FunctionUtil.executeFunction(transSQL, keyValue,functions);
			}

			if (replaceTokenExist) {
				transSQL = replaceToken(transSQL, keyValue);
			}

			for (Iterator iter = tokens.iterator(); iter.hasNext();) {
				Object item = keyValue.get(iter.next());
				temp.add(item);
			}

			qo.setTransformedSQL(transSQL);
			qo.getQueryParamAsArray().clear();
			qo.getQueryParamAsArray().addAll(temp);
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
	 * @author Saji Venugopalan
	 * @version 1.0
	 */
	public static class ListAndMapStrategy extends QueryExecutionStrategy {

		final Map prmSortedMap;

		final List tokens;

		/**
		 *
		 * @param sql
		 *            String
		 */
		ListAndMapStrategy(String name, String sql) {
			super(name, sql);
			this.prmSortedMap = new TreeMap();
			int pos = 0;

			// ----------------------------------------------
			String temp = super.sql;
			while ((pos = temp.indexOf("?")) != -1) {
				prmSortedMap.put(new Integer(pos), PARAM_LIST);
				temp = temp.replaceFirst("[?]", "#");
			}
			// ----------------------------------------------
			temp = super.sql;
			while ((pos = temp.indexOf(Constants.VARIABLE_IDENTIFIER)) != -1) {
				prmSortedMap.put(new Integer(pos), PARAM_TOKEN);
				temp = temp.replaceFirst("[{]", "#");
			}
			// ----------------------------------------------
			QueryObject qo = new QueryObject();
			qo.setSQL(super.sql);
			this.tokens = DBUtil.getTokenParamAsList(qo);
			super.sql = qo.getSQL();

			qo = null;
		}

		/**
		 *
		 * @return int
		 */
		public int getStrategy() {
			return STRATEGY_LIST_MAP;
		}

		/**
		 * implementStrategy
		 *
		 * @param paramList
		 *            List
		 * @param paramMap
		 *            Map
		 */
		public void implementStrategy(QueryObject qo) {

			if (!validateSQL(qo)) {
				getStrategy(qo.getName(), qo.getSQL()).implementStrategy(qo);
				return;
			}

			Map keyValue = qo.getQueryParamAsMap();

			String transSQL = this.sql;

			if (functionExists) {
				transSQL = FunctionUtil.executeFunction(transSQL, keyValue,functions);
			}

			if (replaceTokenExist) {
				transSQL = replaceToken(transSQL, keyValue);
			}

			List values = qo.getQueryParamAsArray();
			Iterator keys = prmSortedMap.keySet().iterator();

			ArrayList temp = new ArrayList();
			Object key = null;

			for (int i = 0, j = 0; keys.hasNext();) {
				key = keys.next();

				if (PARAM_LIST.equals(prmSortedMap.get(key))) {
					temp.add(values.get(i));
					i++;
				} else if (PARAM_TOKEN.equals(prmSortedMap.get(key))) {
					temp.add(keyValue.get(tokens.get(j)));
					j++;
				}
			}

			qo.setTransformedSQL(transSQL);
			qo.getQueryParamAsArray().clear();
			qo.getQueryParamAsArray().addAll(temp);
		}

	}

	public static void main(String[] args) {
		String sql1 = "select * from table where a=? and b=? and c=${test1.test} and d=? and e=${test2.test} and f=${test3.test}";
		String sql2 = "select * from table where c=${test1.test} and e=${test2.test} and f=${test3.test}";
		String sql3 = "select * from table where a=? and b=? and d=? ";

		Map paramMap = new HashMap();
		paramMap.put("test1.test", "Saji");
		paramMap.put("test2.test", "Shiji");
		paramMap.put("test3.test", "Shreya");

		List paramLst = new ArrayList();
		paramLst.add("Test1");
		paramLst.add("Test2");
		paramLst.add("Test3");

		QueryObject qo = new QueryObject();
		qo.setSQL(sql1);
		qo.getQueryParamAsArray().addAll(paramLst);
		qo.getQueryParamAsMap().putAll(paramMap);

		QueryExecutionStrategy qes = QueryExecutionStrategy.getStrategy("",
				sql1);
		qes.implementStrategy(qo);
		System.out.println(sql1);
		System.out.println(qo.getSQL());
		System.out.println(qo.getQueryParamAsArray());

		qo = new QueryObject();
		qo.setSQL(sql1);
		qo.getQueryParamAsArray().addAll(paramLst);
		qo.getQueryParamAsMap().putAll(paramMap);

		qes = QueryExecutionStrategy.getStrategy("", sql1);
		qes.implementStrategy(qo);
		System.out.println(sql1);
		System.out.println(qo.getSQL());
		System.out.println(qo.getQueryParamAsArray());

		qo = new QueryObject();
		qo.setSQL(sql2);
		qo.getQueryParamAsArray().addAll(paramLst);
		qo.getQueryParamAsMap().putAll(paramMap);

		qes = QueryExecutionStrategy.getStrategy("", sql2);
		qes.implementStrategy(qo);
		System.out.println(sql2);
		System.out.println(qo.getSQL());
		System.out.println(qo.getQueryParamAsArray());

		qo = new QueryObject();
		qo.setSQL(sql3);
		qo.getQueryParamAsArray().addAll(paramLst);
		qo.getQueryParamAsMap().putAll(paramMap);

		qes = QueryExecutionStrategy.getStrategy("", sql3);
		qes.implementStrategy(qo);
		System.out.println(sql3);
		System.out.println(qo.getSQL());
		System.out.println(qo.getQueryParamAsArray());

	}

}
