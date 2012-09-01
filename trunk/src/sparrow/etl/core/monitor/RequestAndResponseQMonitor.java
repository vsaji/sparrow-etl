package sparrow.etl.core.monitor;

import java.util.Iterator;
import java.util.Map;

import sparrow.etl.core.fifo.FIFO;
import sparrow.etl.core.log.SparrowLogger;
import sparrow.etl.core.log.SparrowrLoggerFactory;


/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public class RequestAndResponseQMonitor
    implements Reporter {

  private Map transQueues;
  private Map writerQueues;  

  
  private static final SparrowLogger logger = SparrowrLoggerFactory.
      getCurrentInstance(
      RequestAndResponseQMonitor.class);

  /**
   *
   * @param reqMap Map
   * @param resQ FIFO
   */
  public RequestAndResponseQMonitor(Map transQueues, Map writerQueues) {
	 this.transQueues = transQueues;
	 this.writerQueues = writerQueues;	    
  }

  /**
   * destory
   */
  public void destory() {
    this.writerQueues = null;
    this.transQueues = null;    
  }

  /**
   * report
   */
  public void report() {
      StringBuffer tSb = new StringBuffer();
      StringBuffer wSb = new StringBuffer();      
    try{

      for (Iterator it = transQueues.keySet().iterator(); it.hasNext(); ) {
        String key = it.next().toString();
        FIFO req = (FIFO) transQueues.get(key);
        tSb.append("[" + key + "]=> " + req.getDepth()).append(",");
      }

      for (Iterator it = writerQueues.keySet().iterator(); it.hasNext(); ) {
          String key = it.next().toString();
          FIFO wr = (FIFO) writerQueues.get(key);
          wSb.append("[" + key + "]=> " + wr.getDepth()).append(",");
        }
      
    }catch(Exception ex){
      logger.error("Exception occured while get RequestFIFO queue depth["+ex.getMessage()+"]");
    }
    tSb.deleteCharAt(tSb.length()-1);
    wSb.deleteCharAt(wSb.length()-1);    
    logger.info("TRANS_Q.size[" + tSb.toString()+"]");
    logger.info("WRITER_Q.size[" + wSb.toString()+"]");
  }
}
