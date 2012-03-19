package sparrow.elt.core.notifier.event;

import sparrow.elt.core.config.SparrowEventConfig;
import sparrow.elt.core.exception.EvaluatorException;

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
