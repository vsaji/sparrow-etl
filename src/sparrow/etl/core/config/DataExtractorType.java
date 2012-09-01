package sparrow.etl.core.config;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import sparrow.etl.core.util.SparrowUtil;



/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */
public class DataExtractorType {

  private final String extractorType;
  private final String extractorClass;

  public static final String EXTRACTOR_OTHER = "OTHER";

  public static final HashMap TYPE_IDENTIFIER = new HashMap() {
    {
      Map impls = SparrowUtil.getImplConfig("extractor");
      for (Iterator it = impls.keySet().iterator(); it.hasNext(); ) {
        String key = (String) it.next();
        put(key, new DataExtractorType(key, (String) impls.get(key)));
      }
    }
  };

  public String getExtractorClass() {
    return extractorClass;
  }

  public String getExtractorType() {
    return extractorType;
  }

  public DataExtractorType(String extractorType, String extractorClass) {
    this.extractorType = extractorType;
    this.extractorClass = extractorClass;
  }
}
