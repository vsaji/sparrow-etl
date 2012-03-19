package sparrow.elt.core.monitor;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */

public interface Reporter {

  public abstract void report() throws Exception;
  public abstract void destory();
}
