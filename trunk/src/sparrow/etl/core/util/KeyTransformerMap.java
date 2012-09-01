package sparrow.etl.core.util;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public class KeyTransformerMap
    extends HashMap {

  private final String keySeparator;

  /**
   *
   * @param map Map
   * @param keyPattern String
   */
  public KeyTransformerMap(Map map, String keySeparator) {
    super(map);
    this.keySeparator = keySeparator;
  }

  /**
   *
   * @param keySeparator String
   */
  public KeyTransformerMap(String keySeparator) {
    super();
    this.keySeparator = keySeparator;
  }

  /**
   *
   * @param size int
   * @param keySeparator String
   */
  public KeyTransformerMap(int size,String keySeparator) {
    super(size);
    this.keySeparator = keySeparator;
  }

  /**
   *
   * @param key Object
   * @return Object
   */
  public Object get(Object key) {
    String ky = (String) key;
    return (ky == null || ky.indexOf(keySeparator) < 5 ) ? super.get(ky) :
        super.get(ky.substring(ky.indexOf(keySeparator) + 1));
  }

}
