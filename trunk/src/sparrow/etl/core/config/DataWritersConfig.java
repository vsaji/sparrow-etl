/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sparrow.etl.core.config;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import sparrow.etl.jaxb.DATAWRITERSType;
import sparrow.etl.jaxb.WRITERType;


/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */
public interface DataWritersConfig {

  abstract String getOnError();

  abstract int getThreadCount();

  abstract List getDataWriters();

  abstract boolean isWritersExist();

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
class DataWritersConfigImpl
    implements DataWritersConfig {

  private List dataWriters = null;
  private final int threadCount;
  private final String onError;

  /**
   *
   * @param datasources DATASOURCESType
   */
  DataWritersConfigImpl(DATAWRITERSType datawriters) {
    dataWriters = new ArrayList();
    threadCount = datawriters.getTHREADCOUNT();
    onError = datawriters.getONERROR();
    this.bind(datawriters);
  }

  /**
   *
   * @param datasource DatasourceConfig
   */
  private void addWriters(WriterConfig writer) {
    dataWriters.add(writer);
  }

  /**
   *
   */
  private void bind(DATAWRITERSType dataWriters) {
    List writers = dataWriters.getWRITER();
    if (!writers.isEmpty()) {
      for (Iterator iter = writers.iterator(); iter.hasNext(); ) {
        WRITERType item = (WRITERType) iter.next();
        addWriters(new WriterConfigImpl(item));
      }
    }
  }

  public String getOnError() {
    return onError;
  }

  public int getThreadCount() {
    return threadCount;
  }

  public List getDataWriters() {
    return dataWriters;
  }

  /**
   * isWritersExist
   *
   * @return boolean
   */
  public boolean isWritersExist() {
    return!dataWriters.isEmpty();
  }

}
