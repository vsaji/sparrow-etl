package sparrow.etl.core.vo;

import sparrow.etl.core.transformer.DriverRowEventListener;
import sparrow.etl.core.transformer.TransformerLCEvent;


/**
 *
 * <p>Title: DTO object to carry the Transformer request to the Writer Layer</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */
public class SparrowResultHolder {

  private DataOutputHolder result = null;
  private DriverRowEventListener dl = null;
  private boolean endCycle = false;
  private boolean ignoreRequest = false;
  private boolean softRejection = true;
  private TransformerLCEvent lcEvent = null;
  private long startTime = 0;

  /**
   *
   */
  public SparrowResultHolder() {
  }

  public DataOutputHolder getDataOutputHolder() {
    return result;
  }

  public void setDriverRowEventListener(DriverRowEventListener dl) {
    this.dl = dl;
  }

  public void setDataOutputHolder(DataOutputHolder result) {
    this.result = result;
  }

  public void setEndCycle(boolean endCycle) {
    this.endCycle = endCycle;
  }

  public void setIgnoreRequest(boolean ignoreRequest) {
    this.ignoreRequest = ignoreRequest;
  }

  public void setSoftRejection(boolean softRejection) {
    this.softRejection = softRejection;
  }

  public void setLcEvent(TransformerLCEvent lcEvent) {
    if (this.lcEvent == null) {
      this.lcEvent = lcEvent;
    }
  }

  public void setStartTime(long startTime) {
    this.startTime = startTime;
  }

  public DriverRowEventListener getDriverRowEventListener() {
    return dl;
  }

  public boolean isEndCycle() {
    return endCycle;
  }

  public boolean isIgnoreRequest() {
    return ignoreRequest;
  }

  public long getStartTime() {
    return startTime;
  }

  public boolean isSoftRejection() {
    return softRejection;
  }

  /**
   *
   */
  public void destroy() {

    lcEvent.returnObject();
    if (result != null) {
      result.destroy();
    }
    result = null;
    dl = null;
  }

}
