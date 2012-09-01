package sparrow.etl.core.transaction;

import javax.transaction.xa.XAResource;

public interface XAResourceWrapper
    extends XAResource {

  public void doCommit() throws Exception;

  public void doRollback() throws Exception;

  public void doClose() throws Exception;

  public boolean isCloseFlagged();

  public String getName();

}
