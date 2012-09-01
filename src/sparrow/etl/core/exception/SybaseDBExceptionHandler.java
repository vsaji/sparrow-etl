package sparrow.etl.core.exception;

import java.sql.SQLException;

public class SybaseDBExceptionHandler
    extends ExceptionHandlerAdapter {

  public SybaseDBExceptionHandler(ErrorConfig config) {
    super(config);
  }

  /**
   *
   * @param e SQLException
   */
  public void handle(SQLException e) {
    setErrorCode(String.valueOf(e.getErrorCode()));
    setErrorDesc(e.getMessage());
  }

  /**
   * getHandlerClass
   *
   * @return String
   */
  public String getHandlerClass() {
    return "";
  }

}
