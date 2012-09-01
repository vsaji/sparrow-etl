package sparrow.etl.core.transaction;

import java.io.Serializable;
import javax.naming.Reference;
import javax.naming.Referenceable;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.RollbackException;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

import org.apache.log4j.Logger;

/**
 * a singleton implementation of a transaction manager, using thread-local variables to store
 * the current transaction.
 *
 */
public class SparrowTransactionManager
    implements TransactionManager, UserTransaction, Serializable, Referenceable {
  private static final Logger log = Logger.getLogger(SparrowTransactionManager.class.
      getName());
  private static Reference REF = new Reference(SparrowTransactionManager.class.
                                               getName());
  private static final SparrowTransactionManager stm = new
      SparrowTransactionManager();

  private static final Boolean FALSE = new Boolean(false);
  private static final Boolean TRUE = new Boolean(true);

  private static ThreadLocal IN_TRANS = new ThreadLocal() {
    protected Object initialValue() {
      return FALSE;
    }
  };

  private static ThreadLocal TRANS = new ThreadLocal() {
    protected Object initialValue() {
      return new SparrowTransaction();
    }
  };

  public static final SparrowTransactionManager getTransactionManager() {
    return stm;
  }

  public Reference getReference() {
    return REF;
  }

  /**
   * begin a transaction. This method differs from that specified by the TransactionManager
   * interface, in that it doesn't throw an exception if the current thread is already in
   * a transaction -- and it doesn't create a nested transaction.  This basically
   * assumes that everything will run in the current transaction.
   */
  public void begin() {
    if (!isInTransaction()) {
      IN_TRANS.set(TRUE);
      Transaction t = new SparrowTransaction();
      TRANS.set(t);

      if (log.isDebugEnabled()) {
        log.debug("started new transaction " + t);
      }
    }
    else if (log.isDebugEnabled()) {
      log.debug("new transaction not started, already in trans");
    }
  }

  /**
   * commit the current transaction (note: this doesn't throw an error if the
   * current thread is not in a transaction)
   */
  public void commit() throws RollbackException, HeuristicMixedException,
      HeuristicRollbackException, SystemException {
    if (isInTransaction()) {
      SparrowTransaction trans = (SparrowTransaction) TRANS.get();
      trans.commit();

      TRANS.set(null);
      IN_TRANS.set(FALSE);
    }
    else if (log.isDebugEnabled()) {
      log.debug("not in transaction, no commit");
    }
  }

  /**
   * get the status of the current transaction
   */
  public int getStatus() throws SystemException {
    if (isInTransaction()) {
      Transaction trans = (Transaction) TRANS.get();
      return trans.getStatus();
    }
    else {
      return Status.STATUS_NO_TRANSACTION;
    }
  }

  /**
   * get the transaction for the current (calling) thread
   */
  public Transaction getTransaction() {
    if (isInTransaction()) {
      return (Transaction) TRANS.get();
    }
    else {
      return null;
    }
  }

  /**
   * not supported
   */
  public void resume(Transaction transaction) {
    throw new UnsupportedOperationException();
  }

  /**
   * rollback the current transaction (note: this doesn't throw an error if the
   * current thread is not in a transaction)
   */
  public void rollback() throws SystemException {
    if (isInTransaction()) {
      Transaction trans = (Transaction) TRANS.get();
      trans.rollback();
      TRANS.set(null);
      IN_TRANS.set(FALSE);
      if (log.isDebugEnabled()) {
        log.debug("rolled back transaction");
      }
    }
    else if (log.isDebugEnabled()) {
      log.debug("not in transaction, no commit");
    }
  }

  /**
   * flag the current transaction  for rollback (note: this doesn't throw an error
   * if the current thread is not in a transaction)
   */
  public void setRollbackOnly() throws SystemException {
    if (isInTransaction()) {
      Transaction trans = (Transaction) TRANS.get();
      trans.setRollbackOnly();
    }
  }

  /**
   * not supported
   */
  public void setTransactionTimeout(int param) {
    throw new UnsupportedOperationException();
  }

  /**
   * not supported
   */
  public Transaction suspend() {
    throw new UnsupportedOperationException();
  }

  /**
   * a helper function to indicate if the calling thread is in a transaction
   */
  public boolean isInTransaction() {
    Boolean b = (Boolean) IN_TRANS.get();
    if (b.booleanValue()) {
      Transaction trans = (Transaction) TRANS.get();
      try {
        if (trans != null && trans.getStatus() != Status.STATUS_NO_TRANSACTION
            && trans.getStatus() != Status.STATUS_UNKNOWN) {
          return true;
        }
      }
      catch (Exception e) {
        e.printStackTrace();
      }
    }
    return false;
  }

}
