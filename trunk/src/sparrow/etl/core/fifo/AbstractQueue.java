package sparrow.etl.core.fifo;

public class AbstractQueue {

  private String fifoName = null;

  protected AbstractQueue(String fifoName) {
    this.fifoName = fifoName;
  }

}
