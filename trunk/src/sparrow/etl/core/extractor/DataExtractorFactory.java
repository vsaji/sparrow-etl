package sparrow.etl.core.extractor;

import sparrow.etl.core.config.ConfigParam;
import sparrow.etl.core.config.DataExtractorConfig;
import sparrow.etl.core.config.SparrowDataExtractorConfig;
import sparrow.etl.core.context.SparrowApplicationContext;
import sparrow.etl.core.context.SparrowContext;
import sparrow.etl.core.util.SparrowUtil;

public abstract class DataExtractorFactory {

  /**
   *
   * @param config SparrowApplicationContext
   * @return DataLoader
   */
  public static DataExtractor getLoader(SparrowApplicationContext context) {
    return getDriverDataLoader(context);
  }

  /**
   *
   * @param config SparrowApplicationContext
   * @return DataLoader
   */
  private static DataExtractor getDriverDataLoader(SparrowApplicationContext
      context) {

    DataExtractorConfig extractorConfig = context.getConfiguration().
        getDataExtractor();
    DataExtractor extractor = null;

    extractor = (DataExtractor) SparrowUtil.createObject(
        extractorConfig.getType().getExtractorClass(),
        new Class[] {SparrowDataExtractorConfig.class}
        , new Object[] {new SpearDataExtractorConfigImpl(context,extractorConfig)});
    return extractor;
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
private static class SpearDataExtractorConfigImpl implements SparrowDataExtractorConfig{

  private final SparrowApplicationContext   context;
  private final DataExtractorConfig   config;


  SpearDataExtractorConfigImpl(SparrowApplicationContext
      context,DataExtractorConfig config){
    this.context=context;
    this.config = config;
  }

    /**
     * getContext
     *
     * @return SparrowContext
     */
    public SparrowContext getContext() {
      return context;
    }

    /**
     * getInitParameter
     *
     * @return ConfigParam
     */
    public ConfigParam getInitParameter() {
      return config;
    }

    /**
     * getName
     *
     * @return String
     */
    public String getName() {
      return config.getName();
    }
  }




}
