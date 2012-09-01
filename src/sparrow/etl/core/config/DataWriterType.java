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
 * @author not attributable
 * @version 1.0
 */
public class DataWriterType {

  public static final HashMap TYPE_IDENTIFIER = new HashMap() {
    {
      Map impls = SparrowUtil.getImplConfig("writer");
      for (Iterator it = impls.keySet().iterator(); it.hasNext(); ) {
        String key = (String) it.next();
        put(key, new DataWriterType(key, (String) impls.get(key)));
      }
    }
  };

  private String writerTypeAsString;
  private String writerClass;

  public String getWriterTypeAsString() {
    return writerTypeAsString;
  }

  public String getWriterClass() {
    return writerClass;
  }

  public DataWriterType(String writerTypeAsString, String writerClass) {
    this.writerTypeAsString = writerTypeAsString;
    this.writerClass = writerClass;
  }

  public boolean equals(Object o) {
    DataWriterType r = (DataWriterType) o;
    return this.writerTypeAsString.equals(r.writerTypeAsString);
  }

}
