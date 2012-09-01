package sparrow.etl.impl.extractor;

import java.util.ArrayList;

import sparrow.etl.core.config.SparrowDataExtractorConfig;
import sparrow.etl.core.dao.impl.ColumnHeader;
import sparrow.etl.core.dao.impl.RecordSet;
import sparrow.etl.core.dao.impl.RecordSetImpl_Disconnected;
import sparrow.etl.core.dao.provider.DataProvider;
import sparrow.etl.core.dao.provider.impl.DBDataProvider;
import sparrow.etl.core.exception.DataException;
import sparrow.etl.core.extractor.DataExtractor;
import sparrow.etl.core.log.SparrowLogger;
import sparrow.etl.core.log.SparrowrLoggerFactory;
import sparrow.etl.core.vo.DataHolder;
import sparrow.etl.impl.extractor.db.DBDataExtractor;


/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public class GenericDataExtractor
    extends DefaultDataExtractor {

  private static final SparrowLogger logger = SparrowrLoggerFactory.
      getCurrentInstance(
      GenericDataExtractor.class);

  private final boolean multipleDPs;
  private DataExtractor[] extractors;
  private String[] dpNames;

  /**
   *
   * @param config SparrowDataExtractorConfig
   */
  public GenericDataExtractor(SparrowDataExtractorConfig config) {
    super(config);
    dpNames = dataProvider.split("[,]");
    multipleDPs = (dpNames.length > 1);
    if (multipleDPs) {
      dpNames = checkDuplication(dpNames);
    }
  }

  /**
   *
   * @param dps String[]
   */
  private String[] checkDuplication(String[] dps) {
    ArrayList al = new ArrayList();
    for (int i = 0; i < dps.length; i++) {
      if (al.contains(dps[i])) {
        logger.warn("Repeatative Data provider [" + dps[i] +
            "] has been found in the driver extractor. One of them will be ignored.");
      }
      else {
        al.add(dps[i]);
      }
    }

    return (String[]) al.toArray(new String[al.size()]);

  }

  /**
   * destroy
   */
  public void destroy() throws DataException {
    if (multipleDPs) {
      for (int i = 0; i < extractors.length; i++) {
        extractors[i].destroy();
      }
    }
    else {
      super.destroy();
    }
  }

  /**
   * initialize
   */
  public void initialize() {
    if (multipleDPs) {
      extractors = new DataExtractor[dpNames.length];
      String dpName = null;
      for (int i = 0; i < dpNames.length; i++) {
        dpName = dpNames[i];
        DataProvider dp = context.getDataProviderElement(dpName).
            getDataProvider();
        extractors[i] = (dp instanceof DBDataProvider) ?
            new DBDataExtractor(config, dpName) :
            new DefaultDataExtractor(config, dpName);
        extractors[i].initialize();
      }
    }
    else {
      super.initialize();
    }
  }

  /**
   * loadData
   *
   * @return DataHolder
   */
  public DataHolder loadData() throws DataException {
    RecordSet recSet = null;

    if (multipleDPs) {
      ColumnHeader pvs = null;
      RecordSetImpl_Disconnected rs = new RecordSetImpl_Disconnected();
      for (int i = 0; i < extractors.length; i++) {

        RecordSetImpl_Disconnected rs1 = (RecordSetImpl_Disconnected)
            extractors[i].loadData().getData();

        /***************************************************/
        if (pvs == null) {
          pvs = rs1.getColumnHeaders();
        }
        else {
          if (pvs.getColumnCount() != rs1.getColumnHeaders().getColumnCount()) {
            logger.warn("Column mismatch found between [" + dpNames[i - 1] +
                        "] and [" + dpNames[i] + "]");
          }
          pvs = rs1.getColumnHeaders();
        }
        /***************************************************/

        logger.info("Loaded [" + rs1.getRowCount() + "] row(s) from [" +
                    dpNames[i] + "]");

        rs.addResult(rs1.getResult());
      }
      recSet = rs;
      holder.setData(recSet);
      return holder;
    }
    else {
      return super.loadData();
    }
  }
}
