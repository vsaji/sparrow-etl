package sparrow.elt.impl.script;

import sparrow.elt.core.DataSet;
import sparrow.elt.core.config.SparrowDataTransformerConfig;
import sparrow.elt.core.context.SparrowContext;
import sparrow.elt.core.log.SparrowLogger;
import sparrow.elt.core.transformer.DataTransformer;
import sparrow.elt.core.vo.DataOutputHolder;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class ScriptVariables {

  private DataSet dataSet;
  private DataTransformer dataTransformer;
  private SparrowContext context;
  private SparrowDataTransformerConfig config;
  private SparrowLogger logger;
  private DataOutputHolder dataOut;

  /**
   *
   * @param dataSet DataSet
   * @param dataTransformer DataTransformer
   * @param context SparrowContext
   * @param config SparrowDataTransformerConfig
   * @param logger SparrowLogger
   * @param dataOut DataOutputHolder
   */
  public ScriptVariables(DataSet dataSet, DataTransformer dataTransformer,
                         SparrowContext context,
                         SparrowDataTransformerConfig config, SparrowLogger logger,
                         DataOutputHolder dataOut) {
    this.dataOut = dataOut;
    this.dataSet = dataSet;
    this.config = config;
    this.context = context;
    this.dataTransformer = dataTransformer;
    this.logger = logger;
  }

  public DataOutputHolder getDataOut() {
    return dataOut;
  }

  public SparrowLogger getLogger() {
    return logger;
  }

  public DataTransformer getDataTransformer() {
    return dataTransformer;
  }

  public DataSet getDataSet() {
    return dataSet;
  }

  public SparrowContext getContext() {
    return context;
  }

  public void setConfig(SparrowDataTransformerConfig config) {
    this.config = config;
  }

  public void setDataOut(DataOutputHolder dataOut) {
    this.dataOut = dataOut;
  }

  public void setLogger(SparrowLogger logger) {
    this.logger = logger;
  }

  public void setDataTransformer(DataTransformer dataTransformer) {
    this.dataTransformer = dataTransformer;
  }

  public void setDataSet(DataSet dataSet) {
    this.dataSet = dataSet;
  }

  public void setContext(SparrowContext context) {
    this.context = context;
  }

  public SparrowDataTransformerConfig getConfig() {
    return config;
  }

}
