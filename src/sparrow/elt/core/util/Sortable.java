package sparrow.elt.core.util;

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public interface Sortable {

  public static final int PRIORITY_HIGH = 100;
  public static final int PRIORITY_ABOVE_MEDIUM = 75;
  public static final int PRIORITY_MEDIUM = 50;
  public static final int PRIORITY_ABOVE_LOW = 25;
  public static final int PRIORITY_LOW = 1;

  public abstract int getPriority();

}
