package sparrow.etl.core;

import sparrow.etl.core.context.SparrowApplicationContext;
import sparrow.etl.core.dao.impl.ResultRow;
import sparrow.etl.core.exception.RequestProcessException;
import sparrow.etl.core.fifo.FIFO;
import sparrow.etl.core.loadbalance.RequestAssignerPolicy;
import sparrow.etl.core.log.SparrowLogger;
import sparrow.etl.core.log.SparrowrLoggerFactory;
import sparrow.etl.core.util.SparrowUtil;
import sparrow.etl.core.vo.SparrowResultHolder;

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */
public class StandaloneRequestProcessor
    extends AbstractRequestProcessor
    implements Runnable {

  /**
   *
   */
  private static final SparrowLogger logger = SparrowrLoggerFactory.
      getCurrentInstance(
      StandaloneRequestProcessor.class);

  private FIFO requestFifo = null;
  private RequestAssignerPolicy requestAssigner = null;

  public boolean stopProcess = false;

  /**
   *
   * @param context SparrowContext
   */
  public StandaloneRequestProcessor(SparrowApplicationContext context) {
    super(context);
  }

  /**
   *
   * @param responseFifo ResponseFIFO
   */
  public void setWriterRequestAssigner(RequestAssignerPolicy requestAssigner) {
    this.requestAssigner = requestAssigner;
  }

  /**
   *
   * @param requestFifo RequestFIFO
   */
  public void setRequestFIFO(FIFO requestFifo) {
    this.requestFifo = requestFifo;
  }

  /**
   * publish
   */
  public void publish(SparrowResultHolder sparrowResultHolder) {
	  requestAssigner.assign(sparrowResultHolder);
  }

  /**
   *
   * @param o Object
   */
  private void publishObject(Object o) {
	  requestAssigner.assign(o, 0);
  }

  /**
   * run
   */
  public void run() {
    int ii = 0;
    while (true && (!stopProcess)) {

      Object o = requestFifo.consume();
      Object nxtMsg = requestFifo.getNextObject();

      if (nxtMsg == FIFO.END_CYCLE_MESSAGE) {

        Object nxtMsg2 = requestFifo.consume();

        if (o == FIFO.BEGIN_CYCLE_MESSAGE) {
          // Queue contain Begin immediately followed by a End message
          publishObject(nxtMsg); // Post the end message of the queue
        }
        else {
          if (o == FIFO.END_CYCLE_MESSAGE) {} // Fatal error. two end message in succession
          // Post end tagged packet -- o'
          else if (o instanceof ResultRow) {
            processMessage( (ResultRow) o, nxtMsg);
          }
        }
      }
      else {
        if (o == FIFO.BEGIN_CYCLE_MESSAGE) {
          // Do nothing
        }
        else {
          processMessage( (ResultRow) o, nxtMsg);
        }
      }

      /* if (o == FIFO.BEGIN_CYCLE_MESSAGE || o == FIFO.END_CYCLE_MESSAGE) {
         if (o == FIFO.END_CYCLE_MESSAGE && nxtMsg == null) {
           publishObject(o);
         }
       }
       else if (o instanceof ResultRow) {
         ResultRow rr = (ResultRow) o;
         try {
           processRequest(rr, nxtMsg);
         }
         catch (RequestProcessException ex) {
           logger.error(
       "RequestProcessException occured while processing Driver Result:",
               ex);
         }
       }
       else {
         logger.error(
             "Unknown object consumed:" + o);
       }*/
    }
    logger.warn(Thread.currentThread().getName() + "-THREAD EXITING");
  }

  /**
   * 
   * @param rr
   * @param nextMsg
   */
  private void processMessage(ResultRow rr, Object nextMsg) {
    boolean excpt = false;
    try {
      processRequest(rr, nextMsg);
    }
    catch (RequestProcessException ex) {
      logger.error("Request process failed: " + SparrowUtil.printDriverValue(rr));
      logger.error(
          "RequestProcessException occured while processing Driver Result",
          ex);
      ex.printStackTrace();
      excpt = true;
    }
    catch (Exception ex) {
    	ex.printStackTrace();    	
      logger.error("Request process failed: " + SparrowUtil.printDriverValue(rr));
      logger.error(
          "Exception occured while processing Driver Result",
          ex);
      
      excpt = true;
    }

    try {
      if (excpt && nextMsg == FIFO.END_CYCLE_MESSAGE) {
        publishObject(nextMsg);
      }
    }
    catch (Exception ex) {
      logger.error("Request process failed: " + SparrowUtil.printDriverValue(rr));
      logger.error(
          "Exception occured while publishing END MESSAGE",
          ex);
      ex.printStackTrace();
    }
  }

}
