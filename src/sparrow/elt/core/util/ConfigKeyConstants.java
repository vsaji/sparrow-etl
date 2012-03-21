package sparrow.elt.core.util;

public interface ConfigKeyConstants {

	/**
	 *
	 */
	public static final String PARAM_SPARROW_CYCLE_COUNT = "sparrow.cycle.count";

	public static final String PARAM_SPARROW_IGNORE_EE = "sparrow.ignore.extractor.exception";
	
	public static final String PARAM_SPARROW_CYCLE_INTERVAL = "sparrow.cycle.interval";

	public static final String PARAM_DP_CACHE_TYPE = "cache.type";

	public static final String PARAM_CACHE_INDEX = "cache.index.columns";

	public static final String PARAM_RESOURCE = "resource";

	public static final String PARAM_CACHE_FLUSH_EVENT = "cache.flush.event";

	public static final String PARAM_DP_CACHE_REFRESH_TIME = "cache.refresh.interval";

	public static final String PARAM_QUERY = "query";

	public static final String PARAM_PROCEDURE = "procedure";

	public static final String PARAM_CONNECTION = "connection.name";

	public static final String PARAM_PRIMARY_KEYS = "driver.primary.keys";

	public static final String PARAM_USE_DB = "use.db";

	public static final String PARAM_LOOKUP_LISTENER = "lookup.listener";

	public static final String PARAM_COLUMNS = "select.columns";

	public static final String PARAM_SINGLE_REF = "single.reference";

	public static final String PARAM_DATA_PROVIDER = "data.provider";

	public static final String PARAM_KEY_NAME = "key.name";

	public static final String PARAM_COLUMN_NAME = "column.name";

	public static final String PARAM_BATCH_SIZE = "batch.size";

	public static final String PARAM_BATCH_PER_REQ = "batch.per.request";

	public static final String PARAM_SERVICE_INTERVAL = "service.interval";

	public static final String PARAM_PROCESS_NAME = "sparrow.process.name";

	public static final String PARAM_FILE_PATH = "file.path";

	public static final String PARAM_FILE_SUPPRESS_HEADER = "file.skip.row";

	public static final String PARAM_FILE_DELIMITER = "file.delimiter";

	public static final String PARAM_FILE_NAME = "file.name";

	public static final String PARAM_FILE_PROCESSOR = "file.processor";

	public static final String PARAM_POST_PROCESS = "post.process";

	public static final String PARAM_POST_PROCESS_DIR = "post.process.dir";

	public static final String PARAM_VALIDATION_RQD = "validation.required";

	public static final String PARAM_CREAT_PREF = "create.preference";

	public static final String PARAM_REJECTION_REPORT_TYPE = "rejection.report.type";

	public static final String PARAM_REJECTION_REPORT_PATTERN = "rejection.report.pattern";

	public static final String PARAM_REJECTION_REPORT_SRC = "rejection.report.source";

	public static final String PARAM_REJECTION_REPORT_FILE = "rejection.report.file.name";

	public static final String PARAM_WAIT_4_FILE = "wait.for.file";

	public static final String PARAM_IGN_COLLEN_MM = "ignore.collen.mismatch";

	public static final String PARAM_POLLING_INTERVAL = "polling.interval";

	public static final String PARAM_POLLING_COUNT = "polling.count";

	public static final String PARAM_START_LINE_NUM = "start.line.number";

	public static final String PARAM_COLUMN_COUNT = "column.count";

	public static final String PARAM_FETCH_SIZE = "fetch.size";

	public static final String PARAM_RESULT_WRAP = "result.wrap";

	public static final String PARAM_FAIL_DIR = "fail.dir";

	public static final String PARAM_CONSUMER_CNT = "consumer.count";

	public static final String PARAM_LOOKUP_KEYS = "lookup.keys";

	public static final String PARAM_FILE_NAME_RESOLVER = "file.name.resolver";

	public static final String PARAM_COLUMN_LEN_DEF = "column.length.definition";

	public static final String PARAM_JMS_TYPE = "jms.type";

	public static final String PARAM_SEC_EXIT = "security.exit";

	public static final String PARAM_MQ_SEC_EXIT_CLASS = "security.exit.class";

	public static final String PARAM_ERR_LOG_DIR = "error.log.dir";

	public static final String PARAM_RETRY_LIMIT = "retry.limit";

	public static final String PARAM_PAUSE_TIME = "pause.time";

	public static final String PARAM_HEADER_PROPS = "header.properties";

	public static final String PARAM_DEST_NAME = "destination.name";

	public static final String PARAM_HOST_NAME = "host.name";

	public static final String PARAM_DEST_DIR = "destination.dir";

	public static final String PARAM_COLUMN_DEF = "column.definition";

	public static final String PARAM_COLUMN_DEF_VALUE = "column.definition.value";

	public static final String PARAM_TRIM_VALUE = "trim.value";

	public static final String PARAM_FILE_CLOSE_EVENT = "file.close.event";

	public static final String PARAM_ASYNC_WRITE = "async.write";

	public static final String PARAM_FILE_OUTPUT = "file.output";

	public static final String PARAM_SEMAPHORE_CHECK = "sparrow.semaphore.check";

	public static final String PARAM_KEY_ASSIGNER = "sparrow.keyassigner";

	public static final String PARAM_ALLORNONE = "all.or.none";

	public static final String PARAM_TOKEN_RESOLVER = "token.resolver";

	public static final String PARAM_HEADER_ROW = "header.row";

	public static final String PARAM_BODY_ROW = "body.row";

	public static final String PARAM_CONTEXT = "context";

	public static final String PARAM_LOOKUP = "lookup";

	public static final String PARAM_SECURE_CON = "secure.connection";

	public static final String PARAM_FOOTER_ROW = "footer.row";

