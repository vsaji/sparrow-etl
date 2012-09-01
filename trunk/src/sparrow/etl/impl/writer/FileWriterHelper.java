package sparrow.etl.impl.writer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import sparrow.etl.core.util.CounterObject;


/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */
public class FileWriterHelper {

  private static final HashMap INSTANCE_MAP = new HashMap();

  private final String name;
//  private final List requestList = Collections.synchronizedList(new ArrayList());
  private final List requestList = new ArrayList();
  private final CounterObject co = new CounterObject(0);

  private ArrayList headers = null;
  private ArrayList footers = null;  
  private boolean isHeaderFooterResolved = false;
  private boolean footerExist = false;
  private boolean headerExist = false;
  private boolean contentExist = false;
  private boolean headerWritten = false;
  private boolean fileMoved, fileDeleted = false;
  private String headerAsString = "";
  private PrintWriter tempFileWriter = null;

  /**
   *
   * @param name String
   */
  private FileWriterHelper(String name) {
    this.name = name;
  }

  public synchronized void setHeaderExist(boolean headerExist) {
    this.headerExist = headerExist;
  }

  public synchronized void setFooterExist(boolean footerExist) {
    this.footerExist = footerExist;
  }

  public void setHeaders(ArrayList headers) {
    this.headers = headers;
  }

  public synchronized void setHeaderWritten(boolean headerWritten) {
    this.headerWritten = headerWritten;
  }

  public synchronized void setHeaderAsString(String headerAsString) {
    this.headerAsString = headerAsString;
  }

  public void setFooter(ArrayList footers) {
    this.footers = footers;
  }

  public synchronized void setFileMoved(boolean fileMoved) {
    this.fileMoved = fileMoved;
  }

  public synchronized void setFileDeleted(boolean fileDeleted) {
    this.fileDeleted = fileDeleted;
  }

  public void setContentExist(boolean contentExist) {
    if(!this.contentExist){
      this.contentExist = contentExist;
    }
  }

  /**
   *
   * @param name String
   * @return FileWriterHelper
   */
  public static final FileWriterHelper getInstance(String name) {

    if (INSTANCE_MAP.containsKey(name)) {
      return (FileWriterHelper) INSTANCE_MAP.get(name);
    }
    else {
      FileWriterHelper instance = new FileWriterHelper(name);
      INSTANCE_MAP.put(name, instance);
      return instance;
    }
  }

  /**
   *
   * @return ArrayList
   */
  public List getRequestList() {
    return this.requestList;
  }

  public synchronized boolean isFooterExist() {
    return footerExist;
  }

  public synchronized boolean isHeaderExist() {
    return headerExist;
  }

  public ArrayList getHeaders() {
    return headers;
  }

  public synchronized boolean isHeaderWritten() {
    return headerWritten;
  }

  public String getHeaderAsString() {
    return headerAsString;
  }

  public ArrayList getFooter() {
    return footers;
  }

  public String getName() {
    return name;
  }

  public PrintWriter getTempFileWriter(String file) {
    try {
      return (tempFileWriter != null) ? tempFileWriter :
          (tempFileWriter = new
           PrintWriter(new FileOutputStream(new File(file))));
    }
    catch (FileNotFoundException ex) {
      return null;
    }
  }

  public boolean isFileMoved() {
    return fileMoved;
  }

  public boolean isFileDeleted() {
    return fileDeleted;
  }

  public boolean isContentExist() {
    return contentExist;
  }

  public synchronized boolean isHeaderFooterResolved() {
    return isHeaderFooterResolved;
  }

  /**
   *
   * @return CounterObject
   */
  public CounterObject getCounterObject() {
    return co;
  }

  public synchronized void setHeaderFooterResolved(boolean
      isHeaderFooterResolved) {
    this.isHeaderFooterResolved = isHeaderFooterResolved;
  }

}
