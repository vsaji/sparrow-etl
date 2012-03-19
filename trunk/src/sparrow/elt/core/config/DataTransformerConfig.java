/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sparrow.elt.core.config;

import sparrow.elt.core.exception.SparrowRuntimeException;
import sparrow.elt.jaxb.DATATRANSFORMERType;
import sparrow.elt.jaxb.impl.DATATRANSFORMERTypeImpl;

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */
public interface DataTransformerConfig
    extends ConfigParam {

  abstract String getName();

  abstract String getClassName();

  abstract int getPoolSize();

  abstract int getThreadCount();

  abstract DataTransformerType getType();

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
class DataTransformerConfigImpl
    extends ConfigParamImpl
    implements DataTransformerConfig {

  private final String name;
  private final String className;
  private final int poolSize;
  private final int threadCount;
  private final DataTransformerType type;

  /**
   *
   * @param dataExtractor DATAEXTRACTORType
   */
  DataTransformerConfigImpl(DATATRANSFORMERType dataTransformer) {
    super((dataTransformer=preCheck(dataTransformer)).getPARAM());
    this.name = dataTransformer.getNAME();
    this.className = dataTransformer.getCLASS();
    this.poolSize = dataTransformer.getPOOLSIZE();
    this.threadCount = dataTransformer.getTHREADCOUNT();

    if (this.className == null || this.className.trim().equals("")) {

      String identifierKey = dataTransformer.getTYPE().trim().toUpperCase();

      this.type = (DataTransformerType) DataTransformerType.TYPE_IDENTIFIER.get(
          identifierKey);

      if (type == null) {
        throw new SparrowRuntimeException("Unrecognized TYPE [" +
                                        dataTransformer.getTYPE() + "] : Key [" +
                                        identifierKey + "]");
      }
    }
    else {
      this.type = new DataTransformerType(DataTransformerType.OTHER,
                                          this.className);
    }

  }


  /**
   *
   * @param dataTransformer DATATRANSFORMERType
   * @return DATATRANSFORMERType
   */
  private static DATATRANSFORMERType preCheck(DATATRANSFORMERType dataTransformer){
    if(dataTransformer==null){
      dataTransformer = new DATATRANSFORMERTypeImpl();
      dataTransformer.setNAME(DataTransformerType.DEFAULT_NAME);
      dataTransformer.setTYPE(DataTransformerType.DEFAULT_TYPE);
      dataTransformer.setPOOLSIZE(DataTransformerType.DEFAULT_POOL_SIZE);
      dataTransformer.setTHREADCOUNT(DataTransformerType.DEFAULT_THREAD_COUNT);
    }
    return dataTransformer;
  }

  /**
   *
   * @return String
   */
  public String getName() {
    return this.name;
  }

  public String getClassName() {
    return this.className;
  }

  public int getPoolSize() {
    return this.poolSize;
  }

  public int getThreadCount() {
    return this.threadCount;
  }

  /**
   * getType
   *
   * @return DataTransformerType
   */
  public DataTransformerType getType() {
    return type;
  }

}
