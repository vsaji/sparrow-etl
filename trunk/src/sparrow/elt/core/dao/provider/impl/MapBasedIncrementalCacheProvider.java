/**
 * 
 */
package sparrow.elt.core.dao.provider.impl;

import java.util.Map;

import sparrow.elt.core.config.SparrowDataProviderConfig;
import sparrow.elt.core.dao.impl.RecordSet;
import sparrow.elt.core.dao.impl.RecordSetImpl_Disconnected;
import sparrow.elt.core.dao.provider.DataProvider;
import sparrow.elt.core.exception.DataException;


/**
 * @author Saji Venugopalan
 * 
 */
public class MapBasedIncrementalCacheProvider extends MapBasedCacheProvider
		implements IncrementalCacheProvider {

	private final SingletonDataLoader sdl = new SingletonDataLoader();

	/**
	 * @param provider
	 * @param config
	 */
	public MapBasedIncrementalCacheProvider(DataProvider provider,
			SparrowDataProviderConfig config) {
		super(provider, config);
	}

	/**
	 * 
	 * @throws CloneNotSupportedException
	 * @return Object
	 */
	public Object clone() throws CloneNotSupportedException {
		MapBasedIncrementalCacheProvider clone = (MapBasedIncrementalCacheProvider) super
				.clone();
		clone.provider = clone.provider;
		return clone;
	}
	
	/**
	 * 
	 */
	public void loadData() throws DataException {}
	/**
	 * 
	 */
	public RecordSet getData() throws DataException {
		RecordSet rs = superGetData();
		try {
			if (rs.getRowCount() == 0) {
				rs = sdl.loadFromDB(this, getQuery());
				//logger.info("["+getName()+"]["+this.hashCode()+"]["+sdl.hashCode()+"]");
			}
		} catch (DataException ex) {
			ex.printStackTrace();
			rs = new RecordSetImpl_Disconnected();
		} catch (Exception ex) {
			ex.printStackTrace();
			rs = new RecordSetImpl_Disconnected();
		} finally {
			resetQueryObject_();
		}
		return rs;
	}

	protected void resetQueryObject() {
		// empty implementation to avoid the super.getData() to clean the query
		// object
	}

	/**
	 * 
	 */
	private void resetQueryObject_() {
		super.resetQueryObject();
	}

	/**
	 * 
	 */
	public RecordSet loadFromCache(String filter, Map param, String columns)
			throws DataException {
		// Not Applicable for MapBased Cache
		return null;
	}

	/**
	 * 
	 */
	public RecordSet loadFromCache(String columnNames, Map param)
			throws Exception {
		// Not Applicable for MapBased Cache
		return null;
	}

	/**
	 * 
	 */
	public RecordSet superGetData() throws DataException {
		return super.getData();
	}

	/**
	 * 
	 */
	public DataProvider getDataProvider() {
		return provider;
	}

}
