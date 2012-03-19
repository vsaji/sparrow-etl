package sparrow.elt.core.initializer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import sparrow.elt.core.StandaloneRequestProcessor;
import sparrow.elt.core.config.DataTransformerConfig;
import sparrow.elt.core.context.SparrowApplicationContext;
import sparrow.elt.core.exception.InitializationException;
import sparrow.elt.core.fifo.FIFO;
import sparrow.elt.core.loadbalance.RequestAssignerPolicy;
import sparrow.elt.core.log.SparrowLogger;
import sparrow.elt.core.log.SparrowrLoggerFactory;
import sparrow.elt.core.util.Constants;


/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */
public class RequestProcessInitializer
    extends GenericThreadInitializer {

  private static final SparrowLogger logger = SparrowrLoggerFactory.
      getCurrentInstance(
      RequestProcessInitializer.class);

  private List requestProcessorHolder = null;

  /**
   *
   * @param context SparrowApplicationContext
   */
  RequestProcessInitializer() {
    this.requestProcessorHolder = new ArrayList();
  }

  /**
   *
   * @throws InitializationException
   */
  void initialize(SparrowApplicationContext context, Map requestFifos,
                  RequestAssignerPolicy writerRA) {
    try {

      DataTransformerConfig transConfig = context.getConfiguration().
          getDataTransformer();
      int numberOfThread = transConfig.getThreadCount();

      for (int i = 0; i < numberOfThread; i++) {
        String threadName = Constants.PREFIX_DATA_TRANSFORMER + i;

        if (logger.isDebugEnabled()) {
          logger.debug("Initializing DataTransformer : " + threadName);
        }
        StandaloneRequestProcessor reqProcessor = new
            StandaloneRequestProcessor(context);
        reqProcessor.setRequestFIFO( (FIFO) requestFifos.get(
            threadName));
        reqProcessor.setWriterRequestAssigner(writerRA);
        super.addThread(reqProcessor, threadName);
        this.requestProcessorHolder.add(reqProcessor);
      }

      super.startThreads();
    }
    catch (Exception e) {
      throw new InitializationException(e);
    }
  }

  /**
   * stopProcess
   */
  void stopProcess() {
    logger.warn("Application shutdown initiated");
    for (Iterator iter = this.requestProcessorHolder.iterator(); iter.hasNext(); ) {
      StandaloneRequestProcessor item = (StandaloneRequestProcessor) iter.next();
      item.stopProcess = true;
    }
  }

}
