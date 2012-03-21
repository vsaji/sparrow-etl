package sparrow.elt.core.initializer;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import sparrow.elt.core.config.DataTransformerConfig;
import sparrow.elt.core.config.DataWritersConfig;
import sparrow.elt.core.config.IConfiguration;
import sparrow.elt.core.config.XMLConfiguration;
import sparrow.elt.core.context.ContextVariables;
import sparrow.elt.core.context.SparrowApplicationContext;
import sparrow.elt.core.dao.util.TransactionEnabledConnectionProvider;
import sparrow.elt.core.exception.ConfigurationReadingException;
import sparrow.elt.core.exception.ExceptionHandlerInitializer;
import sparrow.elt.core.exception.InitializationException;
import sparrow.elt.core.fifo.FIFO;
import sparrow.elt.core.loadbalance.LBPolicyFactory;
import sparrow.elt.core.loadbalance.RequestAssignerPolicy;
import sparrow.elt.core.log.SparrowLogger;
import sparrow.elt.core.log.SparrowrLoggerFactory;
import sparrow.elt.core.monitor.AppMonitor;
import sparrow.elt.core.monitor.CycleMonitor;
import sparrow.elt.core.monitor.EOPMonitor;
import sparrow.elt.core.monitor.EndCycleMonitor;
import sparrow.elt.core.monitor.RequestAndResponseQMonitor;
import sparrow.elt.core.monitor.Watcher;
import sparrow.elt.core.util.AsyncRequestProcessorHelper;
import sparrow.elt.core.util.Constants;
import sparrow.elt.core.util.ContextParam;
import sparrow.elt.core.util.PIDUtil;
import sparrow.elt.core.util.QueueInfo;
import sparrow.elt.core.util.SparrowUtil;
import sparrow.elt.core.util.TokenResolver;


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
public final class ApplicationInitializer {

	/**
   *
   */
	private boolean isInitialized = false;
	private SparrowApplicationContextImpl context = null;
	private IConfiguration config = null;
	private Map transFIFOMap = null;
	private Map writerFIFOMap = null;
	private CycleMonitor cycleMonitor = null;
	private AppMonitor appMonitor = null;
	private EOPMonitor eopMonitor = null;
	private RequestAssignerPolicy writerRequestAssigner = null;

	/**
   *
   */
	private static final SparrowLogger logger = SparrowrLoggerFactory
			.getCurrentInstance(ApplicationInitializer.class);

	/**
   *
   */
	private static final ApplicationInitializer appInitializer = null;

	/**
   *
   */
	private ApplicationInitializer() {
	}

	/**
	 * 
	 * @return ApplicationInitializer
	 */
	public static ApplicationInitializer getInstance() {
		if (appInitializer == null) {
			return new ApplicationInitializer();
		} else {
			return appInitializer;
		}
	}

	/**
   *
   */
	public void initialize() {
		String configFilePath = System
				.getProperty(Constants.CONFIG_FILE_PROPERTY_NAME);
		initialize(configFilePath);
	}

	/**
	 * 
	 * @
	 */
	public void initialize(String configFile) {

		logger.debug("initialize:Overall initialization flag:" + isInitialized);

		try {
			if (!isInitialized) {

				// NOTE: Order of the method calls below should not be changed.
				SparrowUtil.loadImplConfig();

				this.config = this.loadConfiguration(configFile);
				this.context = new SparrowApplicationContextImpl(this.config);

				this.context.setAttribute(ContextVariables.SPARROW_PID, PIDUtil.getPID());
				this.printUsefulInfo();
				
				TransactionEnabledConnectionProvider.setContext(context);

				SparrowUtil.setContextConfigParam(config.getModule());
				// this.initializeNotifiers();
				this.initializeExceptionHandler();
				this.initializeWatcher();
				this.initializeResources();
				this.initializeDataProvider();
				this.initializeServices();
				this.initializeDataTransformerPool();
				this.initializeWriterFIFOs();

				SparrowUtil.setFinalizerQueueInfo(new QueueInfo(writerFIFOMap));

				this.initializeTransformerFIFOs();
				this.initializeRequestProcessor();
				this.initializeRequestFinalizer();
				this.initializeCyclePreDependents();
				this.initializeAsyncRequestProcessor();

				this.addReqResFIFOsToWatcher();
				this.addGlobalAttributes();
				isInitialized = true;
			}
		} catch (Exception e) {
			throw new InitializationException("APP_INITIALIZATION_EXP", e);
		}

	}

