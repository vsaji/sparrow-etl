package sparrow.elt.core.config;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import sparrow.elt.core.util.SparrowUtil;


/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public class DataTransformerType {

  public static final String OTHER = "OTHER";
  public static final String DEFAULT_TYPE = "DEFAULT";
  public static final String DEFAULT_NAME = "SPARROWTRANS";
  public static final int DEFAULT_POOL_SIZE = 25;
  public static final int DEFAULT_THREAD_COUNT = 5;

  /**
   *
   */
  public static final HashMap TYPE_IDENTIFIER = new HashMap() {
    {
      Map impls = SparrowUtil.getImplConfig("transformer");
      for (Iterator it = impls.keySet().iterator(); it.hasNext(); ) {
        String key = (String) it.next();
        String value = impls.get(key).toString();
        put(key, new DataTransformerType(key, value));
      }
    }
  };

  private final String transformerType;
  private final String transformerClass;

  public String getTransformerClass() {
    return transformerClass;
  }

  public String getTransformerType() {
    return transformerType;
  }

  public DataTransformerType(String transformerType, String transformerClass) {
    this.transformerType = transformerType;
    this.transformerClass = transformerClass;
  }

}
