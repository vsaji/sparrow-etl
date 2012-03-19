package sparrow.elt.core.context;

import java.util.Collection;

import sparrow.elt.core.config.IConfiguration;
import sparrow.elt.core.resource.ResourceManager;
import sparrow.elt.core.transformer.DataTransformer;
import sparrow.elt.core.transformer.DataTransformerFactory;


public interface SparrowApplicationContext
    extends SparrowContext {

  public IConfiguration getConfiguration();

  public Collection getServices();

  public DataTransformer getDataTransformer();

  public void setDataTransformerFactory(DataTransformerFactory factory);

  public DataTransformerFactory getDataTransformerFactory();

  public ResourceManager getResourceManager();

}
