package sparrow.elt.core.initializer;

import sparrow.elt.core.config.ConfigParam;
import sparrow.elt.core.config.DataTransformerConfig;
import sparrow.elt.core.config.DataTransformerType;
import sparrow.elt.core.config.SparrowDataTransformerConfig;
import sparrow.elt.core.context.SparrowContext;
import sparrow.elt.core.exception.InitializationException;
import sparrow.elt.core.log.SparrowLogger;
import sparrow.elt.core.log.SparrowrLoggerFactory;
import sparrow.elt.core.monitor.AppMonitor;
import sparrow.elt.core.monitor.CycleMonitor;
import sparrow.elt.core.transformer.DataTransformer;
import sparrow.elt.core.transformer.DataTransformerFactory;
import sparrow.elt.core.transformer.DataTransformerPoolManager;

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
          SpearDataTransformerConfigImpl(contxt.getConfiguration().
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
  class SpearDataTransformerConfigImpl
      implements SparrowDataTransformerConfig {

    private final DataTransformerConfig item;
    private final SparrowContext context;

    SpearDataTransformerConfigImpl(DataTransformerConfig item,
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
