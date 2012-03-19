package sparrow.elt.core.exception;


public class DependancyCheckException
    extends SparrowException {


  public DependancyCheckException() {
    super();
   }

  public DependancyCheckException(Exception e) {
    super(e);
  }

  public DependancyCheckException(String message, Exception e) {
    super(message, e);
  }

  public DependancyCheckException(String code, String des) {
	    super(code,des);
	  }
  
  public DependancyCheckException(String message) {
    super(message);
  }
}
