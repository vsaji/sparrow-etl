package sparrow.elt.core.config;

import java.util.Comparator;

/**
 * compareTo
 *
 * @param o Object
 * @return int
 */
public class LoadPriorityComparator
    implements Comparator {
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
    ResourceConfig rc1 = (ResourceConfig) o1;
    ResourceConfig rc2 = (ResourceConfig) o2;
    return rc1.getLoadPriority() - rc2.getLoadPriority();
  }

}
