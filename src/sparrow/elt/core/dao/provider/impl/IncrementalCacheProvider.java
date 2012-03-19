/**
 * 
 */
package sparrow.elt.core.dao.provider.impl;

import java.util.Map;

import sparrow.elt.core.dao.impl.RecordSet;
import sparrow.elt.core.dao.provider.DataProvider;
import sparrow.elt.core.exception.DataException;


/**
 * @author Saji
 *
 */
public interface IncrementalCacheProvider {
	public RecordSet loadFromCache(String filter, Map param, String columns) throws DataException;
	public RecordSet loadFromCache(String columnNames,Map param)throws Exception;
	public RecordSet superGetData()throws DataException;
	public DataProvider getDataProvider();
	public void createCache(RecordSet rs) throws DataException;
}
