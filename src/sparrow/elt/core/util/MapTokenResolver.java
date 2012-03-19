package sparrow.elt.core.util;

import java.util.Map;

public interface MapTokenResolver {
  public abstract String getTokenValue(String token, Map values);
}
