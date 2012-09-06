package sparrow.etl.core.initializer;

import sparrow.etl.core.config.ConfigParam;
import sparrow.etl.core.config.DataTransformerConfig;
import sparrow.etl.core.config.DataTransformerType;
import sparrow.etl.core.config.SparrowDataTransformerConfig;
import sparrow.etl.core.context.SparrowContext;
import sparrow.etl.core.exception.InitializationException;
import sparrow.etl.core.log.SparrowLogger;
import sparrow.etl.core.log.SparrowrLoggerFactory;
import sparrow.etl.core.monitor.AppMonitor;
import sparrow.etl.core.monitor.CycleMonitor;
import sparrow.etl.core.transformer.DataTransformer;
import sparrow.etl.core.transformer.DataTransformerFactory;
import sparrow.etl.core.transformer.DataTransformerPoolManager;

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */
public class DataTransformerPoolInitializer
    implements Initializable {

  /**
   *
   */
  private static final SparrowLogger logger = SparrowrLoggerFactory.
      getCurrentInstance(
      DataTransformerPoolInitializer.class);

  /**
   *
   * @param context SparrowApplicationContextImpl
   */
  DataTransformerPoolInitializer() {
  }

  /**
   *
   * @throws InitializationException
   */
  public void initialize(SparrowContext context, AppMonitor appMon,
                         CycleMonitor cycleMon) {
    try {
      SparrowApplicationContextImpl contxt = (SparrowApplicationContextImpl)
          context;
      DataTransformerFactory manager = new DataTransformerPoolManager(new
          SparrowDataTransformerConfigImpl(contxt.getConfiguration().
                                         getDataTransformer(), context));
      DataTransformer dt = manager.getDataTransformer();
      dt.staticInitialize();
      dt.destroy();

      contxt.setDataTransformerFactory(manager);
    }
    catch (Exception e) {
      throw new InitializationException(e);
    }
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
  class SparrowDataTransformerConfigImpl
      implements SparrowDataTransformerConfig {

    private final DataTransformerConfig item;
    private final SparrowContext context;

    SparrowDataTransformerConfigImpl(DataTransformerConfig item,
                                   SparrowContext context) {
      this.item = item;
      this.context = context;
    }

    /**
     * getInitParameter
     *
     * @return ConfigParam
     */
    public ConfigParam getInitParameter() {
      return item;
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
     * getName
     *
     * @return String
     */
    public String getName() {
      return item.getName();
    }

    /**
     * getPoolSize
     *
     * @return int
     */
    public int getPoolSize() {
      return item.getPoolSize();
    }

    /**
     * getTransformerClass
     *
     * @return String
     */
    public String getTransformerClass() {
      return item.getClassName();
    }

    /**
     * getTransformerType
     *
     * @return DataTransformerType
     */
    public DataTransformerType getTransformerType() {
      return item.getType();
    }

  }

}