	/**
 * 
 */
	private void printUsefulInfo() {
		logger.info("Application [" + context.getAppName()
				+ "] initializing on SPARROW Build ["
				+ SparrowUtil.getImplConfig("sparrow").get("build.version")
				+ "]:PID[" + PIDUtil.getPID() + "]");
		logger.info("Build ["
				+ SparrowUtil.getImplConfig("sparrow").get("build.version")
				+ "] contains following fixes ["
				+ SparrowUtil.getImplConfig("sparrow").get("item.numbers") + "]");
	}

	/**
   *
   */
	private void addGlobalAttributes() {
		context.setAttribute(ContextVariables.FETCH_COUNT, new Integer(0));
		context.setAttribute(ContextVariables.REJECT_COUNT, new Integer(0));
		context.setAttribute(ContextVariables.EXCEPTION_COUNT, new Integer(0));
		context.setAttribute(ContextVariables.FAIL_PROCESS, Boolean.FALSE);
	}

	/**
   *
   */
	private void initializeWatcher() {

		String wEnable = ContextParam
				.getContextParamValue("sparrow.watcher.enable");
		boolean watcherEnable = (wEnable == null) ? false : Boolean.valueOf(
				wEnable).booleanValue();

		if (watcherEnable) {
			logger.info("Initializing Watcher");
			Watcher watcher = new Watcher();
			appMonitor.addObserver(watcher);
		}
	}

	/**
   *
   */
	private void addReqResFIFOsToWatcher() {
		Watcher.registerReporters("REQ_RES_Q_MON",
				new RequestAndResponseQMonitor(transFIFOMap, writerFIFOMap));
	}

	/**
	 * initializeCyclePreDependents
	 */
	private void initializeCyclePreDependents() {
		CycleDependencyInitializer initializer = new CycleDependencyInitializer();
		initializer.initialize(this.context, this.cycleMonitor);
		initializer = null;
	}

	/**
	 * initializeExceptionHandler
	 */
	private void initializeExceptionHandler() {
		ExceptionHandlerInitializer initializer = new ExceptionHandlerInitializer();
		initializer.initialize(this.context);
		initializer = null;
	}

	/**
	 * 
	 * @throws ConfigurationReadingException
	 * @return IConfiguration
	 */
	private IConfiguration loadConfiguration(String configFile)
			throws ConfigurationReadingException {

		IConfiguration config = null;
		try {
			InputStream is = null;

			if (configFile == null || configFile.trim().equals("")) {
				logger
						.warn("sparrow.config property is not set. Searching for sparrow-app-config.xml in the classpath");
				is = SparrowUtil.getFileAsStream("sparrow-app-config.xml");
				if (is == null) {
					throw new ConfigurationReadingException(
							"Configuration file could not be located");
				}
			} else {
				logger.info("sparrow.config=" + configFile);
				is = SparrowUtil.getFileAsStream(configFile);
			}

			if (Boolean.getBoolean("prop.all")) {
				String replaceProp = resolvePropertyUse(is);
				is = new ByteArrayInputStream(replaceProp.getBytes());
			}

			config = new XMLConfiguration(is);
			config.loadConfiguration();
			logger.info("Configuration Loaded");
		} catch (ConfigurationReadingException e) {
			throw e;
		} catch (Exception e) {
			throw new ConfigurationReadingException(e);
		}
		return config;
	}

	/**
	 * 
	 * @return String
	 */
	private String resolvePropertyUse(InputStream is) {
		StringBuffer sb = new StringBuffer();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(is,
					"UTF-8"));
			String line = null;

