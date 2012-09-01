package sparrow.etl.core.config;

public interface DependentIndexingSupport {

  abstract String getName();

  abstract String getDepends();

}
