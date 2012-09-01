package sparrow.etl.impl.extractor.jms;

import sparrow.etl.core.dao.metadata.ColumnAttributes;
import sparrow.etl.core.exception.DataException;
import sparrow.etl.core.exception.ParserException;
import sparrow.etl.core.util.IObjectPoolLifeCycle;

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */
public abstract interface SparrowMessageListener {

  public abstract String[] onMessage(SparrowJMSMessage message) throws
      DataException,ParserException;

  public abstract void initialize();

  public abstract void setOLC(IObjectPoolLifeCycle olc);

  public abstract void setColDefAttributes(ColumnAttributes[] colAttribs);

  public abstract void finalizeObject();

  public abstract void destory();

  public abstract void returnObject();
}
