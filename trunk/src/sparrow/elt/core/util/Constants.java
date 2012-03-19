package sparrow.elt.core.util;

public interface Constants {

  public static final String TOKEN_START = "${";
  public static final String FUNCTION_TOKEN="func_";
  public static final String FUNCTION_REPLACE_TOKEN="&~&";
  public static final String REPLACE_TOKEN_START = "@{";
  public static final String SYS_PROP_TOKEN = "@@";
  public static final String TOKEN_END = "}";
  public static final String VARIABLE_IDENTIFIER = TOKEN_START;
  public static final String RECORD_EXIST = "RE";
  public static final String COMMA_SAPERATE = "[,]";
  public static final String NO_RECORD_EXIST = "NRE";

  public static final int EP_NO_RECORD = 1;
  public static final int EP_END_APP = 2;
  public static final int EP_CORE_ERROR = -1;
  public static final int EP_NO_CONTENT = 0;

  public static final String OUTPUT="$VAR";

  public static final String CONFIG_FILE_PROPERTY_NAME = "spear.config";
  public static final String SPEAR_RUN_MDOE = "spear.run.mode";
  public static final String SPEAR_SHUTDOWN_FORMAT = "spear.shutdown.format";
  public static final String SPEAR_SHUTDOWN = "spear.shutdown";
  public static final String SPEAR_PROCESS_NAME = "spear.process.name";
  public static final String SPEAR_CYCLE_SIZE = "spear.cycle.size";
  public static final String SPEAR_CYCLE_COUNT = "spear.cycle.count";
  public static final String SPEAR_PROPERTIES_FILE = "spear.properties";
  public static final String SPEAR_INSTANCE_NAME = "spear.instance.name";

  public static final String JMS_TYPE_TOPIC = "topic";
  public static final String JMS_TYPE_QUEUE = "queue";

  public static final String SPEAR_SHUTDOWN_FORMAT_EEE_HHMM = "EEE:HH:mm";
  public static final String SPEAR_SHUTDOWN_FORMAT_HHMM = "HH:mm";

  public static final String USER_OBJECT_IMPL = "user.obj.impl";
  public static final String USER_OBJ_POOL_SIZE = "user.obj.pool.size";
  public static final int DEFAULT_USER_OBJ_POOL_SIZE = 150;

  public static final int ERROR_EXIT = 1;
  public static final int NORMAL_EXIT = 0;

  public static final String PREFIX_DATA_TRANSFORMER = "DT_";
  public static final String PREFIX_DATA_WRITER = "DW_";

  public static final String WRITER_OE_IGNORE = "ignore";
  public static final String DRIVER = "driver";
  public static final String IGNORE = "ignore";
  public static final String WRITER_OE_FAIL_ALL = "failall";
  public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
  public static final String DATE_FORMAT_YYYYMMDD = "yyyyMMdd";
  public static final String DB_DATA_PROVIDER_TYPE = "DB";
  public static final String DEFAULT_DELIMITER = ",";
  public static final String TYPE_ZIP = "ZIP";
  public static final String TYPE_FTP = "FTP";
  public static final String TYPE_EMAIL = "EMAIL";
  public static final String END_CYCLE = "end.cycle";
  public static final String BEGIN_CYCLE = "begin.cycle";
  public static final String BEGIN_APP = "begin.app";
  public static final String END_APP = "end.app";
  public static final String END_PROCESS = "end.process";
  public static final String CACHE_TYPE_TIMER_REFRESH = "timer.refresh";
  public static final String CACHE_TYPE_INCREMENTAL = "incremental";
  public static final String CACHE_RESOLVER_KEY = "CACHE";
  public static final String RESULT_WRAP_CONNECTED = "connected";
  public static final String RESULT_WRAP_DISCONNECTED = "disconnected";

  public static final String CORE_THREAD_NAME = "core_thread";

  public static final int MESSAGE_TYPE_TEXT = 1;
  public static final int MESSAGE_TYPE_MAP = 2;
  public static final int MESSAGE_TYPE_OBJECT = 3;
  public static final int MESSAGE_TYPE_STREAM = 4;
  public static final int MESSAGE_TYPE_BYTE = 5;

  public static final String TYPE_FILE = "file";
  public static final String TYPE_DB = "db";

  public static final String REJECTION_SERVICE = "rs";
  public static final String DRIVER_ROW = "dRiVeR_rOw";

  public static final String TYPE_RESOURCES = "resources";
  public static final String TYPE_DATA_PROVIDERS = "data.providers";
  public static final String TYPE_DATA_WRITERS = "data.writers";
  public static final String TYPE_DATA_EXTRACTOR = "data.extractor";
  public static final String SECURITY_EXIT_CLASS =
      "sparrow.elt.core.resource.SpearMQSecurityExit";
  public static final String DEFAULT_LB_POLICY =
      "ROUND-ROBIN";
  public static final String LOAD_TYPE_AUTO = "AUTO";
  public static final String LOAD_TYPE_LAZY = "LAZY";
  public static final String LOAD_TYPE_LAZY_CACHE = "LAZY-CACHE";
  public static final String LOAD_TYPE_AUTO_LAZY = "AUTO-LAZY";
  public static final String STRING = "string";
  public static final String XML = "xml";
  public static final String MESSAGE = "message";
  public static final String OBJECT = "object";
  public static final String GLOBAL = "global";
  public static final String UNIX = "unix";
  public static final String SMTP = "smtp";

  public static final String OS = System.getProperty("os.name").toLowerCase();
  public static final String OS_LINUX = "linux";
  public static final String OS_WINDOW = "window";
  public static final String OS_SUN = "sunos";
  public static final String OS_UNIX = "unix";



}
