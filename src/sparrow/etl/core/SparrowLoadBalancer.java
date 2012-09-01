package sparrow.etl.core;

import java.util.Collection;
import java.util.Map;

import sparrow.etl.core.context.SparrowApplicationContext;
import sparrow.etl.core.dao.impl.RecordSet;
import sparrow.etl.core.dao.impl.RowIterator;
import sparrow.etl.core.exception.DataException;
import sparrow.etl.core.fifo.FIFO;
import sparrow.etl.core.loadbalance.LBPolicyFactory;
import sparrow.etl.core.loadbalance.RequestAssignerPolicy;
import sparrow.etl.core.log.SparrowLogger;
import sparrow.etl.core.log.SparrowrLoggerFactory;
import sparrow.etl.core.monitor.CycleMonitor;
import sparrow.etl.core.monitor.EndCycleMonitor;
import sparrow.etl.core.vo.DataHolder;


/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */
public class SparrowLoadBalancer {

  /**
   *
   */
  private static SparrowLoadBalancer instance = null;
  private RequestAssignerPolicy assignerPolicy = null;
  private Map requestFifos = null;
  private CycleMonitor monitor = null;
  private SparrowApplicationContext context = null;

  /**
   *
   */
  private static final SparrowLogger logger = SparrowrLoggerFactory.
      getCurrentInstance(
      SparrowLoadBalancer.class);

  /**
   *
   */
  private SparrowLoadBalancer() {
  }

  /**
   *
   * @return SparrowLoadBalancer
   */
  static SparrowLoadBalancer getInstance() {
    if (instance == null) {
      instance = new SparrowLoadBalancer();
      return instance;
    }
    else {
      return instance;
    }
  }

  void initializeKeyAssigner() {
    assignerPolicy = LBPolicyFactory.getTransFormerLBInstance(requestFifos, context);
    logger.info("Load Balancer Implementation class ["+assignerPolicy.getClass().getName()+"]");
  }

  /**
   *
   * @param requestFifos Map
   */
  void setFIFOCollection(Map requestFifos) {
    if (this.requestFifos == null) {
      this.requestFifos = requestFifos;
    }
  }

  /**
   *
   * @param monitor CycleMonitor
   */
  void setCycleMonitor(CycleMonitor monitor) {
    if (this.monitor == null) {
      this.monitor = monitor;
    }
  }

  /**
   *
   * @param monitor CycleMonitor
   */
  void setContext(SparrowApplicationContext context) {
    if (this.context == null) {
      this.context = context;
    }
  }

  /**
   *
   */
  public void processData(DataHolder dataHolder) throws DataException {

    RecordSet rs = dataHolder.getData();
    Collection fifos = this.requestFifos.values();
    FIFO[] ffos = (FIFO[]) fifos.toArray(new FIFO[fifos.
        size()]);

    postBeginCycleMessage(ffos);

    for (RowIterator iter = rs.iterator(); iter.hasNext(); ) {
      assignerPolicy.assign(iter.next());
    }

    postEndCycleMessage(ffos);
    rs.close();
  }

  /**
   *
   * @param fifos RequestFIFO[]
   */
  private void postBeginCycleMessage(FIFO[] fifos) {

    for (int i = 0; i < fifos.length; i++) {
      fifos[i].produce(fifos[i].BEGIN_CYCLE_MESSAGE);
    }
    EndCycleMonitor.getInstance().reset();
    monitor.notifyBeginCycle();
  }

  /**
   *
   * @param fifos RequestFIFO[]
   */
  private void postEndCycleMessage(FIFO[] fifos) {
    for (int i = 0; i < fifos.length; i++) {
      fifos[i].produce(fifos[i].END_CYCLE_MESSAGE);
    }
  }

}