			while ((line = br.readLine()) != null) {
				{
					sb.append(line);
				}
			}
		} catch (IOException ex) {
			throw new InitializationException(
					"IOException occured while reading configuration file", ex);
		}

		String changedContent = SparrowUtil.replaceTokens(sb.toString(),
				Constants.SYS_PROP_TOKEN, Constants.SYS_PROP_TOKEN,
				new TokenResolver() {
					/**
					 * getTokenValue
					 * 
					 * @param token
					 *            String
					 * @return String
					 */
					public String getTokenValue(String token) {
						return System.getProperty(token);
					}
				});

		return changedContent;
	}

	/**
	 * 
	 * @return SparrowApplicationContext
	 */
	public SparrowApplicationContext getContext() {
		return this.context;
	}

	/**
	 * 
	 * @
	 */
	private void initializeResources() {

		logger.info("Initializing Resources");

		ResourceInitializer intializer = new ResourceInitializer();
		intializer.initialize(context, appMonitor, cycleMonitor);
		intializer = null;
		logger.info("Resources Initialized");
	}

	/**
   *
   */
	private void initializeDataTransformerPool() {

		logger.info("Initializing DataTransformer Implementation");
		DataTransformerPoolInitializer intializer = new DataTransformerPoolInitializer();
		intializer.initialize(context, appMonitor, cycleMonitor);
		logger.info("DataTransformer Implementation initialized");
		intializer = null;
	}

	/**
	 * 
	 * @
	 */
	private void initializeServices() {

		logger.info("Initializing Services");

		ServiceInitializer intializer = new ServiceInitializer(this.context);
		this.appMonitor.addObserver(intializer);
		this.cycleMonitor.addObserver(intializer);
		intializer.initialize();

		logger.info("Services Initialized");

		intializer = null;
	}

	/**
   *
   */
	private void initializeNotifiers() {

		logger.info("Initializing Notifiers");

		NotifierInitializer intializer = new NotifierInitializer();
		intializer.initialize(context, appMonitor, cycleMonitor);
		logger.info("Notifiers Initialized");
		intializer = null;
	}

	/**
   *
   */
	private void initializeDataProvider() {
		DataProviderInitializer intializer = new DataProviderInitializer();
		intializer.initialize(context, appMonitor, cycleMonitor);
		intializer = null;
	}

	/**
	 * 
	 * @
	 */
	private void initializeTransformerFIFOs() {

		logger.info("Initializing DataTransformer FIFOs");

		DataTransformerConfig requestProcConfig = config.getDataTransformer();
		int numberOfThread = requestProcConfig.getThreadCount();
		this.transFIFOMap = new HashMap(numberOfThread);

		for (int i = 0; i < numberOfThread; i++) {
			String qName = Constants.PREFIX_DATA_TRANSFORMER + i;
			FIFO fifo = new FIFO(qName, 200);
			this.transFIFOMap.put(qName, fifo);
		}

		logger.info("[" + numberOfThread
				+ "] DataTransformer FIFOs Initialized");
	}

	/**
	 * 
	 * @return Map
	 **/
	public Map getTransformerFIFOs() {
		return Collections.unmodifiableMap(this.transFIFOMap);
	}

	/**
	 * 
	 * @
	 */
	private void initializeRequestProcessor() {
		logger.info("Initializing DataTransformer threads");
		RequestProcessInitializer requestProcessor = new RequestProcessInitializer();
		requestProcessor.initialize(context, transFIFOMap,
				writerRequestAssigner);
		logger.info("DataTransformer threads Initialized");
		appMonitor.addObserver(requestProcessor);
	}

	/**
   *
   **/

	private void initializeRequestFinalizer() {

		logger.info("Initializing DataWriter threads");

		RequestFinalizerInitializer requestFinalzer = new RequestFinalizerInitializer(
				this.context);
		requestFinalzer.initialize(writerFIFOMap);

		this.appMonitor.addObserver(requestFinalzer);
		this.cycleMonitor.addObserver(requestFinalzer);
		this.eopMonitor.addObserver(requestFinalzer);

		logger.debug("Initializing EndCycleMonitor");

		EndCycleMonitor endCycleMonitor = EndCycleMonitor.getInstance();
		endCycleMonitor.setCycleMonitor(this.cycleMonitor);
		endCycleMonitor.setMaxCount(this.config.getDataTransformer()
				.getThreadCount());
		logger.debug("EndCycleMonitor Initialized");

		logger.info("DataWriter threads Initialized");

	}

	/**
   *
   */
	private void initializeWriterFIFOs() {

		logger.debug("Initializing DataWriter Queues");

		DataWritersConfig writerConfig = config.getDataWriters();
		int numberOfThread = writerConfig.getThreadCount();
		this.writerFIFOMap = new HashMap(numberOfThread);

		for (int i = 0; i < numberOfThread; i++) {
			String qName = Constants.PREFIX_DATA_WRITER + i;
			FIFO fifo = new FIFO(qName, 200);
			this.writerFIFOMap.put(qName, fifo);
		}
		this.writerRequestAssigner = LBPolicyFactory.getWriterLBInstance(
				writerFIFOMap, context);
		logger.info("[" + numberOfThread + "] DataWriter Queues Initialized");
	}

	/**
	 * 
	 * @param cycleMonitor
	 *            CycleMonitor
	 */
	public void setCycleMonitor(CycleMonitor cycleMonitor) {
		if (this.cycleMonitor == null) {
			this.cycleMonitor = cycleMonitor;
		}
	}

	/**
	 * 
	 * @param appMonitor
	 *            AppMonitor
	 */
	public void setAppMonitor(AppMonitor appMonitor) {
		if (this.appMonitor == null) {
			this.appMonitor = appMonitor;
		}
	}

	/**
	 * 
	 * @param appMonitor
	 *            AppMonitor
	 */
	public void setEOPMonitor(EOPMonitor eopMonitor) {
		if (this.eopMonitor == null) {
			this.eopMonitor = eopMonitor;
		}
	}

	/**
   *
   */
	public void initializeAsyncRequestProcessor() {
		appMonitor.addObserver(new AsyncRequestProcessorHelper());
	}

}
