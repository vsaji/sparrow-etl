package sparrow.etl.core.resource;

import java.util.HashMap;

import sparrow.etl.core.exception.ResourceException;


public class ResourceManager {

  private HashMap resources = null;

  public ResourceManager() {
    resources = new HashMap();
  }

  public void addResource(Resource rs) {
    resources.put(rs.getName(), rs);
  }

  public Resource getResource(String key) throws ResourceException {
    Object o = resources.get(key);
    if (o == null) {
      throw new ResourceException("Resource [" + key +
                                  "] could not be located.");
    }
    return (Resource) o;
  }

}
