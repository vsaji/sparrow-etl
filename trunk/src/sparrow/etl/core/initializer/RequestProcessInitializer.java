package sparrow.etl.core.initializer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import sparrow.etl.core.StandaloneRequestProcessor;
import sparrow.etl.core.config.DataTransformerConfig;
import sparrow.etl.core.context.SparrowApplicationContext;
import sparrow.etl.core.exception.InitializationException;
import sparrow.etl.core.fifo.FIFO;
import sparrow.etl.core.loadbalance.RequestAssignerPolicy;
import sparrow.etl.core.log.SparrowLogger;
import sparrow.etl.core.log.SparrowrLoggerFactory;
import sparrow.etl.core.util.Constants;


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
