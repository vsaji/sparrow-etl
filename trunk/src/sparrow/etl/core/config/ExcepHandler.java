package sparrow.etl.core.config;

import sparrow.etl.jaxb.HANDLERType;

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */
public interface ExcepHandler {
  abstract String getClassName();

  abstract String getName();
}

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */
class ExceptionHandlerImpl
    implements ExcepHandler {

  private String className = null;
  private String name = null;

  ExceptionHandlerImpl(HANDLERType handler) {
    this.className = handler.getCLASS();
    this.name = handler.getNAME();
  }

  /**
   * getClassName
   *
   * @return String
   */
  public String getClassName() {
    return className;
  }

  /**
   * getName
   *
   * @return String
   */
  public String getName() {
    return name;
  }

}
