package sparrow.etl.core.fifo;

public class ResponseFIFO
    extends FIFO {

  public ResponseFIFO(String fifoName) {
    super(fifoName, 100);
  }

}
