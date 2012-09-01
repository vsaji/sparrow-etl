package sparrow.etl.impl.extractor.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;

import sparrow.etl.core.config.SparrowDataExtractorConfig;
import sparrow.etl.core.dao.impl.QueryObject;
import sparrow.etl.core.dao.impl.ResultRow;
import sparrow.etl.core.dao.provider.impl.DBDataProviderImpl;
import sparrow.etl.core.dao.util.DBUtil;
import sparrow.etl.core.exception.DataException;
import sparrow.etl.core.util.ConfigKeyConstants;
import sparrow.etl.core.util.Constants;
import sparrow.etl.core.util.SparrowUtil;
import sparrow.etl.core.vo.DataHolder;
import sparrow.etl.impl.extractor.DefaultDataExtractor;


/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */
public final class DBDataExtractor
    extends DefaultDataExtractor {

  private boolean tokenExist;
  private String[] dpList = null;

  /**
   *
   */
  public DBDataExtractor(SparrowDataExtractorConfig config) {
    super(config);
    if (!config.getInitParameter().isParameterExist(ConfigKeyConstants.
        PARAM_DATA_PROVIDER)) {
      dp = new DBDataProviderImpl(config);
    }
  }




  public DBDataExtractor(SparrowDataExtractorConfig config,String dpName) {
    super(config,dpName);
  }

  /**
   *
   * @throws DataException
   * @return DataHolder
   */
  public DataHolder loadData() throws DataException {

    if (tokenExist) {
      HashMap hm = new HashMap();
      for (int i = 0; i < dpList.length; i++) {
        ResultRow rr = context.getDataProviderElement(dpList[i]).getData().
            getFirstRow();
        SparrowUtil.addResultAsKeyValue(rr, dpList[i], hm);
      }
      dp.getQuery().getQueryParamAsMap().putAll(hm);
    }
    return super.loadData();
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
    this.resolveDependencies();
  }

  /**
   *
   */
  private void resolveDependencies() {
    String sql = dp.getQuery().getSQL();
    this.tokenExist = (sql.indexOf(Constants.VARIABLE_IDENTIFIER) != -1 || sql.indexOf(Constants.REPLACE_TOKEN_START) != -1);

    if (tokenExist) {
      QueryObject qo = new QueryObject();
      qo.setSQL(sql, false);
      
      ArrayList tokens = DBUtil.getTokenParamAsList(qo);
      tokens.addAll(DBUtil.getReplaceTokenParamAsList(qo));
      
      TreeSet dpset = new TreeSet();

      for (Iterator it = tokens.iterator(); it.hasNext(); ) {
        String key = it.next().toString();
        String dpCol[] = key.split("[$]");
        dpset.add(dpCol[0]);
      }

      dpList = (String[]) dpset.toArray(new String[dpset.size()]);

      dpset.clear();
      tokens.clear();
      dpset = null;
      tokens = null;
    }

  }

}
