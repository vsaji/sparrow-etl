package sparrow.etl.core.exception;

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public class SchedulerException extends SparrowException{

  public SchedulerException() {
    super();
  }

  public SchedulerException(String msg) {
   super(msg);
 }

 public SchedulerException(String code, String message) {
   super(code, message);
 }

 public SchedulerException(String msg, Exception original) {
   super(msg, original);
 }

 public SchedulerException(Exception original) {
   super(original);
 }

}
