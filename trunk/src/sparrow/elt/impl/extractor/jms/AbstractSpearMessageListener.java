package sparrow.elt.impl.extractor.jms;

import sparrow.elt.core.config.SparrowDataExtractorConfig;
import sparrow.elt.core.dao.metadata.ColumnAttributes;
import sparrow.elt.core.exception.DataException;
import sparrow.elt.core.exception.ParserException;
import sparrow.elt.core.util.IObjectPoolLifeCycle;

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */
public abstract class AbstractSpearMessageListener
    implements SparrowMessageListener {

  private final SparrowDataExtractorConfig config;
  private IObjectPoolLifeCycle olc;
  private ColumnAttributes[] colAttribs = null;

  /**
   *
   * @param config SparrowDataExtractorConfig
   */
  public AbstractSpearMessageListener(SparrowDataExtractorConfig config) {
    this.config = config;
  }

  /**
   * destory
   */
  public void destory() {
  }

  /**
   * finalizeObject
   */
  public void finalizeObject() {
  }

  /**
   *
   */
  public final void returnObject() {
    if (olc != null) {
      olc.returned(this);
    }
  }

  /**
   * initialize
   */
  public void initialize() {
  }

  /**
   * onMessage
   *
   * @param message SparrowJMSMessage
   * @return String[]
   */
  public abstract String[] onMessage(SparrowJMSMessage message) throws
      DataException,ParserException;

  /**
   * setOLC
   *
   * @param olc IObjectPoolLifeCycle
   */
  public final void setOLC(IObjectPoolLifeCycle olc) {
    this.olc = olc;
  }

  /**
   *
   * @param colAttribs ColumnAttributes
   */
  public final void setColDefAttributes(ColumnAttributes[] colAttribs){
    this.colAttribs = colAttribs;
  }

  /**
   *
   * @return ColumnAttributes
   */
  protected final ColumnAttributes[] getColDefAttributes(){
    return this.colAttribs;
  }

  /**
   *
   * @return SparrowDataExtractorConfig
   */
  public SparrowDataExtractorConfig getConfig() {
    return config;
  }



}
