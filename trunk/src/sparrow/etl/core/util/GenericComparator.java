package sparrow.etl.core.util;

import java.util.Comparator;

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */
public class GenericComparator
    implements Comparator {

  private static final Comparator instance = new GenericComparator();

  /**
   *
   * @return Comparator
   */
  public static final Comparator getComparator(){
    return instance;
  }

  /**
   * equals
   *
   * @param obj Object
   * @return boolean
   */
  public boolean equals(Object obj) {
    return false;
  }

  /**
   * compare
   *
   * @param o1 Object
   * @param o2 Object
   * @return int
   */
  public int compare(Object o1, Object o2) {
    Sortable rc1 = (Sortable) o1;
    Sortable rc2 = (Sortable) o2;
    return rc2.getPriority()- rc1.getPriority();
  }

}
