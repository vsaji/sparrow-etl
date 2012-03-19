package sparrow.elt.core.exception;

import java.sql.SQLException;
import javax.jms.JMSException;

import org.apache.commons.dbcp.SQLNestedException;

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */
public class SparrowExceptionHandler
    extends ExceptionHandlerAdapter {

  /**
   *
   * @param config ErrorConfig
   */
  public SparrowExceptionHandler(ErrorConfig config) {
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
   *
   * @param e SQLException
   */
  public void handle(SQLNestedException e) {
    if (e.getCause() instanceof SQLException) {
      SQLException e1 = (SQLException) e.getCause();
      setErrorCode(String.valueOf(e1.getSQLState()));
      setErrorDesc(e.getMessage());
    }
  }

  /**
   *
   * @param e JMSException
   */
  public void handle(JMSException e) {
    setErrorCode(e.getErrorCode());
    setErrorDesc(e.getMessage());
  }

  /**
   *
   * @param e CycleException
   */
  public void handle(EventNotifierException e) {
    setErrorCode(e.getErrorCode());
    setErrorDesc(e.getErrorDescription());
  }

  /**
   *
   * @param e InitializationException
   */
  public void handle(InitializationException e) {
    setErrorCode(e.getErrorCode());
    setErrorDesc(e.getErrorDescription());
  }

  /**
   *
   * @param e InitializationException
   */
  public void handle(SparrowRuntimeException e) {
    setErrorCode(e.getErrorCode());
    setErrorDesc(e.getErrorDescription());
  }

  /**
   *
   * @param e ResourceException
   */
  public void handle(ResourceException e) {
    setErrorCode(e.getErrorCode());
    setErrorDesc(e.getErrorDescription());
  }

  /**
   *
   * @param e ValidatorException
   */
  public void handle(ValidatorException e) {
    setErrorCode(e.getErrorCode());
    setErrorDesc(e.getErrorDescription());
  }

  /**
   *
   * @param e DataException
   */
  public void handle(DataException e) {
    setErrorCode(e.getErrorCode());
    setErrorDesc(e.getErrorDescription());
  }

  /**
   *
   * @param e SparrowException
   */
  public void handle(SparrowException e) {
    setErrorCode(e.getErrorCode());
    setErrorDesc(e.getErrorDescription());
  }

  /**
   *
   * @param e SparrowException
   */
  public void handle(ScriptException e) {
    setErrorCode(e.getErrorCode());
    setErrorDesc(e.getErrorDescription());
  }

  /**
   *
   * @param e SparrowTransactionException
   */
  public void handle(SparrowTransactionException e) {
    setErrorCode(e.getErrorCode());
    setErrorDesc(e.getErrorDescription());
  }

  /**
   *
   * @param e SparrowTransactionException
   */
  public void handle(TypeCastException e) {
    setErrorCode(e.getErrorCode());
    setErrorDesc(e.getErrorDescription());
  }

  /**
   *
   * @param e RequestProcessException
   */
  public void handle(RequestProcessException e) {
    setErrorCode(e.getErrorCode());
    setErrorDesc(e.getErrorDescription());
  }

  /**
   *
   * @param e ObjectCreationException
   */
  public void handle(ObjectCreationException e) {
    setErrorCode(e.getErrorCode());
    setErrorDesc(e.getErrorDescription());
  }

  /**
   *
   * @param e NotifierException
   */
  public void handle(NotifierException e) {
    setErrorCode(e.getErrorCode());
    setErrorDesc(e.getErrorDescription());
  }

  /**
   *
   * @param e EvaluatorException
   */
  public void handle(EvaluatorException e) {
    setErrorCode(e.getErrorCode());
    setErrorDesc(e.getErrorDescription());
  }

  /**
   *
   * @param e DataTransformerException
   */
  public void handle(DataTransformerException e) {
    setErrorCode(e.getErrorCode());
    setErrorDesc(e.getErrorDescription());
  }

  /**
   *
   * @param e EnrichDataException
   */
  public void handle(EnrichDataException e) {
    setErrorCode(e.getErrorCode());
    setErrorDesc(e.getErrorDescription());
  }

  /**
   *
   * @param e ProcessException
   */
  public void handle(ProcessException e) {
    setErrorCode(e.getErrorCode());
    setErrorDesc(e.getErrorDescription());
  }

  /**
   *
   * @param e ProviderNotFoundException
   */
  public void handle(ProviderNotFoundException e) {
    setErrorCode(e.getErrorCode());
    setErrorDesc(e.getErrorDescription());
  }

  /**
   *
   * @param e SchedulerException
   */
  public void handle(SchedulerException e) {
    setErrorCode(e.getErrorCode());
    setErrorDesc(e.getErrorDescription());
  }

  /**
   *
   * @param e ServiceUnavailableException
   */
  public void handle(ServiceUnavailableException e) {
    setErrorCode(e.getErrorCode());
    setErrorDesc(e.getErrorDescription());
  }

  /**
   *
   * @param e ServiceInitializationException
   */
  public void handle(ServiceInitializationException e) {
    setErrorCode(e.getErrorCode());
    setErrorDesc(e.getErrorDescription());
  }

//  /**
//   *
//   * @param e MethodInvocationException
//   */
//  public void handle(MethodInvocationException e) {
//    setErrorCode(e.getErrorCode());
//    setErrorDesc(e.getErrorDescription());
//  }

  /**
   *
   * @param e FIFOException
   */
  public void handle(FIFOException e) {
    setErrorCode(e.getErrorCode());
    setErrorDesc(e.getErrorDescription());
  }

  /**
   *
   * @param e DataWriterException
   */
  public void handle(DataWriterException e) {
    setErrorCode(e.getErrorCode());
    setErrorDesc(e.getErrorDescription());
  }

  /**
   *
   * @param e DependancyCheckException
   */
  public void handle(DependancyCheckException e) {
    setErrorCode(e.getErrorCode());
    setErrorDesc(e.getErrorDescription());
  }

  /**
   * getHandlerClass
   *
   * @return String
   */
  public String getHandlerClass() {
    return this.getClass().getName();
  }

}
