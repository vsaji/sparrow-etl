package sparrow.etl.core.context;

import java.util.Collection;

import sparrow.etl.core.config.IConfiguration;
import sparrow.etl.core.resource.ResourceManager;
import sparrow.etl.core.transformer.DataTransformer;
import sparrow.etl.core.transformer.DataTransformerFactory;


public interface SparrowApplicationContext
    extends SparrowContext {

  public IConfiguration getConfiguration();

  public Collection getServices();

  public DataTransformer getDataTransformer();

  public void setDataTransformerFactory(DataTransformerFactory factory);

  public DataTransformerFactory getDataTransformerFactory();

  public ResourceManager getResourceManager();

}
