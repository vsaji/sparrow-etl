package sparrow.etl.core.config;

import sparrow.etl.jaxb.DATAEXTRACTORType;

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */
public interface DataExtractorConfig
    extends ConfigParam {

  abstract String getName();

  abstract String getClassName();

  abstract DataExtractorType getType();

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
class DataExtractorConfigImpl
    extends ConfigParamImpl
    implements DataExtractorConfig {

  private final String name;
  private final String className;
  private final DataExtractorType type;

  /**
   *
   * @param dataExtractor DATAEXTRACTORType
   */
  DataExtractorConfigImpl(DATAEXTRACTORType dataExtractor) {
    super(dataExtractor.getPARAM());
    this.name = dataExtractor.getNAME();
    this.className = dataExtractor.getCLASS();

    if (this.className == null || this.className.trim().equals("")) {
      String type = dataExtractor.getTYPE().toUpperCase();
      this.type = (DataExtractorType.TYPE_IDENTIFIER.containsKey(type)) ?
          (DataExtractorType) DataExtractorType.TYPE_IDENTIFIER.get(type) :
          new DataExtractorType(DataExtractorType.EXTRACTOR_OTHER,
                                this.className);
    }
    else {
      this.type = new DataExtractorType(dataExtractor.getTYPE(),
                                        this.className);
    }

  }

  /**
   *
   * @return String
   */
  public String getName() {
    return this.name;
  }

  /**
   *
   * @return String
   */
  public String getClassName() {
    return this.className;
  }

  /**
   * getType
   *
   * @return String
   */
  public DataExtractorType getType() {
    return this.type;
  }

}
