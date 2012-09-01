package sparrow.etl.core.transaction;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.transaction.Status;
import javax.transaction.Synchronization;
import javax.transaction.Transaction;
import javax.transaction.xa.XAResource;

import org.apache.log4j.Logger;

public class SparrowTransaction
    implements Transaction {

  private static final Logger log = Logger.getLogger(SparrowTransaction.class.
      getName());

  private Map xaResourceMap = new HashMap(); // the map of trans connections
  private int status; // the status of this transaction

  /**
   * create a new transaction and make it active
   */
  public SparrowTransaction() {
    if (log.isDebugEnabled()) {
      log.debug("starting new transaction");
    }
    status = Status.STATUS_ACTIVE;
  }

  /**
   * commit a transaction, and close any connections that have been flagged for
   * closing
   */
  public void commit() {

    if (status == Status.STATUS_MARKED_ROLLBACK) {
      rollback();
      return;
    }

    status = Status.STATUS_COMMITTING;
    Iterator iter = xaResourceMap.values().iterator();
    while (iter.hasNext()) {
      //     TransConnection conn = (TransConnection) iter.next();
      XAResourceWrapper xarw = (XAResourceWrapper) iter.next();

      try {
        /*    conn.doCommit();
             if (conn.isCloseFlagged()) {
               conn.doClose();
               iter.remove();
             }*/
        if (log.isDebugEnabled()) {
          log.debug("Commiting Transaction [" + xarw+"]");
        }

        xarw.doCommit();
        xarw.doClose();
        iter.remove();
      }
      catch (Exception se) {
        se.printStackTrace();
      }
    }
    if (log.isDebugEnabled()) {
      log.debug("TRANSACTION COMMITED");
    }

    status = Status.STATUS_COMMITTED;
  }

  /**
   *
   * @param xAResource XAResource
   * @param param int
   * @return boolean
   */
  public boolean delistResource(XAResource xAResource, int param) {
    TransConnection conn = (TransConnection) xAResource;
    TransUtils.close(conn);
    xaResourceMap.remove(conn.getName());
    return true;

  }

  public boolean enlistResource(XAResource xAResource) {
    /**  if (! (xAResource instanceof TransConnection)) {
        throw new UnsupportedOperationException();
      } **/
    try {
      XAResourceWrapper xarw = (XAResourceWrapper) xAResource;
//      TransConnection conn = (TransConnection) xAResource;
      xaResourceMap.put(xarw.getName(), xarw);
      return true;
    }
    catch (Exception se) {
      se.printStackTrace();
      return false;
    }
  }

  /**
   * get a resource by name
   */
  public Object getResource(String name) {
    if (xaResourceMap.containsKey(name)) {
      return xaResourceMap.get(name);
    }
    else {
      return null;
    }
  }

  /**
   * get the status of this transaction
   */
  public int getStatus() {
    return status;
  }

  /**
   * not supported
   */
  public void registerSynchronization(Synchronization synchronization) {
    throw new UnsupportedOperationException();
  }

  /**
   * rollback the connections in this transaction
   */
  public void rollback() {
    status = Status.STATUS_ROLLING_BACK;
    Iterator iter = xaResourceMap.values().iterator();
    while (iter.hasNext()) {
      //    TransConnection conn = (TransConnection) iter.next();
      XAResourceWrapper xarw = (XAResourceWrapper) iter.next();
      try {
        if (log.isDebugEnabled()) {
          log.debug("Rolling back Transaction [" + xarw+"]");
        }
        xarw.doRollback();
        /**      conn.doRollback(null);
              if (conn.isCloseFlagged()) {
                if (log.isDebugEnabled()) {
                  log.debug("close flagged, so closing connection");
                }
                conn.doClose();**/
        xarw.doClose();
        iter.remove();
        //      }
      }
      catch (Exception se) {
        se.printStackTrace();
      }
    }
    if (log.isDebugEnabled()) {
      log.debug("TRANSACTION ROLLED BACK");
    }

    status = Status.STATUS_ROLLEDBACK;
  }

  /**
   * flag that a rollback is required
   */
  public void setRollbackOnly() {
    status = Status.STATUS_MARKED_ROLLBACK;
  }
}
