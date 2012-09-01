package sparrow.etl.core.dao.provider.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

import sparrow.etl.core.config.SparrowDataExtractorConfig;
import sparrow.etl.core.config.SparrowDataProviderConfig;
import sparrow.etl.core.context.SparrowContext;
import sparrow.etl.core.dao.impl.CSVRecordSet;
import sparrow.etl.core.dao.impl.CSVRecordSetImpl_Connected;
import sparrow.etl.core.dao.impl.CSVRecordSetImpl_Disconnected;
import sparrow.etl.core.dao.impl.QueryObject;
import sparrow.etl.core.dao.impl.RecordSet;
import sparrow.etl.core.dao.provider.DataProvider;
import sparrow.etl.core.exception.DataException;
import sparrow.etl.core.exception.InitializationException;
import sparrow.etl.core.exception.SparrowRuntimeException;
import sparrow.etl.core.exception.ValidatorException;
import sparrow.etl.core.log.SparrowLogger;
import sparrow.etl.core.log.SparrowrLoggerFactory;
import sparrow.etl.core.util.Constants;
import sparrow.etl.core.util.FileProcessInfo;
import sparrow.etl.core.util.SparrowUtil;


/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */
public class CSVDataProvider
    implements DataProvider {

  private static final SparrowLogger logger = SparrowrLoggerFactory.
      getCurrentInstance(
      CSVDataProvider.class);
  private static final String PPTYPE_MOVEFILE = "movefile";
  private static final String PPTYPE_DELETEFILE = "deletefile";

  private CSVRecordSet result = null;
  private BufferedReader br = null;
  protected File[] files;

  private int fileCnt;
  private int fileInc = 0;
  private int pollingCount = 0;

  private final SparrowContext context;
  private FileProcessInfo fileInfo = null;
  private CSVFileProcessor fileProcessor;
  private String name = null;
  private PostProcessHandler ppHandler = new IgnoreHandler();

  /**
   *
   * @param context SparrowContext
   */
  public CSVDataProvider(SparrowContext context) {
    this.context = context;
  }

  /**
   *
   * @param config SparrowDataExtractorConfig
   */
  public CSVDataProvider(SparrowDataExtractorConfig config) {
    this.context = config.getContext();
    this.name = config.getName();
    this.fileInfo = new FileProcessInfo(name, config.getInitParameter());
  }

  /**
   *
   * @param config SparrowDataProviderConfig
   */
  public CSVDataProvider(SparrowDataProviderConfig config) {
    this.context = config.getContext();
    this.name = config.getName();
    this.fileInfo = new FileProcessInfo(name, config.getInitParameter());
  }


  /**
   *
   */
  public QueryObject getQuery() {
	throw new UnsupportedOperationException("CSVDataProvider doesn't support getQuery method invocation");
  }

/**
   * destory
   */
  public void destory() {
    fileProcessor.close();
  }

  /**
   * getData
   *
   * @return Object
   */
  public RecordSet getData() throws DataException {
    try {

      /**
       * If no files fetched at the first call  or
       * If all file(s) were fetched have been processed
       */

      if (fileInc == fileCnt) {

        if (fileInfo.isWaitForFile()) {

          while (fileCnt == 0) {
            files = lookupFile();
            fileInc = 0;
            result = null;

            if (fileCnt == 0 && pollingCount < fileInfo.getPollingCount()) {
              logger.warn("No matching file found for Data Provider [" + name +
                          "], Pattern [" + fileInfo.getFileSourceDir() + "/" +
                          fileInfo.getFilePattern() + "]");
              Thread.sleep(fileInfo.getPollingInterval());
              pollingCount++;
            }
            else {
              if (fileCnt > 0) {
                logger.info("File matched for Data Provider [" + name +
                            "], Pattern [" + fileInfo.getFileSourceDir() + "/" +
                            fileInfo.getFilePattern() + "]");
                /**
                 * File located before the polling threshhold reach
                 */
                ensureFileCount();
                break;
              }
              else {
                logger.warn("No matching file found for Data Provider [" + name +
                            "], Pattern [" + fileInfo.getFileSourceDir() + "/" +
                            fileInfo.getFilePattern() + "]");

                /**
                 * No file could be located after the polling count completed
                 */
                throwFileNotArrivedException(
                    "No matching file found for Data Provider [" + name +
                    "], Pattern [" + fileInfo.getFileSourceDir() + "/" +
                    fileInfo.getFilePattern() + "]");
              }
            }
          }

        }
        else {
          logger.warn("No matching file found for Data Provider [" + name +
                      "], Pattern [" + fileInfo.getFileSourceDir() + "/" +
                      fileInfo.getFilePattern() + "]");

          throwFileNotArrivedException(
              "No matching file found for Data Provider [" + name +
              "], Pattern [" +
              fileInfo.getFileSourceDir() + "/" +
              fileInfo.getFilePattern() + "]");

        }
      }

      result = (Constants.RESULT_WRAP_CONNECTED.equals(fileInfo.getResultWrap())) ?
          getDataConnected() : getDataDisconnected();

      return result;
    }
    catch (Exception ex) {
      throw new DataException("IOException while processing the file", ex);
    }
  }

  /**
   *
   * @param des String
   */
  private void throwFileNotArrivedException(String des) {
    if (SparrowUtil.isThisCallFromCore()) {
      throw new SparrowRuntimeException("FILE_NOT_ARRIVED", des);
    }
  }

  /**
   *
   * @return RecordSet
   */
  private CSVRecordSet getDataConnected() throws DataException {
    try {
      /**
       * File in process
       */
      if (result != null && !result.isFileFullyRead()) {
        return result;
      }
      else {

        if ( (result != null) && (!ppHandler.isComplete()) &&
            result.isFileFullyRead()) {
          ppHandler.handlePostProcess(files[fileInc]);
        }

        if (result != null && result.isFileFullyRead() &&
            SparrowUtil.isThisCallFromCore()) {
          return new CSVRecordSetImpl_Disconnected();
        }
      }

      while (fileInc < fileCnt) {

        if (validateFile(files[fileInc])) {

          br = new BufferedReader(new InputStreamReader(new FileInputStream(
              files[
              fileInc])));

          fileProcessor.setReader(br);
          result = new CSVRecordSetImpl_Connected(fileProcessor);
          break;
        }

        if ( (fileInc + 1) < fileCnt) {
          fileInc++;
        }
        else {
          break;
        }
      }

    }
    catch (Exception e) {
      logger.error("Exception occured while reading data ", e);
    }
    return result;
  }

  /**
   *
   * @return RecordSet
   */
  private CSVRecordSet getDataDisconnected() throws DataException {

    try {

      /**
       * File in process
       */
      if (result != null && !result.isFileFullyRead()) {
        result.populate();
        return result;
      }
      else {
        if ( (result != null) && (!ppHandler.isComplete()) &&
            (result.isFileFullyRead())) {
          ppHandler.handlePostProcess(files[fileInc]);
        }

        if (result != null && result.isFileFullyRead() &&
            SparrowUtil.isThisCallFromCore()) {
          return new CSVRecordSetImpl_Disconnected();
        }
      }

      while (fileInc < fileCnt) {

        if (validateFile(files[fileInc])) {

          br = new BufferedReader(new InputStreamReader(new FileInputStream(
              files[
              fileInc])));

          fileProcessor.setReader(br);
          result = new
              CSVRecordSetImpl_Disconnected(
              fileProcessor);
          result.populate();

        }

        if ( (fileInc + 1) < fileCnt) {
          fileInc++;
        }
        else {
          break;
        }

      }
    }
    catch (Exception e) {
      logger.error("Exception occured while loading the data", e);
    }
    finally {
      return result;
    }
  }

  /**
   * getName
   *
   * @return String
   */
  public String getName() {
    return name;
  }

  /**
   * initialize
   */
  public void initialize() {
    fileProcessor = (CSVFileProcessor) SparrowUtil.createObject(
        fileInfo.getFileProcessor(), new Class[] {SparrowContext.class,
        FileProcessInfo.class}
        , new Object[] {context, fileInfo});

    files = lookupFile();
    ensureFileCount();

    if (PPTYPE_MOVEFILE.equals(fileInfo.getPostProcess())) {
      ppHandler = new MoveFileHandler();
    }
    else if (PPTYPE_DELETEFILE.equals(fileInfo.getPostProcess())) {
      ppHandler = new DeleteFileHandler();
    }

  }

  /**
   *
   */
  private void ensureFileCount() {
    if (fileCnt > 1) {
      throw new InitializationException("MORE_THAN_ONE_MATCH",
                                        "More than one matching found for Dataprovider [" +
                                        name +
                                        "]. File Count [" + fileCnt +
                                        "]. File Names [" + getFileNames() +
                                        "] ");
    }
  }

  /**
   *
   * @return String
   */
  private String getFileNames() {
    StringBuffer str = new StringBuffer();

    for (int i = 0; i < fileCnt; i++) {
      str.append(files[i].getName()).append(",");
    }
    str.deleteCharAt(str.length() - 1);
    return str.toString();
  }

  /**
   * loadData
   */
  public void loadData() {
  }


  /**
   *
   * @return File[]
   */
  private File[] lookupFile() {
    File[] files;
    try {
      files = fileProcessor.sort(SparrowUtil.getFileList(fileInfo.
          getFileSourceDir(),
          fileInfo.getFilePattern(),
          Constants.TOKEN_START,
          Constants.TOKEN_END, fileProcessor.getFileNameResolver()));

    }
    catch (IOException ex) {
      throw new InitializationException(
          "IOException occured while performing the sort opertion on files.",
          ex);
    }
    if(files==null){
      throw new InitializationException(
          "INVALID_FILE_PATH",
              "Invalid File Path [" + fileInfo.getFileSourceDir() +"]");
    }

    fileCnt = files.length;
    return files;
  }

  /**
   *
   * @param file File
   * @return boolean
   */
  private boolean validateFile(File file) throws ValidatorException {
    boolean isValid = true;
    if (fileInfo.isValidationRequired()) {
      isValid = fileProcessor.validate(file);
      if (!isValid) {
        File targetFile = new File(fileInfo.getFailDir() + "/" + file.getName());
        targetFile.delete();
        file.renameTo(new File(fileInfo.getFailDir() + "/" + file.getName()));
        logger.warn("FILE VALIDATION FAILED [" + files[fileInc].getName() +
                    "]. MOVED TO " + fileInfo.getFailDir());
        throw new ValidatorException("INVALID_FILE",
                                     "FILE VALIDATION FAILED [" +
                                     files[fileInc].getName() +
                                     "]. MOVED TO " + fileInfo.getFailDir());
      }
    }
    return isValid;
  }

  /**
   *
   * @throws CloneNotSupportedException
   * @return Object
   */
  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }

  /**
   * isCacheable
   *
   * @return boolean
   */
  public boolean isCacheable() {
    return false;
  }

  /**
   * applyFilter
   *
   * @param whereCondition String
   * @param param Map
   * @param columns String
   * @return Object
   */
  public RecordSet applyFilter(String whereCondition, Map param, String columns) {
    throw new SparrowRuntimeException("Unsupported operation");
  }

  /**
   * executeQuery
   *
   * @return int
   */
  public int executeQuery() {
    throw new UnsupportedOperationException("CSVDataProvider doesn't support executeQuery method invocation");
  }

  /**
   * <p>Title: </p>
   * <p>Description: </p>
   * <p>Copyright: Copyright (c) 2004</p>
   * <p>Company: </p>
   * @author Saji Venugopalan
   * @version 1.0
   */
  private interface PostProcessHandler {
    abstract void handlePostProcess(File file);

    abstract boolean isComplete();
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
  private class IgnoreHandler
      implements PostProcessHandler {

    private boolean complete = false;

    public void handlePostProcess(File file) {
      complete = true;
    }

    public boolean isComplete() {
      return complete;
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
  private class DeleteFileHandler
      implements PostProcessHandler {
    private boolean complete = false;

    public void handlePostProcess(File file) {
      fileProcessor.close();
      file.delete();
      complete = true;
    }

    public boolean isComplete() {
      return complete;
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
  private class MoveFileHandler
      implements PostProcessHandler {
    private boolean complete = false;

    public void handlePostProcess(File file) {
      fileProcessor.close(); // to release the file from the current process
      File targetFile = new File(fileInfo.getPostProcessDir() + "/" +
                                 file.getName());
      targetFile.delete();
      file.renameTo(targetFile);
      complete = true;
    }

    public boolean isComplete() {
      return complete;
    }
  }

}
