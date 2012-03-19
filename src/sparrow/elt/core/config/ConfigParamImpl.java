package sparrow.elt.core.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import sparrow.elt.core.dao.impl.ResultRow;
import sparrow.elt.core.exception.InitializationException;
import sparrow.elt.core.security.CryptoUtil;
import sparrow.elt.core.util.Constants;
import sparrow.elt.core.util.KeyTransformerMap;
import sparrow.elt.core.util.SparrowUtil;
import sparrow.elt.core.util.TokenResolver;
import sparrow.elt.jaxb.MODULEType;
import sparrow.elt.jaxb.PARAMType;


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
public class ConfigParamImpl implements ConfigParam, DBVariableObserver,
		TokenResolver {

	private Map paramTable;

	private static CryptoUtil cryptoUtil;
	private static final TokenResolver SYS_PROP = new TokenResolverImpl();

	private static final String APP_PROPERTIES = "app.properties";
	private static final String PROP_KEY = "prop.key";
	private static final String PROP_TOKEN = "prop.token";	
	

	private Map dbVariableMap;

	private String currentKey = null;

	/**
	 * 
	 * @param module
	 *            MODULEType
	 */
	ConfigParamImpl(MODULEType module) {
		cryptoUtil = CryptoUtil.getInstance(module.getPROCESSID());
		init(module.getMODULEPARAM().getPARAM());
	}

	/**
	 * 
	 * @param paramTypes
	 *            List
	 */
	ConfigParamImpl(List paramTypes) {
		init(paramTypes);
	}

	private void init(List paramTypes) {
		dbVariableMap = new HashMap();
		if (paramTypes != null) {
			paramTable = isModuleConfig() ? new KeyTransformerMap(paramTypes
					.size(), "@") : new HashMap(paramTypes.size());
			this.bind(paramTypes);
		}
	}

	/**
   *
   */
	private void bind(List paramTypes) {
		if (!paramTypes.isEmpty()) {
			for (Iterator iter = paramTypes.iterator(); iter.hasNext();) {
				PARAMType item = (PARAMType) iter.next();
				currentKey = item.getNAME().trim();
				paramTable.put(currentKey, scanVars(item.getVALUE(),
						isModuleConfig(), paramTable));
			}
		}
	}

	/**
	 * 
	 * @param value
	 *            String
	 * @return String
	 */
	public static String scanVars(String value, boolean isModuleConfig,
			Map paramTable) {

		if (value.indexOf(TokenResolverImpl.SYS_PROP_TOKEN) != -1) {
			value = SparrowUtil.replaceTokens(value,
					TokenResolverImpl.SYS_PROP_TOKEN,
					TokenResolverImpl.SYS_PROP_TOKEN, SYS_PROP);
		}

		if (value.indexOf(ModuleConfigImpl.TOKEN_IDENTIFIER) != -1) {
			value = (isModuleConfig) ? SparrowUtil.replaceTokens(value,
					paramTable) : SparrowUtil.replaceTokens(value,
					Constants.TOKEN_START, Constants.TOKEN_END,
					ModuleConfigImpl.MODULE_PROP);
		}
		if (value.indexOf("{ENC}") == 0) {
			String[] discover = urts(value.substring(5));
			try {
				value = cryptoUtil.fff(value.substring(5), discover[1],
						discover[0]);
			} catch (Exception ex) {
				throw new InitializationException(ex);
			}
		}
		// if (value.indexOf(Constants.VARIABLE_IDENTIFIER) != -1) {
		// SparrowUtil.replaceTokens(value, this);
		// }

		return value;
	}

	/**
	 * getTokenValue
	 * 
	 * @param token
	 *            String
	 * @return String
	 */
	public String getTokenValue(String token) {
		if (token.indexOf("$") > 0) {
			String dpCol[] = token.split("[$]");

			if (!dbVariableMap.containsKey(dpCol[0])) {
				dbVariableMap.put(dpCol[0], new ArrayList());
				DBVariableObservable.getInstance().addObserver(dpCol[0], this);
			}
			((ArrayList) dbVariableMap.get(dpCol[0])).add(currentKey + "|"
					+ dpCol[1]);
		}
		return "";
	}

	/**
	 * 
	 * @param name
	 *            String
	 * @return String
	 */
	public String getParameterValue(String name) {
		return (String) paramTable.get(name);
	}

	/**
	 * isParameterExist
	 * 
	 * @param name
	 *            String
	 * @return boolean
	 */
	public boolean isParameterExist(String name) {
		return (paramTable.get(name) != null && !paramTable.get(name)
				.toString().trim().equals(""));
	}

	/**
	 * 
	 * @return boolean
	 */
	protected boolean isModuleConfig() {
		return false;
	}

	/**
	 * populateVariable
	 * 
	 * @param dpName
	 *            String
	 * @param rr
	 *            ResultRow
	 */
	public void populateVariable(String dpName, ResultRow rr) {
		List l = (ArrayList) dbVariableMap.get(dpName);
		String key = null;
		String[] split = null;
		for (Iterator it = l.iterator(); it.hasNext();) {
			key = (String) it.next();
			split = key.split("[|]");
			// paramTable.put(key,)
		}
	}

	/**
	 * 
	 * @param dfg
	 *            String
	 * @return String[]
	 */
	private static final String[] urts(String dfg) {
		int aaa = Arrays.binarySearch(CryptoUtil.rrr, dfg.charAt(0)) + 1;
		int sss = aaa - 1;
		dfg = dfg.substring(1);
		String jjj = dfg.substring(aaa, aaa + 8);
		String zzz = dfg.substring(0, sss)
				+ dfg.substring(aaa + 8, dfg.length());
		return new String[] { jjj, zzz };
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
	private static class TokenResolverImpl implements TokenResolver {

		private Properties userProperties = readProperties();
		private String propKey = null;
		
		public static String SYS_PROP_TOKEN = Constants.SYS_PROP_TOKEN;
		private static final String PROP_KEY_RPLC_TOKEN = "{"+PROP_KEY+"}";

		/**
		 *
		 */
		TokenResolverImpl() {
			String sysPropToken = getTokenValue(PROP_TOKEN);
			this.propKey = System.getProperty(PROP_KEY);
			if (sysPropToken != null) {
				SYS_PROP_TOKEN = sysPropToken;
			}
		}

		/**
		 * 
		 * @param token
		 *            String
		 * @return String
		 */
		public String getTokenValue(String token) {
			if(propKey!=null && token.indexOf(PROP_KEY_RPLC_TOKEN)!=-1){
				token = SparrowUtil.replace(token, PROP_KEY_RPLC_TOKEN, propKey);
			}
			String val = System.getProperty(token);
			if (val == null && userProperties != null) {
				val = userProperties.getProperty(token);
			}
			return val;
		}

		/**
		 * 
		 * @return Properties
		 */
		Properties readProperties() {
			Properties p = null;
			if (System.getProperty(APP_PROPERTIES) != null) {
				p = loadProperties();
			}
			return p;
		}

		
		/**
		 * 
		 * @param p
		 */
		private Properties loadProperties() {
			InputStream is = null;
			Properties p = new Properties();
			try {
				String[] propFiles = System.getProperty(APP_PROPERTIES).split(
						"[,]");

				for (int i = 0; i < propFiles.length; i++) {
					is = SparrowUtil.getFileAsStream(propFiles[i]);
					p.load(is);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				p = null;
			} finally {
				if (is != null) {
					try {
						is.close();
					} catch (IOException ex1) {
						ex1.printStackTrace();
					}
				}
			}
			return p;
		}

	}

}
