package sparrow.etl.core.util;

import java.util.Map;

import org.apache.commons.collections.map.CaseInsensitiveMap;

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */
public class CaseInSensitiveMap
    extends CaseInsensitiveMap //TreeMap
    implements Map {

  /**
   *
   */
  public CaseInSensitiveMap() {
    super();
  }

  /**
   *
   * @param initialCapacity int
   */
  public CaseInSensitiveMap(int initialCapacity) {
    super(initialCapacity);
  }

  /**
   *
   * @param initialCapacity int
   * @param loadFactor float
   */
  public CaseInSensitiveMap(int initialCapacity, float loadFactor) {
    super(initialCapacity, loadFactor);
  }

  /**
   *
   * @param map Map
   */
  public CaseInSensitiveMap(Map map) {
    super(map);
  }

}
