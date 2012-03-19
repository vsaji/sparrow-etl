package sparrow.elt.core.util;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import sparrow.elt.core.config.ConfigParam;
import sparrow.elt.core.dao.impl.ColumnHeader;
import sparrow.elt.core.dao.impl.ColumnTypes;
import sparrow.elt.core.dao.impl.ResultRow;
import sparrow.elt.core.exception.DataException;
import sparrow.elt.core.exception.InitializationException;
import sparrow.elt.core.exception.MethodInvocationException;
import sparrow.elt.core.exception.ObjectCreationException;
import sparrow.elt.core.exception.SparrowRuntimeException;
import sparrow.elt.core.lang.function.Expression;
import sparrow.elt.core.lang.function.FunctionUtil;
import sparrow.elt.core.log.SparrowLogger;
import sparrow.elt.core.log.SparrowrLoggerFactory;


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
public final class SparrowUtil {

	/**
   *
   */

	private static final SparrowLogger logger = SparrowrLoggerFactory
			.getCurrentInstance(SparrowUtil.class);

	private static String[] keys = null;
	private static boolean configParamSet = false;
	private static boolean implLoaded = false;
	private static QueueInfo qInfo = null;
	private static Map IMPL_RESOLVER = new HashMap();
	private static String instanceName = null;

	public static final Comparator OBJECT_PRIORITY_SORTER = GenericComparator
			.getComparator();

	/**
   *
   */
	private SparrowUtil() {
	}

	/**
	 * 
	 * @param text
	 *            String
	 * @param repl
	 *            String
	 * @param with
	 *            String
	 * @param max
	 *            int
	 * @return String
	 */
	public static String replace(String text, String repl, String with) {
		int max = -1;
		StringBuffer buf = new StringBuffer(text.length());
		int start = 0, end = 0;
		while ((end = text.indexOf(repl, start)) != -1) {
			buf.append(text.substring(start, end)).append(with);
			start = end + repl.length();

			if (--max == 0) {
				break;
			}
		}
		buf.append(text.substring(start));
		return buf.toString();
	}

	/**
	 * 
	 * @param text
	 *            String
	 * @param repl
	 *            String
	 * @param with
	 *            String
	 * @return String
	 */
	public static String replaceFirst(String text, String repl, String with) {
		StringBuffer buf = new StringBuffer(text.length());
		int start = 0, end = 0;
		while ((end = text.indexOf(repl, start)) != -1) {
			buf.append(text.substring(start, end)).append(with);
			start = end + repl.length();
			break;
		}
		buf.append(text.substring(start));
		return buf.toString();
	}

	/**
	 * 
	 * @param origString
	 *            String
	 * @param vars
	 *            Map
	 * @param startSymbol
	 *            String
	 * @param endSymbol
	 *            String
	 * @return String
	 */
	public static final String replaceTokens(String origString, Map vars,
			String startSymbol, String endSymbol) {

		StringBuffer finalString = new StringBuffer();
		int index = 0;
		int i = 0;
		String key = null;
		String value = null;
		while ((index = origString.indexOf(startSymbol, i)) > -1) {
			key = origString.substring(index + 2, origString.indexOf(endSymbol,
					index + 2));
			Object o = vars.get(key);
			value = (o == null) ? null : o.toString();
			finalString.append(origString.substring(i, index));
			if (value != null) {
				finalString.append(value);
			} else {
				finalString.append(startSymbol + key + endSymbol);
			}
			i = index + 3 + key.length();
		}
		finalString.append(origString.substring(i));

		return finalString.toString();
	}

	/**
	 * 
	 * @param origString
	 *            String
	 * @param vars
	 *            Map
	 * @return String
	 */
	public static final String replaceTokens(String origString, Map vars) {
		return replaceTokens(origString, vars, Constants.TOKEN_START,
				Constants.TOKEN_END);
	}

	/**
	 * 
	 * @param origString
	 *            String
	 * @param vars
	 *            Map
	 * @return String
	 */
	public static final String replaceTokens(String origString, Map vars,
			TokenResolver r) {
		String value = replaceTokens(origString, vars, Constants.TOKEN_START,
				Constants.TOKEN_END);
		value = replaceTokens(value, r);
		return value;
	}

