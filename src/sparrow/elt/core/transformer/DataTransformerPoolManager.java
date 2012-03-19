package sparrow.elt.core.transformer;

import org.apache.commons.pool.BasePoolableObjectFactory;
import org.apache.commons.pool.impl.StackObjectPool;

import sparrow.elt.core.config.SparrowDataTransformerConfig;
import sparrow.elt.core.exception.SparrowRuntimeException;
import sparrow.elt.core.log.SparrowLogger;
import sparrow.elt.core.log.SparrowrLoggerFactory;
import sparrow.elt.core.util.Constants;
import sparrow.elt.core.util.IObjectPoolLifeCycle;
import sparrow.elt.core.util.SparrowUtil;

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */
public class DataTransformerPoolManager
    implements DataTransformerFactory {

  private StackObjectPool pool;
  private SparrowDataTransformerConfig config;

  /**
   *
   */
  private static final SparrowLogger logger = SparrowrLoggerFactory.
      getCurrentInstance(
      DataTransformerPoolManager.class);

  public DataTransformerPoolManager(SparrowDataTransformerConfig config) {
    this.config = config;

    int poolSize = (config.getInitParameter().isParameterExist(Constants.
        USER_OBJ_POOL_SIZE)) ?
        Integer.parseInt(config.getInitParameter().getParameterValue(Constants.
        USER_OBJ_POOL_SIZE)) : Constants.DEFAULT_USER_OBJ_POOL_SIZE;

    this.pool = new StackObjectPool(new DataTransformerPoolFactory(this),
                                    poolSize,
                                    Constants.DEFAULT_USER_OBJ_POOL_SIZE);
  }

  /**
   * returned
   *
   * @param o Object
   */
  public void returned(Object o) {
    try {
      pool.returnObject(o);
    }
    catch (Exception ex) {
      throw new SparrowRuntimeException(
          "Exception occured while retruning object to pool [" +
          ex.getMessage() + "]");

    }

  }

  /**
   * getUserObject
   *
   * @param context SparrowContext
   * @return UserObject
   */
  public DataTransformer getDataTransformer() {
    DataTransformer dt = (DataTransformer) offered();
    dt.initialize();
    return dt;
  }

  /**
   * offered
   *
   * @return Object
   */
  public Object offered() {
    Object usrObj = null;
    try {
      usrObj = pool.borrowObject();
    }
    catch (Exception ex) {
      throw new SparrowRuntimeException(
          "Exception occured while borrowing object from pool [" +
          ex.getMessage() + "]");
    }
    return usrObj;
  }

  /**
   * destroy
   */
  public void destroy() {
    pool.clear();
  }

  /**
   * getSpearDataTransformerConfig
   *
   * @return SparrowDataTransformerConfig
   */
  public SparrowDataTransformerConfig getSpearDataTransformerConfig() {
    return config;
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
  private class DataTransformerPoolFactory
      extends BasePoolableObjectFactory {

    private IObjectPoolLifeCycle lc = null;

    /**
     * makeObject
     *
     * @return Object
     */
    public DataTransformerPoolFactory(IObjectPoolLifeCycle lc) {
      this.lc = lc;
    }

    /**
     *
     * @return Object
     */
    public Object makeObject() {

      DataTransformer dt = (DataTransformer) SparrowUtil.createObject(config.
          getTransformerType().getTransformerClass(),
          SparrowDataTransformerConfig.class, config);
      dt.setOLC(lc);
      logger.debug("New DataTransformer Object Created");
      return dt;
    }

    /**
     *
     * @param obj Object
     */
    public void destroyObject(Object obj) {
      DataTransformer dt = (DataTransformer) obj;
      dt.destroy();
      dt.finalizeObject();
      dt = null;
    }

    /**
     *
     * @param obj Object
     */
    public void passivateObject(Object obj) {
      DataTransformer dt = (DataTransformer) obj;
      dt.destroy();
    }

  }

}
