package sparrow.elt.core.util.tar;

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public interface
    TarProgressDisplay {
  /**
   * Display a progress message.
   *
   * @param msg The message to display.
   */

  public void
      showTarProgressMessage(String msg);
}
