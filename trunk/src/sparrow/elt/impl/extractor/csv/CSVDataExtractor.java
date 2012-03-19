package sparrow.elt.impl.extractor.csv;

import sparrow.elt.core.config.SparrowDataExtractorConfig;
import sparrow.elt.core.dao.provider.impl.CSVDataProvider;
import sparrow.elt.core.util.ConfigKeyConstants;
import sparrow.elt.impl.extractor.DefaultDataExtractor;

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
