package sparrow.etl.impl.writer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import sparrow.etl.core.config.ConfigParam;
import sparrow.etl.core.config.SparrowDataWriterConfig;
import sparrow.etl.core.context.ContextVariables;
import sparrow.etl.core.exception.DataWriterException;
import sparrow.etl.core.exception.InitializationException;
import sparrow.etl.core.util.AsyncRequestProcessor;
import sparrow.etl.core.util.ConfigKeyConstants;
import sparrow.etl.core.util.Constants;
import sparrow.etl.core.util.CounterObject;
import sparrow.etl.core.util.RequestListener;
import sparrow.etl.core.util.SparrowUtil;
import sparrow.etl.core.util.TokenResolver;
import sparrow.etl.core.vo.DataOutputHolder;
import sparrow.etl.core.writer.AbstractDataWriter;


/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */
public class FileWriter
    extends AbstractDataWriter {

  private static final String FILE_WRITE_TYPE_SINGLE = "single";
  private static final String FILE_WRITE_TYPE_MULTIPLE = "multiple";
  private static final String DEFAULT_FLUSH_TYPE = "request";
  private static final String DEFAULT_CRT_PREF = "always";
  private static final String CRT_PREF_WHN_REC_EXIST = "when.record.exist";

  private IFileWriter fWriter = null;
  private boolean multipleFileOut = false;
  private boolean bodyExist = false;
  private boolean backupOnExist = false;
  private String flushType, fileCloseEvent = null;
  private String fileName, filePath = null;
  private String tempFileName = null;
  private String bodyPattern = "";
  private String createPreference = null;

  private final List requestContainer;
  private final CounterObject co;
  private final ConfigParam cp;
  private final FileWriterHelper helper;

  /**
   *
   * @param config SparrowDataWriterConfig
   */
  public FileWriter(SparrowDataWriterConfig config) {

    super(config);
    cp = config.getInitParameter();

    SparrowUtil.validateParam(new String[] {ConfigKeyConstants.PARAM_FILE_PATH,
                            ConfigKeyConstants.PARAM_FILE_NAME}
                            , "FileWriter[" + WRITER_NAME + "]",
                            cp);

    helper = FileWriterHelper.getInstance(WRITER_NAME);
    requestContainer = helper.getRequestList();
    co = helper.getCounterObject();
  }

  /**
   * destroy
   */
  public void destroy() {
  }

  /**
   * endCycle
   */
  public void endCycle() {
    if (Constants.END_CYCLE.equals(flushType)) {
      fWriter.flush();
    }
  }

  /**
   *
   * @return boolean
   */
  public boolean isInTransaction() {
    return false;
  }

  /**
   * initialize
   */
  public void initialize() {

    bodyExist = cp.isParameterExist(ConfigKeyConstants.PARAM_BODY_ROW);
    bodyPattern = SparrowUtil.performTernary(cp,
                                           ConfigKeyConstants.
                                           PARAM_BODY_ROW, "");
    backupOnExist = SparrowUtil.performTernary(cp,
                                             ConfigKeyConstants.
                                             PARAM_BACK_ON_EXIST
                                             , false);
    createPreference = SparrowUtil.performTernary(cp,
                                                ConfigKeyConstants.
                                                PARAM_CREAT_PREF
                                                , DEFAULT_CRT_PREF);

    initFileHandler();

    try {

      if (!helper.isHeaderFooterResolved()) {
        setHeaderFooter();
        writeHeader();
        helper.setHeaderFooterResolved(true);
      }
    }
    catch (Exception ex) {
      throw new InitializationException(
          "Exception occured while writing header [" + helper.getHeaders() +
          "]");
    }
  }

  /**
   *
   */
  private void initFileHandler() {

    /*******************************************************************/
    String fileOut = SparrowUtil.performTernary(cp,
                                              ConfigKeyConstants.
                                              PARAM_FILE_OUTPUT,
                                              FILE_WRITE_TYPE_SINGLE);

    boolean asyncWrite = SparrowUtil.performTernary(cp,
                                                  ConfigKeyConstants.
                                                  PARAM_ASYNC_WRITE, false);

    /*******************************************************************/
    filePath = cp.getParameterValue(ConfigKeyConstants.PARAM_FILE_PATH);
    filePath = replaceToken(filePath);

    fileName = cp.getParameterValue(ConfigKeyConstants.PARAM_FILE_NAME);

    flushType = SparrowUtil.performTernary(cp,
                                         ConfigKeyConstants.PARAM_FLUSH_TYPE,
                                         DEFAULT_FLUSH_TYPE);
    fileCloseEvent = SparrowUtil.performTernary(cp,
                                              ConfigKeyConstants.
                                              PARAM_FILE_CLOSE_EVENT,
                                              Constants.END_APP);

    /*******************************************************************/
    tempFileName = filePath + "/" + WRITER_NAME + ".tmp";

    if (fileOut.equals(FILE_WRITE_TYPE_MULTIPLE)) {
      fWriter = (asyncWrite) ?
          (IFileWriter)new AsyncMultiFileWriter(filePath, fileName) :
          new SynchronizedMultiFileWriter(filePath, fileName);
      multipleFileOut = true;
      config.getContext().setAttribute(WRITER_NAME, filePath);
    }
    else {
      fileName = replaceToken(fileName);

      String file = filePath + "/" + fileName;

      fWriter = (asyncWrite) ?
          (IFileWriter)new AsynchFileWriter(file) :
          ( (flushType.equals(DEFAULT_FLUSH_TYPE)) ?
           new PerRequestFileWriter(file) :
           new GenericFileWriter(file));

      config.getContext().setAttribute(WRITER_NAME, file);
    }
  }

  /**
   *
   * @param cp ConfigParam
   */
  private void setHeaderFooter() {

    String hdr = SparrowUtil.performTernary(cp,
                                          ConfigKeyConstants.PARAM_HEADER_ROW, null);
    String  footer= SparrowUtil.performTernary(cp,
                                             ConfigKeyConstants.
                                             PARAM_FOOTER_ROW, null);
    /*********************************************************/
    ArrayList ftr = new ArrayList();
    if (footer == null) {
        for (int i = 1;
             (footer = cp.getParameterValue(ConfigKeyConstants.PARAM_FOOTER_ROW +
                                         "." + i)) != null; i++) {
        	ftr.add(footer);
        }
      }
      else {
    	  ftr.add(footer);
      }
    helper.setFooter(ftr);
    helper.setFooterExist(!ftr.isEmpty());
    /*********************************************************/
    ArrayList hr = new ArrayList();

    if (hdr == null) {
      for (int i = 1;
           (hdr = cp.getParameterValue(ConfigKeyConstants.PARAM_HEADER_ROW +
                                       "." + i)) != null; i++) {
        hr.add(hdr);
      }
    }
    else {
      hr.add(hdr);
    }

    helper.setHeaders(hr);
    helper.setHeaderExist(!hr.isEmpty());
    /*********************************************************/    
  }

  /**
   * writeData
   *
   * @param data DataOutputHolder
   */
  public final int writeData(DataOutputHolder data, int statusCode) throws
      DataWriterException {

    if (KEY_NAME != null && data.getObject(KEY_NAME) == null) {
      logger.info("Skipping writer [" + WRITER_NAME +
                  "] - Content is null for key.name [" + KEY_NAME + "]");
      return STATUS_SUCCESS;
    }

    
    try {

      String value = (bodyExist) ?
          SparrowUtil.replaceTokens(bodyPattern, data.getTokenValue(),
                                  TOKEN_RESOLVER) :
          getOutputString(data.getObject(KEY_NAME));

      if (value == null) {
        logger.info("Writer [" + WRITER_NAME + "] value is empty or null");
      }

      fWriter.write(value, data.getTokenValue());
      this.calcRecCount(value);
      helper.setContentExist(true);
    }
    catch (Exception ex) {
      throw new DataWriterException(
          "Exception occured while executing query [" + WRITER_NAME + "]", ex);
    }
    return STATUS_SUCCESS;
  }


  /**
   * 
   * @param value
   */
  private void calcRecCount(String value){
	  if(value!=null && value.indexOf("\n")!=-1){
		  for(int f=0;((f=value.indexOf("\n"))!=-1); ){
			  	co.increment();
				value = value.substring(f+1);
			}
		  co.increment();
	  }else{
		  co.increment();
	  }
  }
  
  /**
   *
   * @param o Object
   * @return String
   */
  protected String getOutputString(Object o) {
    return (String) o;
  }

  /**
   * endOfProcess
   * This method will be called only once even though if there are more than one
   * instance exists for the same writer
   * @param flag int
   */
  public final void endOfProcess(int flag) {
    try {

      boolean isProcessEnd = false;

      if (Constants.EP_END_APP == flag &&
          Constants.END_APP.equals(fileCloseEvent)) {
        //     logger.info("DEFAULT_FILE_CLOSE_EVENT.equals(" + fileCloseEvent + ")");
        setRecordCount();
        writeFooter();
        fWriter.flush();
        fWriter.close();
        fWriter.move();
        isProcessEnd = true;
      }
      else if (
          Constants.EP_NO_RECORD == flag &&
          Constants.END_PROCESS.equals(fileCloseEvent)) {
        //    logger.info("FILE_CLOSE_EVENT_END_PROCESS.equals(" + fileCloseEvent +
        //               ")");
        setRecordCount();
        writeFooter();
        fWriter.flush();
        fWriter.close();
        fWriter.move();
        isProcessEnd = true;
      }

      if (isProcessEnd) {
        //No record fetched && create file only when record exist.
        if (!helper.isContentExist() &&
            CRT_PREF_WHN_REC_EXIST.equals(createPreference)) {
          fWriter.delete();
        }
      }

    }
    catch (Exception ex) {
      logger.error("Exception occured in endOfProcess", ex);
    }
  }

  /**
   *
   */
  private void writeFooter() throws Exception {
	  writeFooter(fWriter);
  }

  /**
   * 
   * @return
 * @throws Exception 
   */
  private void writeFooter(IFileWriter ifw) throws Exception{
	  if (helper.isFooterExist() && !multipleFileOut) {
	      for (Iterator it = helper.getFooter().iterator(); it.hasNext(); ) {
	        String temp = (String) it.next();
	        temp = replaceToken(temp);
	        ifw.write(temp);
	      }
	    }
  }
  
  
  /**
   * 
   * @return
   */
  private void writeFooter(PrintWriter p){
	  if (helper.isFooterExist() && !multipleFileOut) {
	      for (Iterator it = helper.getFooter().iterator(); it.hasNext(); ) {
	        String temp = (String) it.next();
	        temp = replaceToken(temp);
            p.println(temp);
	      }
	    }
  }
  
  /**
   *
   * @throws Exception
   */
  private void writeHeader() throws Exception {

    if (helper.isHeaderExist()) {
      StringBuffer headerAsString = new StringBuffer("");
      for (Iterator it = helper.getHeaders().iterator(); it.hasNext(); ) {
        String tempHdr = (String) it.next();
        tempHdr = replaceToken(tempHdr);
        headerAsString.append(tempHdr).append("\n");
        if (!multipleFileOut) {
          fWriter.write(tempHdr);
        }
      }
      if (headerAsString.length() > 0) {
        headerAsString.deleteCharAt(headerAsString.length() - 1);
      }
      helper.setHeaderAsString(headerAsString.toString());
    }

  }

  /**
   * onError
   *
   * @param writerName String
   * @param ex Exception
   */
  public void onError(String writerName, Exception ex) {
  }

  /**
   * beginCycle
   */
  public void beginCycle() {
  }

  /**
   *
   * <p>Title: </p>
   * <p>Description: </p>
   * <p>Copyright: Copyright (c) 2004</p>
   * <p>Company: </p>
   * @author Saji Venugopalan
   * @version 1.0
   */
  private interface IFileWriter {

    abstract void write(String line, Map driverMap) throws Exception;

    abstract void write(String line) throws Exception;

    abstract void move();

    abstract void delete();

    abstract void flush();

    abstract void close();
  }

  /**
   *
   * <p>Title: </p>
   * <p>Description: </p>
   * <p>Copyright: Copyright (c) 2004</p>
   * <p>Company: </p>
   * @author Saji Venugopalan
   * @version 1.0
   */
  private class GenericFileWriter
      implements IFileWriter {

    protected final PrintWriter writer;
    protected final String actualFile;
//    private Object lock = new Object();

    GenericFileWriter(String actualFile) {
      try {
        this.actualFile = actualFile;
        SparrowUtil.doFileExistAction(actualFile, backupOnExist);
        writer = helper.getTempFileWriter(tempFileName);
      }
      catch (Exception ex) {
        throw new InitializationException(
            "Exception occure while initializing temp file", ex);
      }
    }

    public void write(String line) {
//       synchronized(lock){
      requestContainer.add(line);
//       }
    }

    public void write(String line, Map tokenValues) {
//       synchronized(lock){
      write(line);
//       }
    }

    public void close() {
      writer.close();
    }

    public void flush() {
//      synchronized(lock){
      synchronized (requestContainer) {
        for (Iterator it = requestContainer.iterator(); it.hasNext(); ) {
          writer.println( (String) it.next());
        }
        requestContainer.clear();
      }
      writer.flush();
    }

    /**
     * move
     */
    public void move() {
      if (!helper.isFileMoved()) {
        new File(tempFileName).renameTo(new File(actualFile));
        new File(tempFileName).delete();
        helper.setFileMoved(true);
      }
    }

    /**
     * delete
     */
    public void delete() {
      if (!helper.isFileDeleted()) {
        new File(tempFileName).delete();
        new File(actualFile).delete();
        helper.setFileDeleted(true);
      }
    }
  }

  /**
   *
   * <p>Title: </p>
   * <p>Description: </p>
   * <p>Copyright: Copyright (c) 2004</p>
   * <p>Company: </p>
   * @author Saji Venugopalan
   * @version 1.0
   */
  private class PerRequestFileWriter
      extends GenericFileWriter {

    PerRequestFileWriter(String file) {
      super(file);
    }

    public void write(String line) {
      writer.println(line);
      writer.flush();
    }

    public void flush() {
    }
  }

  /**
   *
   * <p>Title: </p>
   * <p>Description: </p>
   * <p>Copyright: Copyright (c) 2004</p>
   * <p>Company: </p>
   * @author Saji Venugopalan
   * @version 1.0
   */
  private class AsyncMultiFileWriter
      implements IFileWriter, RequestListener {

    final String filePath, fileName;
    final boolean tokenExist;

    final static String LISTENER_2 = "async.listener.2";

    final AsyncRequestProcessor arp = AsyncRequestProcessor.
        createAsynchProcessor(WRITER_NAME);

    AsyncMultiFileWriter(String filePath, String fileName) {
      arp.registerListener(LISTENER_2, this);
      arp.start();
      this.filePath = filePath;
      this.fileName = fileName;
      this.tokenExist = (fileName.indexOf(Constants.VARIABLE_IDENTIFIER) != -1);
      TOKEN_RESOLVER.addTokenAndValue(ContextVariables.RECORD_COUNT, "1");
    }

    public void write(String line) throws Exception {
    }

    public void write(String line, Map tokenValues) throws Exception {
      arp.process(LISTENER_2, new Object[] {line, tokenValues});
    }

    public void close() {
    }

    public void flush() {
    }

    public void endProcess() {
    }

    public void process(Object o) throws Exception {
      String flName = fileName;
      Object[] value = (Object[]) o;

      if (tokenExist) {
        flName = resolveTokens(fileName, TOKEN_RESOLVER, (Map) value[1]);
      }

      PrintWriter writer = new PrintWriter(new FileOutputStream(filePath + "/" +
          flName));
      if (helper.isHeaderExist()) {
        writer.println(helper.getHeaderAsString());
      }
      writer.println(value[0]);
      if (helper.isFooterExist()) {
    	  writeFooter(writer);
      }
      writer.close();
    }

    /**
     * move
     */
    public void move() {
    }

    /**
     * delete
     */
    public void delete() {
    }

  }

  /**
   *
   * <p>Title: </p>
   * <p>Description: </p>
   * <p>Copyright: Copyright (c) 2004</p>
   * <p>Company: </p>
   * @author not attributable
   * @version 1.0
   */
  private class SynchronizedMultiFileWriter
      implements IFileWriter {

    final String filePath, fileName;
    final boolean tokenExist;

    SynchronizedMultiFileWriter(String filePath, String fileName) {
      this.filePath = filePath;
      this.fileName = fileName;
      this.tokenExist = (fileName.indexOf(Constants.VARIABLE_IDENTIFIER) != -1);
      TOKEN_RESOLVER.addTokenAndValue(ContextVariables.RECORD_COUNT, "1");
    }

    public void write(String line) throws Exception {
    }

    public void write(String line, Map tokenValues) throws Exception {

      String flName = fileName;

      if (tokenExist) {
        flName = resolveTokens(fileName, TOKEN_RESOLVER, tokenValues);
      }

      PrintWriter writer = new PrintWriter(new FileOutputStream(filePath + "/" +
          flName));

      if (helper.isHeaderExist()) {
        writer.println(helper.getHeaderAsString());
      }

      writer.println(line);

      if (helper.isFooterExist()) {
    	  writeFooter(writer);
      }

      writer.close();
    }

    public void close() {
    }

    public void flush() {
    }

    /**
     * move
     */
    public void move() {
    }

    /**
     * delete
     */
    public void delete() {
    }

  }

  /**
   *
   * <p>Title: </p>
   * <p>Description: </p>
   * <p>Copyright: Copyright (c) 2004</p>
   * <p>Company: </p>
   * @author Saji Venugopalan
   * @version 1.0
   */
  private class AsynchFileWriter
      extends GenericFileWriter
      implements IFileWriter, RequestListener {

    final static String LISTENER_1 = "async.listener.1";
    final AsyncRequestProcessor arp = AsyncRequestProcessor.
        createAsynchProcessor(WRITER_NAME);

    private Object lock = new Object();

    AsynchFileWriter(String actualFile) {
      super(actualFile);
      arp.registerListener(LISTENER_1, this);
      arp.start();
    }

    public void write(String line) {
      arp.process(LISTENER_1, line);
    }

    public void close() {
      arp.close();
    }

    public void flush() {
      synchronized (lock) {
        writer.flush();
      }
    }

    public void endProcess() {
      if (writer != null) {
        writer.close();
      }
    }

    public void process(Object o) {
      synchronized (lock) {
        writer.println(o.toString());
        if (DEFAULT_FLUSH_TYPE.equals(flushType)) {
          writer.flush();
        }
      }
    }

    public void move() {
      synchronized (lock) {
        super.move();
      }
    }

    public void delete() {
      synchronized (lock) {
        super.delete();
      }
    }
  }

  /**
   *
   * @param fileName String
   * @param tResolver TokenResolver
   * @param tokenValues Map
   * @return String
   */
  private String resolveTokens(String fileName, TokenResolver tResolver,
                               Map tokenValues) {

    String flName = SparrowUtil.replaceTokens(fileName, tResolver);

    if (flName.indexOf(Constants.TOKEN_START) != -1) {
      flName = SparrowUtil.replaceTokens(fileName, tokenValues);
    }

    return flName;
  }

  /**
   *
   */
  private void setRecordCount(){
    TOKEN_RESOLVER.addTokenAndValue(WRITER_NAME+ContextVariables.RECORD_COUNT,
                                      String.valueOf(co.getCount()));
  }


}
