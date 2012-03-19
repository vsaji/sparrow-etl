package sparrow.elt.core.dao.provider;


public interface DataProviderElementExtn
    extends DataProviderElement {

  public abstract void initialize();

  public abstract void setDataProvider(DataProvider dataProvider);

  public abstract void destroy();

}
