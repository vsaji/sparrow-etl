package sparrow.elt.core.config;

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */
public abstract class ConfigurationAdapter
    implements IConfiguration {

  private ModuleConfig module = null;
  private ServicesConfig services = null;
  private DataProvidersConfig dataproviders = null;
  private DataExtractorConfig dataExtractor = null;
  private DataTransformerConfig dataTransformer = null;
  private DataLookUpConfig dataLookUp = null;
  private DataWritersConfig datawriters = null;
  private ExceptionHandlerConfig exceptionConfig = null;
  private NotifiersConfig notifiersConfig = null;
  private ResourcesConfig resources = null;

  public ModuleConfig getModule() {
    return module;
  }

  public ServicesConfig getServices() {
    return services;
  }

  public DataProvidersConfig getDataProviders() {
    return dataproviders;
  }

  public ExceptionHandlerConfig getExceptionHandler() {
    return exceptionConfig;
  }

  public DataExtractorConfig getDataExtractor() {
    return dataExtractor;
  }

  public DataWritersConfig getDataWriters() {
    return datawriters;
  }

  public DataTransformerConfig getDataTransformer() {
    return dataTransformer;
  }

  public DataLookUpConfig getDataLookUp() {
    return dataLookUp;
  }

  protected void setDataExtractor(DataExtractorConfig dataExtractor) {
    this.dataExtractor = dataExtractor;
  }

  protected void setDataLookUp(DataLookUpConfig dataLookUp) {
    this.dataLookUp = dataLookUp;
  }

  protected void setDataTransformer(DataTransformerConfig dataTransformer) {
    this.dataTransformer = dataTransformer;
  }

  protected void setExceptionConfig(ExceptionHandlerConfig exceptionConfig) {
    this.exceptionConfig = exceptionConfig;
  }

  protected void setModule(ModuleConfig module) {
    this.module = module;
  }

  protected void setServices(ServicesConfig services) {
    this.services = services;
  }

  protected void setDataProviders(DataProvidersConfig dataproviders) {
    this.dataproviders = dataproviders;
  }

  protected void setDataWriters(DataWritersConfig datawriters) {
    this.datawriters = datawriters;
  }

  /**
   * getResources
   *
   * @return ResourcesConfig
   */
  public ResourcesConfig getResources() {
    return resources;
  }

  public NotifiersConfig getNotifiers() {
    return notifiersConfig;
  }

  protected void setResources(ResourcesConfig resources) {
    this.resources = resources;
  }

  public void setNotifiers(NotifiersConfig notifiersConfig) {
    this.notifiersConfig = notifiersConfig;
  }

}
