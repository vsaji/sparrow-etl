package sparrow.elt.core.util;

import java.io.IOException;
import java.util.Properties;

public class SparrowPropertyUtils {

  private static SparrowPropertyUtils instance = null;
  private Properties props = null;

  private SparrowPropertyUtils() {
  }

  /**
   *
   * @return SparrowPropertyUtils
   */
  static SparrowPropertyUtils getInstance() {
    if (instance == null) {
      instance = new SparrowPropertyUtils();
      instance.loadSpearProperties();
    }
    return instance;
  }

  /**
   *
   */
  private void loadSpearProperties() {
    props = new Properties();
    try {
      props.load(SparrowPropertyUtils.class.getClassLoader().getResourceAsStream(
          Constants.SPEAR_PROPERTIES_FILE));
    }
    catch (IOException ex) {
    }
  }

  /**
   *
   * @param key String
   * @return String
   */
  String getProperty(String key) {
    return props.getProperty(key);
  }

}
