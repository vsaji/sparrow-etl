package sparrow.etl.impl.extractor;

import sparrow.etl.core.config.SparrowDataExtractorConfig;
import sparrow.etl.core.context.SparrowContext;
import sparrow.etl.core.dao.impl.RecordSet;
import sparrow.etl.core.dao.provider.DataProvider;
import sparrow.etl.core.dao.provider.DataProviderElementExtn;
import sparrow.etl.core.exception.DataException;
import sparrow.etl.core.extractor.DataExtractor;
import sparrow.etl.core.util.ConfigKeyConstants;
import sparrow.etl.core.vo.DataHolder;

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */
public class DefaultDataExtractor
    implements DataExtractor {

  protected final SparrowContext context;
  protected final SparrowDataExtractorConfig config;
  protected final String dataProvider;
  protected final DataHolder holder;

  protected DataProvider dp = null;

  /**
   *
   */
  public DefaultDataExtractor(SparrowDataExtractorConfig config) {
    this(config,config.getInitParameter().getParameterValue(
        ConfigKeyConstants.PARAM_DATA_PROVIDER));
  }


  /**
    *
    */
   public DefaultDataExtractor(SparrowDataExtractorConfig config,String dpName) {
     this.context = config.getContext();
     this.config = config;
     this.dataProvider = dpName;
     this.holder = new DataHolder();
   }

  /**
   *
   * @throws DataException
   * @return CachedRowSet
   */
  public DataHolder loadData() throws DataException {
    RecordSet result = dp.getData();
    holder.setData(result);
    return holder;
  }

  /**
   * destroy
   */
  public void destroy() throws DataException{
    dp.destory();
  }

  /**
   * initialize
   */
  public void initialize() {
    DataProviderElementExtn dpe = (DataProviderElementExtn) context.
        getDataProviderElement(this.dataProvider);
    dp = dpe.getDataProvider();
  }

}
