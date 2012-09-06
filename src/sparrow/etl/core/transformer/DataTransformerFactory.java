package sparrow.etl.core.transformer;

import sparrow.etl.core.config.SparrowDataTransformerConfig;
import sparrow.etl.core.util.IObjectPoolLifeCycle;

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public interface DataTransformerFactory extends IObjectPoolLifeCycle{

  public abstract DataTransformer getDataTransformer();

  public abstract SparrowDataTransformerConfig getSparrowDataTransformerConfig();

}
