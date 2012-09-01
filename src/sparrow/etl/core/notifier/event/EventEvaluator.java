package sparrow.etl.core.notifier.event;

import sparrow.etl.core.config.SparrowEventConfig;
import sparrow.etl.core.exception.EvaluatorException;

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public interface EventEvaluator {

  public abstract void evaluate(Event event)throws EvaluatorException;
  public abstract SparrowEventConfig getConfig();

}
