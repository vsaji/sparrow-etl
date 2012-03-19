package sparrow.elt.core.notifier.event;

import sparrow.elt.core.config.SparrowEventConfig;

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public class ExceptionEventEvaluator implements EventEvaluator{

  private final SparrowEventConfig config;

  /**
   *
   * @param config SparrowEventConfig
   */
  public ExceptionEventEvaluator(SparrowEventConfig config) {
    this.config = config;
  }

  /**
   * evaluate
   *
   * @param event Event
   */
  public void evaluate(Event event) {
    event.setNotifierName(config.getNotifierName());
    event.setMessage("Enriched by Evaluator["+event.getMessage()+"]");
  }

  /**
   * getConfig
   *
   * @return SparrowEventConfig
   */
  public SparrowEventConfig getConfig() {
    return config;
  }
}