	/**
	 * 
	 * @param origString
	 *            String
	 * @param startSymbol
	 *            String
	 * @param endSymbol
	 *            String
	 * @return String
	 */
	public static final String convertToJOSQLQulifiedVariable(
			String origString, String startSymbol, String endSymbol) {

		StringBuffer finalString = new StringBuffer();
		int index = 0;
		int i = 0;
		String key = null;
		String value = null;
		while ((index = origString.indexOf(startSymbol, i)) > -1) {
			key = origString.substring(index + 2, origString.indexOf(endSymbol,
					index + 2));
			value = ":" + key;
			finalString.append(origString.substring(i, index));
			if (value != null) {
				finalString.append(":" + key);
			} else {
				finalString.append(startSymbol + key + endSymbol);
			}
			i = index + 3 + key.length();
		}
		finalString.append(origString.substring(i));

		return finalString.toString();
	}

	/**
	 * 
	 * @return boolean
	 */
	public static final boolean isThisCallFromCore() {
		return Thread.currentThread().getName().equals(
				Constants.CORE_THREAD_NAME);
	}

	/**
	 * 
	 * @param params
	 *            String[]
	 */
	public static final void validateParam(String[] params,
			String componentName, ConfigParam param) {
		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < params.length; i++) {
			String paramValue = param.getParameterValue(params[i]);
			if (paramValue == null || paramValue.trim().equals("")) {
				sb.append(params[i]).append(",");
			}
		}

