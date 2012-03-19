package sparrow.elt.core;

import sparrow.elt.core.context.SparrowApplicationContext;
import sparrow.elt.core.exception.RequestProcessException;
import sparrow.elt.core.fifo.FIFO;
import sparrow.elt.core.log.SparrowLogger;
import sparrow.elt.core.log.SparrowrLoggerFactory;
import sparrow.elt.core.monitor.EndCycleMonitor;
import sparrow.elt.core.vo.SparrowResultHolder;

public class StandaloneRequestFinalizer
    extends AbstractRequestFinalizer
    implements Runnable {

  public boolean stopProcess = false;
  private SparrowResultHolder oldRef = null;
  /**
   *
   */
  private static final SparrowLogger logger = SparrowrLoggerFactory.
      getCurrentInstance(
      StandaloneRequestFinalizer.class);

  /**
   *
   */
  private FIFO fifo = null;

  /**
   *
   * @param context SparrowContext
   */
  public StandaloneRequestFinalizer(SparrowApplicationContext context) {
    super(context);
  }

  /**
   *
   * @param fifo ResponseFIFO
   */
  public void setFIFO(FIFO fifo) {
    this.fifo = fifo;
  }

  /**
   * run
   */
  public void run() {

    while (true && (!stopProcess)) {

      Object o = fifo.consume();
      if (o == FIFO.BEGIN_CYCLE_MESSAGE || o == FIFO.END_CYCLE_MESSAGE) {
        if (o == FIFO.END_CYCLE_MESSAGE) {
          EndCycleMonitor.getInstance().increment();
        }
      }
      else if (o instanceof SparrowResultHolder) {
        SparrowResultHolder sparrowResultHolder = (SparrowResultHolder) o;
        oldRef = sparrowResultHolder;
        try {
          processRequest(sparrowResultHolder);
        }
        catch (RequestProcessException ex) {
          logger.error(
              "RequestProcessException occured while processing Driver Result:" +
              sparrowResultHolder.getDataOutputHolder().getDriverRow(), ex);
          ex.printStackTrace();
        }
        catch (Exception ex) {
          logger.error(
              "Exception occured while processing Driver Result" +
              sparrowResultHolder.getDataOutputHolder().getDriverRow(), ex);
          ex.printStackTrace();
        }
        
      }
      else {
        logger.error(
            "Unknown object consumed:" + o);
      }
    }
  }

  public void stopProcess() {

  }

}
