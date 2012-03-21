/* Generated by Together */

package sparrow.elt.core.config;

import sparrow.elt.core.exception.ConfigurationReadingException;

public interface IConfiguration {

  abstract ModuleConfig getModule();

  abstract ResourcesConfig getResources();

  abstract DataExtractorConfig getDataExtractor();

  abstract ServicesConfig getServices();

  abstract DataProvidersConfig getDataProviders();

  abstract DataWritersConfig getDataWriters();

  abstract DataTransformerConfig getDataTransformer();

  abstract DataLookUpConfig getDataLookUp();

  abstract ExceptionHandlerConfig getExceptionHandler();

  abstract NotifiersConfig getNotifiers();

  abstract void loadConfiguration() throws ConfigurationReadingException;

}