package sparrow.elt.core.config;

import java.util.Comparator;

public class UnLoadPriorityComparator
    implements Comparator {

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
    return rc1.getUnLoadPriority() - rc2.getUnLoadPriority();
  }

}
