package sparrow.etl.core.extractor;

import sparrow.etl.core.exception.DataException;
import sparrow.etl.core.vo.DataHolder;


/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */
public interface DataExtractor {

  public abstract void initialize();

  public abstract DataHolder loadData() throws DataException;

  public abstract void destroy() throws DataException;

}
