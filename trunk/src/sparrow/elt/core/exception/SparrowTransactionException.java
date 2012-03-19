package sparrow.elt.core.exception;



public class SparrowTransactionException
    extends SparrowRuntimeException {


  public SparrowTransactionException(Exception e) {
    super(e);
  }

  public SparrowTransactionException(String message, Exception e) {
    super(message, e);
  }

  public SparrowTransactionException(String message) {
    super(message);
  }

  public SparrowTransactionException(String code,String message) {
	    super(code,message);
	  }
  
}
