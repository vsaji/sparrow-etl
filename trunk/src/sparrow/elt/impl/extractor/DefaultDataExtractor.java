package sparrow.elt.impl.extractor;

import sparrow.elt.core.config.SparrowDataExtractorConfig;
import sparrow.elt.core.context.SparrowContext;
import sparrow.elt.core.dao.impl.RecordSet;
import sparrow.elt.core.dao.provider.DataProvider;
import sparrow.elt.core.dao.provider.DataProviderElementExtn;
import sparrow.elt.core.exception.DataException;
import sparrow.elt.core.extractor.DataExtractor;
import sparrow.elt.core.util.ConfigKeyConstants;
import sparrow.elt.core.vo.DataHolder;

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
