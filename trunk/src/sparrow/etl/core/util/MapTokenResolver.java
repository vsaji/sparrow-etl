package sparrow.etl.core.util;

import java.util.Map;

public interface MapTokenResolver {
  public abstract String getTokenValue(String token, Map values);
}
