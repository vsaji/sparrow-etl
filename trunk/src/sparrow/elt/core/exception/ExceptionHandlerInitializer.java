package sparrow.elt.core.exception;

import sparrow.elt.core.context.SparrowApplicationContext;

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */
public class ExceptionHandlerInitializer {

  /**
   *
   */
  public ExceptionHandlerInitializer() {}

  /**
   *
   * @param context SparrowApplicationContext
   */
  public void initialize(SparrowApplicationContext context) {
    ExceptionHandler.initialize(context.getConfiguration());
  }
}