		if (sb.length() > 0) {
			String missingParams = "Following mandatory PARAM(S) are missing:\nComponent:"
					+ componentName
					+ ":\n{"
					+ sb.deleteCharAt(sb.length() - 1).toString() + "}";
			throw new InitializationException("MANDATORY_PARAM_MISSING",
					missingParams);

		}
	}

	/**
	 * 
	 * @param filePath
	 *            String
	 * @param filePattern
	 *            String
	 * @param startSymbol
	 *            String
	 * @param endSymbol
	 *            String
	 * @param r
	 *            Resolver
	 * @return File[]
	 */
	public static final File[] getFileList(String filePath, String filePattern,
			String startSymbol, String endSymbol, TokenResolver r) {

		filePath = replaceTokens(filePath, r);
		filePattern = replaceTokens(filePattern, r);
		filePath = (filePath
				.substring(filePath.length() - 1, filePath.length())
				.equals("/") ? filePath.substring(filePath.length() - 1)
				: filePath);
		FileFilter filter = new PatternFileFilter(filePattern);
		File[] file = new File(filePath).listFiles(filter);
		return file;
	}

	/**
	 * 
	 * @param origString
	 *            String
	 * @param vars
	 *            Map
	 * @param startSymbol
	 *            String
	 * @param endSymbol
	 *            String
	 * @return String
	 */
	public static final String replaceTokens(String origString, TokenResolver r) {

		String startSymbol = Constants.TOKEN_START;
		String endSymbol = Constants.TOKEN_END;

		StringBuffer finalString = new StringBuffer();
		int index = 0;
		int i = 0;
		String key = null;
		String value = null;
		while ((index = origString.indexOf(startSymbol, i)) > -1) {
			key = origString.substring(index + 2, origString.indexOf(endSymbol,
					index + 2));
			value = r.getTokenValue(key);
			finalString.append(origString.substring(i, index));
			if (value != null && !key.equals(value)) {
				finalString.append(value);
			} else {
				finalString.append(startSymbol + key + endSymbol);
			}
			i = index + 3 + key.length();
		}
		finalString.append(origString.substring(i));

		return finalString.toString();
	}

	/**
	 * 
	 * @param origString
	 *            String
	 * @param startSymbol
	 *            String
	 * @param endSymbol
	 *            String
	 * @param r
	 *            TokenResolver
	 * @return String
	 */
	public static final String replaceTokens(String origString,
			String startSymbol, String endSymbol, TokenResolver r) {

		StringBuffer finalString = new StringBuffer();

		int index = 0;
		int i = 0;
		String key = null;
		String value = null;
		int strtTokLen = startSymbol.length();
		int endTokLen = endSymbol.length();
		int endDouble = endTokLen + 2;
		try {

			while ((index = origString.indexOf(startSymbol, i)) > -1) {
				key = origString.substring(index + strtTokLen, origString
						.indexOf(endSymbol, index + strtTokLen));
				value = r.getTokenValue(key);
				finalString.append(origString.substring(i, index));
				if (value != null && !key.equals(value)) {
					finalString.append(value);
				} else {
					finalString.append(startSymbol + key + endSymbol);
				}
				i = index + endDouble + key.length();
			}
			finalString.append(origString.substring(i));
		} catch (Exception ex) {
			throw new SparrowRuntimeException(
					"Exception occured while replacing string[" + origString
							+ "]");
		}
		return finalString.toString();
	}

	/**
	 * 
	 * @param origString
	 *            String
	 * @param vars
	 *            Map
	 * @param startSymbol
	 *            String
	 * @param endSymbol
	 *            String
	 * @return String
	 */
	public static final String replaceTokens(String origString,
			String startSymbol, String endSymbol, MapTokenResolver r, Map values) {

		StringBuffer finalString = new StringBuffer();
		int index = 0;
		int i = 0;
		String key = null;
		String value = null;
		while ((index = origString.indexOf(startSymbol, i)) > -1) {
			key = origString.substring(index + 2, origString.indexOf(endSymbol,
					index + 2));
			value = r.getTokenValue(key, values);
			finalString.append(origString.substring(i, index));
			if (value != null) {
				finalString.append(value);
			} else {
				finalString.append(startSymbol + key + endSymbol);
			}
			i = index + 3 + key.length();
		}
		finalString.append(origString.substring(i));

		return finalString.toString();
	}

	/**
	 * 
	 * @param className
	 *            String
	 * @param contructorArg
	 *            Class
	 * @param constArgsObject
	 *            Object
	 * @return Object
	 */
	public static final Object createObject(String className,
			Class contructorArg, Object constArgsObject) {
		Object o = null;
		try {
			Class[] constructorArgs = { contructorArg };
			Constructor constructor = Class.forName(className).getConstructor(
					constructorArgs);
			Object[] configParaArg = { constArgsObject };
			o = constructor.newInstance(configParaArg);
		} catch (ClassNotFoundException ex) {
			throw new ObjectCreationException(
					"ClassNotFoundException while loading " + className, ex);
		} catch (NoSuchMethodException ex) {
			throw new ObjectCreationException(
					"NoSuchMethodException while loading " + className, ex);
		} catch (InvocationTargetException ex) {
			throw new ObjectCreationException(
					"InvocationTargetException while loading " + className, ex);
		} catch (IllegalAccessException ex) {
			throw new ObjectCreationException(
					"IllegalAccessException while loading " + className, ex);
		} catch (InstantiationException ex) {
			throw new ObjectCreationException(
					"InstantiationException while loading " + className, ex);
		}
		return o;
	}

	/**
	 * 
	 * @param className
	 *            String
	 * @param contructorArg
	 *            Class
	 * @param constArgsObject
	 *            Object
	 * @return Object
	 */
	public static final Object createObject(String className,
			Class[] contructorArg, Object[] constArgsObject) {
		Object o = null;
		try {
			Constructor constructor = Class.forName(className).getConstructor(
					contructorArg);
			o = constructor.newInstance(constArgsObject);
		} catch (ClassNotFoundException ex) {
			throw new ObjectCreationException(
					"ClassNotFoundException while loading " + className, ex);
		} catch (NoSuchMethodException ex) {
			throw new ObjectCreationException(
					"NoSuchMethodException while loading " + className, ex);
		} catch (InvocationTargetException ex) {
			throw new ObjectCreationException(
					"InvocationTargetException while loading " + className, ex);
		} catch (IllegalAccessException ex) {
			throw new ObjectCreationException(
					"IllegalAccessException while loading " + className, ex);
		} catch (InstantiationException ex) {
			throw new ObjectCreationException(
					"InstantiationException while loading " + className, ex);
		}
		return o;
	}

	/**
	 * 
	 * @param className
	 *            String
	 * @throws ObjectCreationException
	 * @return Object
	 */
	public static final Object createObject(String className) {
		Object o = null;
		try {
			o = Class.forName(className).newInstance();
		} catch (ClassNotFoundException ex) {
			throw new ObjectCreationException(
					"ClassNotFoundException while loading " + className, ex);
		} catch (IllegalAccessException ex) {
			throw new ObjectCreationException(
					"IllegalAccessException while loading " + className, ex);
		} catch (InstantiationException ex) {
			throw new ObjectCreationException(
					"InstantiationException while loading " + className, ex);
		}
		return o;
	}

	/**
	 * 
	 * @param date
	 *            Date
	 * @param format
	 *            String
	 * @return String
	 */
	public static final String formatDate(Date date, String format) {
		return new SimpleDateFormat(format).format(date);
	}

	/**
	 * 
	 * @param value
	 *            String
	 * @return String
	 */
	public static final String getDayName(String value) {
		return value.substring(0, value.indexOf(":"));
	}

	/**
	 * 
	 * @param value
	 *            String
	 * @return int
	 */
	public static final int getHour(String value, int pos) {
		String oValue = value.substring((pos == -1) ? value.indexOf(":") + 1
				: pos, value.lastIndexOf(":"));
		return Integer.parseInt(oValue);
	}

	/**
	 * 
	 * @param value
	 *            String
	 * @return int
	 */
	public static final int getMinute(String value) {
		String oValue = value.substring(value.lastIndexOf(":") + 1, value
				.length());
		return Integer.parseInt(oValue);
	}

	/**
	 * 
	 * @param o
	 *            Object
	 * @param methodName
	 *            String
	 * @return Object
	 */
	public static final Object invokeMethod(Object o, String methodName,
			Class[] argType, Object[] args) throws MethodInvocationException {
		Object rO = null;
		try {
			// Method m = o.getClass().getDeclaredMethod(methodName, argType);
			Method m = o.getClass().getMethod(methodName, argType);
			rO = m.invoke(o, args);
		} catch (InvocationTargetException ex1) {
			throw new MethodInvocationException(
					"InvocationTargetException occured while invoking "
							+ methodName + " in " + o.getClass().getName(), ex1);
		} catch (IllegalArgumentException ex1) {
			throw new MethodInvocationException(
					"IllegalArgumentException occured while invoking "
							+ methodName + " in " + o.getClass().getName(), ex1);
		} catch (IllegalAccessException ex1) {
			throw new MethodInvocationException(
					"IllegalAccessException occured while invoking "
							+ methodName + " in " + o.getClass().getName(), ex1);
		} catch (SecurityException ex1) {
			throw new MethodInvocationException(
					"SecurityException occured while invoking " + methodName
							+ " in " + o.getClass().getName(), ex1);
		} catch (NoSuchMethodException ex1) {
			throw new MethodInvocationException(
					"NoSuchMethodException occured while invoking "
							+ methodName + " in " + o.getClass().getName(), ex1);
		}
		return rO;
	}

	/**
	 * 
	 * @param param
	 *            ConfigParam
	 */
	public final static void setContextConfigParam(ConfigParam param) {

		if (!configParamSet) {
			ContextParam.setContextConfigParam(param);
			String temp = param
					.getParameterValue(ConfigKeyConstants.PARAM_PRIMARY_KEYS);
			keys = (temp != null) ? temp.split("[,]") : null;
			configParamSet = true;
		}

	}

	/**
	 * 
	 * @param cp
	 *            ConfigParam
	 * @param key
	 *            String
	 * @param alternate
	 *            String
	 * @return String
	 */
	public static final String performTernary(ConfigParam cp, String key,
			String alternate) {
		String value = cp.getParameterValue(key);
		return (cp.isParameterExist(key)) ? value : alternate;
	}

	/**
	 * 
	 * @param cp
	 *            ConfigParam
	 * @param key
	 *            String
	 * @param alternate
	 *            int
	 * @return int
	 */
	public static final long performTernaryForLong(ConfigParam cp, String key,
			long alternate) {
		String value = cp.getParameterValue(key);
		return (cp.isParameterExist(key)) ? Long.parseLong(value) : alternate;
	}

	/**
	 * 
	 * @param cp
	 *            ConfigParam
	 * @param key
	 *            String
	 * @param alternate
	 *            String
	 * @return String
	 */
	public static final int performTernary(ConfigParam cp, String key,
			int alternate) {
		String value = cp.getParameterValue(key);
		return (cp.isParameterExist(key)) ? Integer.parseInt(value) : alternate;
	}

	/**
	 * 
	 * @param cp
	 *            ConfigParam
	 * @param key
	 *            String
	 * @param alternate
	 *            boolean
	 * @return boolean
	 */
	public static final boolean performTernary(ConfigParam cp, String key,
			boolean alternate) {
		String value = cp.getParameterValue(key);
		return (cp.isParameterExist(key)) ? Boolean.valueOf(value)
				.booleanValue() : alternate;

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
	private static final class PatternFileFilter implements FileFilter {

		final Pattern pattern;

		PatternFileFilter(String pattern) {
			pattern = pattern.replaceAll("\\*", ".*");
			this.pattern = Pattern.compile(pattern);
		}

		/**
		 * accept
		 * 
		 * @param pathname
		 *            File
		 * @return boolean
		 */
		public boolean accept(File pathname) {
			if (pathname.isDirectory()) {
				return false;
			} else {
				return pattern.matcher(pathname.getName()).matches();
			}
		}
	}

	/**
	 * 
	 * @param rr
	 *            ResultRow
	 * @return String
	 */
	public static final String printDriverValue(ResultRow rr) {
		String value = "";
		if (keys != null && rr != null) {
			String tmp = "";
			try {
				for (int i = 0; i < keys.length; i++) {
					tmp = rr.getValue(keys[i]);
					value = value + tmp + ",";
				}
			} catch (Exception e) {
				return "UNKNOWN";
			}
			value = value.substring(0, value.length() - 1);
		} else {
			try {
				value = (rr != null) ? rr.getValue(0) : "ResultRow is NULL";
			} catch (DataException ex) {
				value = "UNKNOWN";
			}
		}
		return value;
	}

	/**
	 * 
	 * @param ch
	 *            ColumnHeader
	 * @param tableName
	 *            String
	 * @return String
	 */
	public static final String constructCREATE_TABLE(ColumnHeader ch,
			String tableName) {

		String text = "CREATE TABLE " + tableName + " (";
		int colCount = ch.getColumnCount();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < colCount; i++) {
			sb.append(ch.getFieldName(i)).append(" ").append(
					ch.getFieldTypeName(i)).append(getPrecision_Scale(ch, i))
					.append(",");
		}
		sb.deleteCharAt(sb.length() - 1);
		text = text + sb.toString() + ")";
		return text;
	}

	/**
	 * 
	 * @param ch
	 *            ColumnHeader
	 * @param tableName
	 *            String
	 * @return String
	 */
	public static final String constructCREATE_INDEX(String tableName,
			String[] indexNames) {

		String text = "";
		StringBuffer sb = null;
		if (indexNames.length > 0) {
			text = "CREATE INDEX " + tableName + "_IDX  ON " + tableName;
			sb = new StringBuffer();
			for (int i = 0; i < indexNames.length; i++) {
				sb.append(indexNames[i]).append(",");
			}
			sb.deleteCharAt(sb.length() - 1);
			text = text + "( " + sb.toString() + ")";
		}

		return text;
	}

	/**
	 * 
	 * @param ch
	 *            ColumnHeader
	 * @param tableName
	 *            String
	 * @return String
	 */
	public static final String constructINSERT(String tableName, ColumnHeader ch) {

		String text = "INSERT INTO " + tableName + " VALUES ";
		int colCount = ch.getColumnCount();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < colCount; i++) {
			sb.append("?,");
		}
		sb.deleteCharAt(sb.length() - 1);
		text = text + "( " + sb.toString() + ")";
		return text;
	}

	/**
	 * 
	 * @param ch
	 *            ColumnHeader
	 * @param indx
	 *            int
	 * @return String
	 */
	private static final String getPrecision_Scale(ColumnHeader ch, int indx) {
		int type = ch.getFieldType(indx);

		switch (type) {
		/**
		 * case ColumnTypes.DOUBLE: case ColumnTypes.FLOAT: return "(" +
		 * ch.getFieldSize(indx) + "," + ch.getFieldScale(indx) + ")";
		 **/
		case ColumnTypes.STRING:
			return "("
					+ ((ch.getFieldSize(indx) == -1) ? 100 : ch
							.getFieldSize(indx)) + ")";
		default:
			return "";
		}
	}

	/**
	 * 
	 * @param result
	 *            ResultRow
	 * @param prefix
	 *            String
	 * @param resultKeyValue
	 *            Map
	 */
	public static final void addResultAsKeyValue(ResultRow result,
			String prefix, Map resultKeyValue) {
		try {
			for (int i = 0; i < result.getColumnCount(); i++) {
				String key = prefix + "$" + result.getColumnName(i);
				resultKeyValue.put(key, getValue(result, result
						.getColumnName(i)));
			}
		} catch (DataException ex) {
			logger
					.error(ex.getErrorCode() + "-" + ex.getErrorDescription(),
							ex);
		}

	}

	/**
	 * 
	 * @param result
	 *            ResultRow
	 * @param columnName
	 *            String
	 * @return Object
	 */
	private static final Object getValue(ResultRow result, String columnName)
			throws DataException {

		ColumnHeader header = result.getHeader();

		switch (header.getFieldType(columnName)) {
		case ColumnTypes.STRING:
			return result.getValue(columnName);
		case ColumnTypes.INTEGER:
			return result.getInt(columnName);
		case ColumnTypes.LONG:
			return result.getLong(columnName);
		case ColumnTypes.DOUBLE:
			return result.getDouble(columnName);
		case ColumnTypes.DATE:
			Date dt = result.getDate(columnName);
			return (dt != null) ? new Timestamp(dt.getTime()) : null;
		case ColumnTypes.FLOAT:
			return result.getFloat(columnName);
		case ColumnTypes.JAVA_OBJECT:
			return result.getObject(columnName);
		case ColumnTypes.BLOB:
			return result.getBlob(columnName);
		case ColumnTypes.CLOB:
			return result.getValue(columnName);
		case ColumnTypes.NUMBER:
			return result.getNumber(columnName);
		default:
			return result.getValue(columnName);
		}

	}

	/**
	 * 
	 * @return QueueInfo
	 */
	public static final QueueInfo getFinalizerQueueInfo() {
		return qInfo;
	}

	/**
	 * 
	 * @param quInfo
	 *            QueueInfo
	 */
	public static final void setFinalizerQueueInfo(QueueInfo quInfo) {
		if (qInfo == null) {
			qInfo = quInfo;
		}
	}

	/**
	 * 
	 * @param fileName
	 *            String
	 */
	public static final Properties loadProperties(String fileName) {
		Properties props = new Properties();
		try {
			props.load(getFileAsStream(fileName));
		} catch (Exception ex) {
			logger.warn("File [" + fileName + "] could not be loaded");
		}
		return props;
	}

	/**
	 * 
	 * @param value
	 *            String
	 * @return String
	 */
	public static final String replaceNewLine(String value) {

		if (value != null) {
			return value.replaceAll("\n", " ");
		} else {
			return "";
		}
	}

	/**
	 * 
	 * @param param
	 *            ConfigParam
	 * @throws NamingException
	 * @return InitialContext
	 */
	public static final Context getInitialContext(ConfigParam param)
			throws NamingException {
		return new InitialContext(getInitialContextProperties(param));
	}

	/**
	 * 
	 * @param param
	 *            ConfigParam
	 * @return Properties
	 */
	public static final Properties getInitialContextProperties(ConfigParam param) {
		Properties props = new Properties();
		props.put(Context.PROVIDER_URL, param
				.getParameterValue(ConfigKeyConstants.PARAM_PROVIDER_URL));
		props.put(Context.INITIAL_CONTEXT_FACTORY, param
				.getParameterValue(ConfigKeyConstants.PARAM_INITIAL_CONTEXT));

		if (param.isParameterExist(ConfigKeyConstants.PARAM_SECURITY_PRINCIPAL)) {
			props
					.put(
							Context.SECURITY_PRINCIPAL,
							param
									.getParameterValue(ConfigKeyConstants.PARAM_SECURITY_PRINCIPAL));
		}
		if (param
				.isParameterExist(ConfigKeyConstants.PARAM_SECURITY_CREDENTIALS)) {

			props
					.put(
							Context.SECURITY_CREDENTIALS,
							param
									.getParameterValue(ConfigKeyConstants.PARAM_SECURITY_CREDENTIALS));
		}
		return props;
	}

	/**
	 * 
	 * @param componentKey
	 *            String
	 * @return Map
	 */
	public static final Map getImplConfig(String componentKey) {
		return (Map) IMPL_RESOLVER.get(componentKey);
	}

	/**
	 * 
	 * @param prefix
	 *            String
	 * @param extention
	 *            String
	 * @return String
	 */
	public static final String constructOutputFileName(String path,
			String prefix, String extention) {
		return path + "/" + prefix + instanceName + "_"
				+ new SimpleDateFormat("ddMMyyyy").format(new Date())
				+ extention;
	}

	/**
   *
   */
	public static final void loadImplConfig() {

		if (!implLoaded) {
			instanceName = System.getProperties().getProperty(
					Constants.SPEAR_INSTANCE_NAME);
			instanceName = (instanceName == null) ? "" : "_" + instanceName;

			IMPL_RESOLVER = covertToCatalogMap(
					"com/cs/sg/spear/core/config/impl.properties",
					"user_impl.properties");
			makeUnmodifiable(IMPL_RESOLVER);
			implLoaded = true;
		}
	}

	/**
	 * 
	 * @param main
	 *            String
	 * @param override
	 *            String
	 * @return Map
	 */
	public static final Map covertToCatalogMap(String main, String override) {

		Map rtnVal = new HashMap();

		Properties prop = loadProperties(main);

		Properties user_prop = loadProperties(override);

		if (!user_prop.isEmpty()) {
			prop.putAll(user_prop);
		}

		Enumeration en = prop.propertyNames();

		while (en.hasMoreElements()) {

			String key = (String) en.nextElement();
			String mainKey = key.substring(0, key.indexOf("."));
			String subKey = key.substring(key.indexOf(".") + 1);
			HashMap hm = null;

			if (rtnVal.containsKey(mainKey)) {
				hm = (HashMap) rtnVal.get(mainKey);
			} else {
				hm = new HashMap();
				rtnVal.put(mainKey, hm);
			}
			hm.put(subKey, prop.getProperty(key));
		}

		return rtnVal;
	}

	/**
	 * 
	 * @param file
	 * @param backupOnExist
	 */
	public static final void doFileExistAction(String file,
			boolean backupOnExist) {
		if (new File(file).exists()) {
			if (backupOnExist) {
				new File(file).renameTo(new File(file + "."
						+ formatDate(new Date(), "ddMMyyyyHHmmss") + ".bak"));
			} else {
				new File(file).delete();
			}
		}
	}

	/**
	 * 
	 * @param value
	 * @param gtr
	 * @return
	 */
	public static String evaluateAndReplace(String value,GenericTokenResolver gtr) {

		String val = (value.indexOf(Constants.VARIABLE_IDENTIFIER) != -1) ? SparrowUtil
				.replaceTokens(value, gtr): value;

		if (val.indexOf(Constants.FUNCTION_TOKEN) != -1) {
			List temp = new ArrayList();
			val = FunctionUtil.resolveFunctions(val, temp);
			Expression[] functions = FunctionUtil.getFunctions(temp);
			val = FunctionUtil.executeFunction(val, gtr.getAllTokens(),
					functions);
		}
		return val;
	}

	/**
   *
   */
	private static final void makeUnmodifiable(Map value) {
		for (Iterator key = value.keySet().iterator(); key.hasNext();) {
			String ky = (String) key.next();
			Map subSet = (HashMap) value.get(ky);
			subSet = Collections.unmodifiableMap(subSet);
			value.put(ky, subSet);
		}
	}

	/**
	 * 
	 * @param resource
	 *            String
	 * @throws Exception
	 * @return InputStream
	 */
	public static final InputStream getFileAsStream(String file)
			throws Exception {
		boolean isAbsolutePath = isFile(file);
		InputStream is = (isAbsolutePath) ? new FileInputStream(file)
				: SparrowUtil.class.getClassLoader().getResourceAsStream(file);
		return is;
	}

	/**
	 * 
	 * @param value
	 *            String
	 * @return String
	 */
	public static final String strongLeftTrim(String value) {
		int i = 0;
		for (; i < value.length(); i++) {
			char c = value.charAt(i);
			if (c == 10 || c == 32 || c == 9) {
				continue;
			}
			break;
		}
		return value.substring(i);
	}

	/**
	 * 
	 * @param value
	 *            String
	 * @return String
	 */
	public static final String strongRightTrim(String value) {
		int i = value.length() - 1;
		for (; 0 < i; i--) {
			char c = value.charAt(i);
			if (c == 10 || c == 32 || c == 9) {
				continue;
			}
			break;
		}
		return value.substring(0, i + 1);
	}

	/**
	 * 
	 * @param value
	 *            String
	 * @return String
	 */
	public static final String strongTrim(String value) {
		String trimVal = strongLeftTrim(value);
		trimVal = strongRightTrim(trimVal);
		return trimVal;
	}

	/**
	 * 
	 * @param pattern
	 * @param text
	 * @return
	 */
	public static final boolean matchString(String pattern, String text) {
		pattern += "\0";
		text += "\0";

		int n = pattern.length();
		boolean[] states = new boolean[n + 1];
		boolean[] old = new boolean[n + 1];
		old[0] = true;

		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			states = new boolean[n + 1];

			for (int j = 0; j < n; j++) {
				char p = pattern.charAt(j);

				if (old[j] && (p == '*'))
					old[j + 1] = states[j] = states[j + 1] = true;
				if (old[j] && (p == c))
					states[j + 1] = true;
				if (old[j] && (p == '.'))
					states[j + 1] = true;
				// if(old[j] && (p=='*')) states[j]=states[j+1]=true;
			}
			old = states;
		}
		return states[n];
	}

	/**
	 * 
	 * @param fileName
	 *            String
	 * @return String
	 */
	public static final String readTextFile(String fileName) throws Exception {

		StringBuffer sb = new StringBuffer();
		InputStream in = null;

		try {
			in = getFileAsStream(fileName);
			InputStreamReader isr = new InputStreamReader(in);
			int ch = 0;

			while ((ch = in.read()) > -1) {
				sb.append((char) ch);
			}
			isr.close();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (Exception ex1) {
					ex1.printStackTrace();
				}
			}
		}
		return sb.toString();
	}

	/**
	 * 
	 * @param value
	 *            String
	 * @return String
	 */
	public static final String escapeXML(String value) {
		if (value != null) {
			value = replace(value, "&", "&amp;");
			value = replace(value, ">", "&lt;");
			value = replace(value, "<", "&gt;");
			value = replace(value, "\"", "&quot;");
		}
		return value;
	}

	/**
	 * 
	 * @param value
	 *            String
	 * @return boolean
	 */
	public static final boolean isFile(String value) {
		return (value.toLowerCase().charAt(0) == '/')
				|| (value.toLowerCase().charAt(1) == ':');
	}

	/**
	 * 
	 * @param value
	 * @return
	 */
	public static final String getFileSeperator(String value) {
		return (value.lastIndexOf("/")>-1) ? "/" : ((value.lastIndexOf("\\") > -1) ? "\\" : null);
	}
	
	/**
	 * 
	 * @param s
	 * @return
	 */
	public static final boolean isNotNullAndEmpty(String s) {
		return (s != null && !s.trim().equals(""));
	}

	/**
	 * 
	 * @param arg
	 *            String[]
	 */
	public static void main(String[] arg) {
		String abc = "{prop.key}.db.url";
		System.out.println(replace(abc, "{prop.key}", "dev"));

		String test = "\n  \t \n This is a simple text \t \n\n";
		System.out.println("*******************************");
		System.out.println(strongLeftTrim(test));
		System.out.println("*******************************");
		System.out.println(strongRightTrim(test));
		System.out.println("*******************************");
		System.out.println(strongTrim(test));
	}

}
