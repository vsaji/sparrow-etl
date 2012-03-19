package sparrow.elt.core.writer;

import java.util.ArrayList;
import java.util.List;

import sparrow.elt.core.config.SparrowDataWriterConfig;
import sparrow.elt.core.context.SparrowContext;
import sparrow.elt.core.exception.DataWriterException;
import sparrow.elt.core.exception.EventNotifierException;
import sparrow.elt.core.exception.InitializationException;
import sparrow.elt.core.lang.function.Expression;
import sparrow.elt.core.lang.function.FunctionUtil;
import sparrow.elt.core.log.SparrowLogger;
import sparrow.elt.core.log.SparrowrLoggerFactory;
import sparrow.elt.core.util.ConfigKeyConstants;
import sparrow.elt.core.util.Constants;
import sparrow.elt.core.util.GenericTokenResolver;
import sparrow.elt.core.util.Sortable;
import sparrow.elt.core.util.SparrowUtil;
import sparrow.elt.core.vo.DataOutputHolder;


/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */
public abstract class AbstractDataWriter
    implements DataWriter {

  protected final SparrowDataWriterConfig config;
  protected final String KEY_NAME;
  protected final String WRITER_NAME;
  protected final String RESOURCE_NAME;
  protected final boolean SINGLE_REFERENCE;
  protected final GenericTokenResolver TOKEN_RESOLVER;

  private boolean inTrans = false;

  protected static final SparrowLogger logger = SparrowrLoggerFactory.
      getCurrentInstance(
      AbstractDataWriter.class);

  /**
   *
   * @param config SparrowDataWriterConfig
   */
  public AbstractDataWriter(SparrowDataWriterConfig config) {
    this.config = config;
    this.KEY_NAME = config.getInitParameter().getParameterValue(
        ConfigKeyConstants.PARAM_KEY_NAME);
    this.WRITER_NAME = config.getName();
    this.RESOURCE_NAME = config.getInitParameter().getParameterValue(
        ConfigKeyConstants.PARAM_RESOURCE);
    this.SINGLE_REFERENCE = SparrowUtil.performTernary(config.getInitParameter(),
        ConfigKeyConstants.PARAM_SINGLE_REF, false);
    this.TOKEN_RESOLVER = (config.getInitParameter().isParameterExist(
        ConfigKeyConstants.
        PARAM_TOKEN_RESOLVER)) ?
        (GenericTokenResolver) SparrowUtil.createObject(config.getInitParameter().
        getParameterValue(
        ConfigKeyConstants.PARAM_TOKEN_RESOLVER), SparrowContext.class,
        config.getContext()) : GenericTokenResolver.getInstance(config.getContext());

  }

  /**
   *
   * @param data DataOutputHolder
   * @param statusCode int
   * @throws DataWriterException
   * @return int
   */
  public abstract int writeData(DataOutputHolder data, int statusCode) throws
      DataWriterException;

  /**
   * getWriterConfig
   *
   * @return SparrowDataWriterConfig
   */
  public final SparrowDataWriterConfig getWriterConfig() {
    return config;
  }

  /**
   *
   * @param writerName String
   * @param ex Exception
   */
  public void onError(String writerName, Exception ex) {
  }

  /**
   *
   * @return SparrowContext
   */
  protected final SparrowContext getContext() {
    return config.getContext();
  }

  /**
   * destroy
   */
  public void destroy() {
  }

  /**
   *
   * @return boolean
   */

  public boolean endRequest() {
    return true;
  }

  /**
   *
   * @return boolean
   */
  public boolean startRequest() {
    return true;
  }

  /**
   *
   */
  public boolean isInTransaction() {
    return inTrans;
  }

  public void setInTransaction(boolean trans) {
    inTrans = trans;
  }

  /**
   * initialize
   */
  public void initialize() {
  }

  /**
   * requestPostCommit
   *
   * @return boolean
   */
  public void endOfProcess(int flag) {
  }

  /**
   * endCycle
   */
  public void endCycle() throws EventNotifierException {

  }

  /**
   * beginCycle
   */
  public void beginCycle() throws EventNotifierException {
  }

  /**
   *
   * @throws EventNotifierException
   */
  public void beginApplication() throws EventNotifierException {
  }

  /**
   *
   * @throws EventNotifierException
   */
  public void endApplication() throws EventNotifierException {
  }

  /**
   *
   * @return int
   */
  public int getPriority() {
    return Sortable.PRIORITY_ABOVE_MEDIUM;
  }

  /**
   *
   * @param value String
   * @return String
   */
  protected String replaceToken(String value) {

	  return SparrowUtil.evaluateAndReplace(value, TOKEN_RESOLVER);
	  
	    /*String val = (value.indexOf(Constants.VARIABLE_IDENTIFIER) != -1) ?
	        SparrowUtil.replaceTokens(value, TOKEN_RESOLVER) : value;
	
	    if (val.indexOf(Constants.FUNCTION_TOKEN) != -1) {
	      val = scanAndExecuteFunctions(val);
	    }
	    return val;
	     */  
  }

  /**
   *
   * @param values0 String
   * @return String
 
  protected String scanAndExecuteFunctions(String value) {
    List temp = new ArrayList();
    value = FunctionUtil.resolveFunctions(value, temp);
    Expression[] functions = FunctionUtil.getFunctions(temp);
    value = FunctionUtil.executeFunction(value, TOKEN_RESOLVER.getAllTokens(),
                                         functions);
    return value;
  }
  */
  
  /**
   *
   * @return String[]
   */
  protected String[] getFileNamesFromFileWriter() {
    return getFileNamesFromFileWriter(config.getInitParameter().
                                      getParameterValue(
        ConfigKeyConstants.PARAM_FILE_LIST));
  }

  /**
   *
   * @return String[]
   */
  /**
   *
   * @return String[]
   */
  protected String[] getFileNamesFromFileWriter(String paramVal) {
    String[] input = paramVal.split("[,]");
    String[] files = new String[input.length];
    for (int i = 0; i < input.length; i++) {

      if (input[i].startsWith("/") || input[i].indexOf(":") != -1) {
        files[i] = replaceToken(input[i]);
      }
      else {
        files[i] = (String) config.getContext().getAttribute(input[i]);
        if (files[i] == null) {
          throw new InitializationException("WRITER_NOT_FOUND",
                                            "WRITER [" + input[i] +
                                            "] unable to locate. Either [" +
                                            input[i] +
                                            "] is not a [FILE or ZIP] type.");
//        logger.error("WRITER [" + writers[i] + "] unable to locate. Either [" +
//                     writers[i] + "] is not a [FILE or ZIP] type.");
        }

      }

    }
    return files;
  }

  /**
   *
   * @param paramVal String
   * @return String
   */
  protected String getFileNameFromFileWriter(String paramVal) {
    String fileName = null;
    if (paramVal.indexOf(Constants.FUNCTION_TOKEN) != -1) {
      fileName = replaceToken(paramVal);
    }else{
      fileName = getFileNamesFromFileWriter(paramVal)[0];
    }
    return fileName;
  }

}
