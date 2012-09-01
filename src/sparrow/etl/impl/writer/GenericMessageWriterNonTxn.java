package sparrow.etl.impl.writer;

import javax.jms.JMSException;
import javax.jms.Session;

import sparrow.etl.core.config.SparrowDataWriterConfig;
import sparrow.etl.core.exception.EventNotifierException;


/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */
public class GenericMessageWriterNonTxn
    extends GenericMessageWriter {

  private Session session = null;

  public GenericMessageWriterNonTxn(SparrowDataWriterConfig config, int transaction) {
    super(config, transaction);
  }

  /**
   *
   * @throws EventNotifierException
   */
  public void beginCycle() throws EventNotifierException {
    try {
      session = super.getSession();
    }
    catch (Exception ex) {
      throw new EventNotifierException("Could not obtain resource [" +
                                       RESOURCE_NAME + "]", ex);
    }
  }

  /**
   *
   * @throws EventNotifierException
   */
  public void endCycle() throws EventNotifierException {

    try {
      session.close();
      session = null;
    }
    catch (JMSException ex1) {
      ex1.printStackTrace();
    }
  }

  /**
   *
   * @return Session
   */
  protected Session getSession() {
    return session;
  }

  /**
   *
   * @param session Session
   * @param exception boolean
   */
  protected void closeSession(Session session, boolean exception) {
    try {
      if(session.getTransacted()){
        if (exception) {
          session.rollback();
        }
        else {
          session.commit();
        }
      }
    }
    catch (JMSException ex) {
      ex.printStackTrace();
    }
  }

}