	public static final String PARAM_FLUSH_TYPE = "flush.type";

	public static final String PARAM_PROVIDER_URL = "provider.url";

	public static final String PARAM_INITIAL_CONTEXT = "initialcontext.factoy";

	public static final String PARAM_SECURITY_PRINCIPAL = "security.principal";

	public static final String PARAM_SECURITY_CREDENTIALS = "security.credentials";

	public static final String PARAM_CONNECTION_FACTORY = "connection.factory";

	public static final String PARAM_SESS_TRANS = "session.transacted";

	public static final String PARAM_MSG_ACK_TYPE = "message.ack.type";

	public static final String PARAM_MSG_LSTNR = "message.listener";

	public static final String PARAM_STORE_TYPE = "message.store.type";

	public static final String PARAM_STORE_SRC = "message.store.source";

	public static final String PARAM_DEADLTTR_Q = "deadletter.queue";

	public static final String PARAM_EXEMPT_TRANS = "exempt.trans";

	public static final String PARAM_BACK_ON_EXIST = "backup.on.exist";

	public static final String PARAM_WRITER_NAMES = "writer.names";

	public static final String PARAM_OUT_FILE = "out.file";

	public static final String PARAM_FILE_LIST = "file.list";

	public static final String PARAM_ATTACHMENT = "attachment";

	public static final String PARAM_DRIVER_CLASS_NAME = "driver.classname";

	public static final String PARAM_CONNECTION_URL = "connection.url";

	public static final String PARAM_USER_NAME = "user.name";

	public static final String PARAM_SECURE_STREAM = "stream";

	public static final String PARAM_PASSWORD = "password";

	public static final String PARAM_POOL_SIZE = "pool.size";

	public static final String PARAM_MAX_WAIT = "max.wait";

	public static final String PARAM_MAX_IDEL = "max.idle";

	public static final String PARAM_MIN_IDEL = "min.idle";

	public static final String PARAM_POOL_ON_START = "pool.on.start";

	public static final String PARAM_AUTO_COMMIT = "default.autocommit";

	public static final String PARAM_GROUP_OUTPUT = "group.output";

	public static final String PARAM_IGNORE_NULL = "ignore.null";

	public static final String PARAM_PLACEHOLDER = "placeholder";

	public static final String PARAM_REPLACE_NULL_WITH = "replace.null.with";

	public static final String PARAM_TEMPLATE_TYPE = "template.type";

	public static final String PARAM_EMAIL_TO = "email.to";

	public static final String PARAM_EMAIL_FROM = "email.from";

	public static final String PARAM_EMAIL_CC = "email.cc";

	public static final String PARAM_EMAIL_BCC = "email.bcc";

	public static final String PARAM_EMAIL_SUBJECT = "email.subject";

	public static final String PARAM_EMAIL_CONTENT = "email.content";

	public static final String PARAM_UNIX_SHELL = "unix.shell";

	public static final String PARAM_EMAIL_MODE = "email.mode";

	public static final String PARAM_EMAIL_CONTENT_TYPE = "email.content.type";

	public static final String PARAM_EMAIL_CONDITION_TYPE = "email.condition.type";

	public static final String PARAM_EMAIL_CONDITION_IF_VALUE = "email.condition.if.value";

	public static final String PARAM_EMAIL_CONDITION_IF_SUBJECT = "email.condition.if.subject";

	public static final String PARAM_EMAIL_CONDITION_IF_CONTENT = "email.condition.if.content";

	public static final String PARAM_EMAIL_CONDITION_ELSE_SUBJECT = "email.condition.else.subject";

	public static final String PARAM_EMAIL_CONDITION_ELSE_CONTENT = "email.condition.else.content";

	public static final String PARAM_EMAIL_SMTP_HOST = "mail.smtp.host";

	public static final String PARAM_EMAIL_SMTP_PORT = "mail.smtp.port";

	public static final String PARAM_PRESERVE_FILE = "preserve.file";

	public static final String PARAM_COMPRESS = "compress";

	public static final String PARAM_EXPRESSION = "expression";

	public static final String PARAM_CONTEXT_FILE = "context.file";

	public static final String PARAM_CONTEXT_LOC = "context.location";

	public static final String PARAM_SUP_NORSLT_DBCALL = "suppress.noresult.dbcall";

	public static final String PARAM_FROM_NAME = "from.name";

	public static final String PARAM_TO_NAME = "to.name";

	public static final String PARAM_FROM_DIR = "from.dir";

	public static final String PARAM_TO_DIR = "to.dir";

	public static final String PARAM_DIR_NAME = "dir.name";

	public static final String PARAM_CHANNEL = "channel";

	public static final String PARAM_ACTION = "action";

	public static final String PARAM_FTP_MODE = "ftp.mode";

	public static final String PARAM_PORT = "port";

	public static final String PARAM_ON_FAILURE = "on.failure";

	public static final String PARAM_PRSRV_STORE = "preserve.store";

	public static final String PARAM_SCRIPT_VALUE = "script.value";

	public static final String PARAM_SCRIPT_INPUT = "script.input";

	public static final String PARAM_SCRIPT_LANG = "script.lang";

	public static final String PARAM_ATTACHMENT_IF = "attachment.if";

	public static final String PARAM_ATTACHMENT_ELSE = "attachment.else";

	public static final String PARAM_TMPLT_FILE = "template.file";

	public static final String PARAM_MAX_ROW = "max.row";
	
	public static final String PARAM_CREATE_ZEROCOUNT_FILE = "create.zero.count.file";
	
	public static final String PARAM_MAX_ROW_EXCEED = "max.row.exceed";

	public static final String PARAM_CSV_TEMPLATE_FILE = "csv.template.file";
	
	public static final String PARAM_CONDITION = "condition";	


}
