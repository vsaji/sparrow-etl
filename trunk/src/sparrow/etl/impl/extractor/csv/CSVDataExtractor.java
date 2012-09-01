package sparrow.etl.impl.extractor.csv;

import sparrow.etl.core.config.SparrowDataExtractorConfig;
import sparrow.etl.core.dao.provider.impl.CSVDataProvider;
import sparrow.etl.core.util.ConfigKeyConstants;
import sparrow.etl.impl.extractor.DefaultDataExtractor;

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public class CSVDataExtractor
    extends DefaultDataExtractor {

  /**
   *
   */
  public CSVDataExtractor(SparrowDataExtractorConfig config) {
    super(config);
    if (!config.getInitParameter().isParameterExist(ConfigKeyConstants.
        PARAM_DATA_PROVIDER)) {
      dp = new CSVDataProvider(config);
    }
  }

  /**
   *
   */
  public void initialize() {
    if (dp != null) {
      dp.initialize();
    }
    else {
      super.initialize();
    }
  }

}
