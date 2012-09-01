package sparrow.etl.impl.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;

import sparrow.etl.core.config.SparrowDataProviderConfig;
import sparrow.etl.core.context.SparrowContext;
import sparrow.etl.core.dao.impl.QueryObject;
import sparrow.etl.core.dao.impl.RecordSet;
import sparrow.etl.core.dao.provider.DataProvider;
import sparrow.etl.core.dao.provider.impl.DBDataProvider;
import sparrow.etl.core.dao.util.ConnectionProvider;
import sparrow.etl.core.exception.DataException;
import sparrow.etl.core.exception.InitializationException;
import sparrow.etl.core.exception.ScriptException;
import sparrow.etl.core.exception.SparrowTransactionException;
import sparrow.etl.core.script.ScriptEngine;
import sparrow.etl.core.script.ScriptFactory;
import sparrow.etl.core.util.ConfigKeyConstants;
import sparrow.etl.core.util.Constants;
import sparrow.etl.core.util.SparrowUtil;


/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public class ProceduralDataProvider
    implements DBDataProvider {

  protected final SparrowContext context;
  protected final SparrowDataProviderConfig config;
  protected QueryObject query;
  protected QueryObject copy;
  protected final String name;
  protected Map dpCache;
  protected static final Map STATIC_INSTANCES = new HashMap();
  protected ScriptEngine sce = null;

  private static final StringBuffer DEFAULT_IMPORTS = getImports();
  private ConnectionProvider cp = new TransEnabledConnectionProvider();
  private boolean isInTrans = false;

  /**
   *
   * @param config SparrowDataProviderConfig
   */
  public ProceduralDataProvider(SparrowDataProviderConfig config) {

    String cacheType=config.getInitParameter().getParameterValue(ConfigKeyConstants.
        PARAM_DP_CACHE_TYPE);
    boolean lkpKeys=config.getInitParameter().isParameterExist(ConfigKeyConstants.PARAM_LOOKUP_KEYS);

    if (!(lkpKeys) && cacheType!=null && Constants.CACHE_TYPE_INCREMENTAL.equals(cacheType)) {
      throw new InitializationException(
          "[" + config.getName() +
          "] - Data Provider Type [PROC]: does not support incremental cache");
    }

    this.context = config.getContext();
    this.config = config;
    this.name = config.getName();
    this.query = new QueryObject(name);
    this.copy = query.getCopy();
    this.dpCache = new HashMap();

    sce = (ScriptEngine) STATIC_INSTANCES.get(name);

    if (sce == null) {
      String script = config.getInitParameter().getParameterValue(
          ConfigKeyConstants.PARAM_PROCEDURE);
      script = DEFAULT_IMPORTS.toString() + script;

      sce = ScriptFactory.getScriptEngine();
      sce.setScriptContent(script);
      sce.setArgumentVariableNames(new String[] {ScriptEngine.VAR_CONTEXT,
                                   ScriptEngine.VAR_DATAHANDLER,ScriptEngine.VAR_DATASET});
      sce.setReturnType(RecordSet.class);
      sce.setArgumentClassTypes(new Class[] {
        SparrowContext.class, ProceduralDataProvider.class, Map.class});
      try {
        sce.initialize();
      }
      catch (ScriptException ex) {
        throw new InitializationException(ex);

      }
      STATIC_INSTANCES.put(name, sce);
    }
  }

  /**
   * clone
   *
   * @return Object
   */
  public Object clone() throws CloneNotSupportedException {
    ProceduralDataProvider cln = (ProceduralDataProvider)super.clone();
    cln.setQuery(copy.getCopy());
    cln.dpCache = new HashMap();
    return cln;
  }

  /**
   * destory
   */
  public void destory() {
  }

  /**
   * getData
   *
   * @return RecordSet
   */
  public RecordSet getData() {

    RecordSet rs = null;
    try {
      rs = (RecordSet) evaluate();
    }
    catch (ScriptException ex) {
      ex.printStackTrace();
      rs = RecordSet.EMPTY_RECORDSET;
    }
    return rs;
  }

  /**
   *
   */
  private Object evaluate() throws ScriptException {
    Object o = null;
    try {
      o = sce.evaluate(new Object[] {context, this, query.getQueryParamAsMap()});
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
    finally {
      query.reset(copy);
    }
    return o;
  }

  /**
   *
   */
  public void beginTrans() {
    if(!isInTrans){
      context.getTransactionManager().begin();
      isInTrans = true;
    }
    else{
      throw new SparrowTransactionException("Nested Transaction not supported");
    }
  }

  /**
   *
   */
  public void commitTrans() {
    try {
      if (isInTrans) {
        context.getTransactionManager().commit();
      }
    }
    catch (SystemException ex) {
    }
    catch (HeuristicRollbackException ex) {
    }
    catch (HeuristicMixedException ex) {
    }
    catch (RollbackException ex) {
    }
    finally {
      isInTrans = false;

    }
  }

  /**
   *
   */
  public void rollbackTrans() {
    try {
      if (isInTrans) {
        context.getTransactionManager().rollback();
      }
    }
    catch (SystemException ex) {
      ex.printStackTrace();
    }
    finally {
      isInTrans = false;
    }
  }

  /**
   *
   * @param dpName String
   * @throws DataException
   * @return RecordSet
   */
  public RecordSet getData(String dpName) throws DataException {
    return getData(dpName, null, null);
  }

  /**
   *
   * @param dpName String
   * @throws DataException
   * @return RecordSet
   */
  public RecordSet getData(String dpName, Map mapParam, List listParam) throws
      DataException {

    //System.out.println("getName="+this.getName()+",jsce="+jsce.hashCode()+",this="+this.hashCode()+",dpName="+dpName);

    DataProvider dp = (DataProvider) dpCache.get(dpName);
    DBDataProvider dbdp = null;
    boolean isDBDp = false;

    if (mapParam != null) {
      query.getQueryParamAsMap().putAll(mapParam);
    }
    if (listParam != null) {
      query.getQueryParamAsArray().addAll(listParam);
    }

    if (dp == null) {
      dp = context.getDataProviderElement(dpName).getDataProvider();
      dpCache.put(dpName, dp);
    }
    else {
      dp = (DataProvider) dpCache.get(dpName);
    }

    if (dp instanceof DBDataProvider) {

      dbdp = ( (DBDataProvider) dp);
      isDBDp = true;

      if (isInTrans) {
        dbdp.setConnectionProvider(cp);
      }

      dbdp.getQuery().getQueryParamAsArray().addAll(query.getQueryParamAsArray());
      dbdp.getQuery().getQueryParamAsMap().putAll(query.getQueryParamAsMap());
    }

    RecordSet rs = null;
    try {
      rs = dp.getData();
    }
    finally {
      if (isDBDp && isInTrans) {
        dbdp.setConnectionProvider(null);
      }
    }
    return rs;
  }

  /**
   * getName
   *
   * @return String
   */
  public String getName() {
    return name;
  }

  /**
   * initialize
   */
  public void initialize() {
  }

  /**
   * closeConnection
   * @deprecated
   */
  public void closeConnection() {
  }

  /**
   * executeBatch
   *
   * @param sql String
   * @param param List
   * @return int[]
   * @deprecated
   */
  public int[] executeBatch(String sql, List param) {
    return null;
  }

  /**
   * executeBatch
   *
   * @param param List
   * @return int[]
   * @deprecated
   */
  public int[] executeBatch(List param) {
    return null;
  }

  /**
   * executeQuery
   *
   * @return int
   */
  public int executeQuery() {
    try {
      evaluate();
    }
    catch (ScriptException ex) {
      ex.printStackTrace();
    }
    return 0;
  }

  /**
   *
   * @param dpName String
   * @throws DataException
   * @return int
   */
  public int executeQuery(String dpName) throws DataException {
    return executeQuery(dpName, null, null);
  }

  /**
   *
   * @param dpName String
   * @param mapParam Map
   * @param listParam List
   */
  public int executeQuery(String dpName, Map mapParam, List listParam) throws
      DataException {
    DataProvider dp = (DataProvider) dpCache.get(dpName);

    if (mapParam != null) {
      query.getQueryParamAsMap().putAll(mapParam);
    }
    if (listParam != null) {
      query.getQueryParamAsArray().addAll(listParam);
    }

    if (dp == null) {
      dp = context.getDataProviderElement(dpName).getDataProvider();
      dpCache.put(dpName, dp);
    }
    else {
      dp = (DataProvider) dpCache.get(dpName);
    }

    DBDataProvider dbdp = ( (DBDataProvider) dp);
    dbdp.getQuery().getQueryParamAsArray().addAll(query.getQueryParamAsArray());
    dbdp.getQuery().getQueryParamAsMap().putAll(query.getQueryParamAsMap());

    int count = 0;

    try {
      if (isInTrans) {
        dbdp.setConnectionProvider(cp);
      }
      count = dbdp.executeQuery();
    }
    finally {
      if (isInTrans) {
        dbdp.setConnectionProvider(null);
      }
    }
    return count;

  }

  /**
   * executeSQLBatch
   *
   * @param param List
   * @return int[]
   * @deprecated
   */
  public int[] executeSQLBatch(List param) {
    return null;
  }

  /**
   * getConnection
   *
   * @return Connection
   * @deprecated
   */
  public Connection getConnection() {
    return null;
  }

  /**
   * getQuery
   *
   * @return QueryObject
   */
  public QueryObject getQuery() {
    return query;
  }

  /**
   * setQuery
   *
   * @param query QueryObject
   */
  public void setQuery(QueryObject query) {
    this.query = query;
    this.copy = this.query.getCopy();
  }

  /**
   * setConnectionProvider
   *
   * @param connectionProvider ConnectionProvider
   * @deprecated
   */
  public void setConnectionProvider(ConnectionProvider connectionProvider) {
  }

  /**
   * getImports
   *
   * @return StringBuffer
   */
  private static StringBuffer getImports() {

    Map imports = SparrowUtil.getImplConfig("import");

    StringBuffer sb = new StringBuffer();
    Object importStr = null;
    for(int i=1;(importStr=imports.get(String.valueOf(i)))!=null;i++){
      sb.append("import ").append(importStr.toString()).append(";\n");
    }
    return sb;
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
  private class TransEnabledConnectionProvider
      implements ConnectionProvider {
    /**
     * getConnection
     *
     * @return Connection
     */
    public Connection getConnection(String conName) throws SQLException {
      return context.getTransactionEnabledDBConnection(conName);
    }

  }

}
