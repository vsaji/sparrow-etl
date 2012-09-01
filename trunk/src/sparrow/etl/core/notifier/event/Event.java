package sparrow.etl.core.notifier.event;

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public class Event {

  public static final String EXCEPTION="EXCEPTION";


  private String type = null;
  private String notifierName = null;
  private String message = null;

  public Event() {
  }


  public String getType() {
    return type;
  }

  public void setNotifierName(String notifierName) {
    this.notifierName = notifierName;
  }

  public void setType(String type) {
    this.type = type;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getNotifierName() {
    return notifierName;
  }

  public String getMessage() {
    return message;
  }

}
