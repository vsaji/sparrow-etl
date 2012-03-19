package sparrow.elt.core.cycledependency;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import sparrow.elt.core.exception.DependancyCheckException;
import sparrow.elt.core.exception.SparrowRuntimeException;


/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */
public class CycleDependencyChecker {

  private static final List PRE_DEPENDANTS = new ArrayList();

  /**
   *
   */
  private CycleDependencyChecker() {
  }

  /**
   *
   * @return Status
   */
  public static final Status checkDependencyChain() {

    Status status = new Status();

    for (Iterator iter = PRE_DEPENDANTS.iterator(); iter.hasNext(); ) {
      CycleEventListener item = (CycleEventListener) iter.next();
      try {
        boolean result = item.checkDependency();
        status.setStatus(item.getName(), result,
                         item.isProcessTerminationRequired(),
                         item.getStatusDescription());
      }
      catch (DependancyCheckException ex) {
        throw new SparrowRuntimeException(
            "DependancyCheckException occured while checking [" + item.getName() +
            "]", ex);
      }
      catch (Exception ex) {
        throw new SparrowRuntimeException("Exception occured while checking [" +
                                        item.getName() + "]", ex);
      }
    }

    return status;
  }

  /**
   *
   * @param dependent Dependable
   */
  public static final void registerPreDependant(CycleEventListener listener) {
    PRE_DEPENDANTS.add(listener);
  }

  /**
   *
   * <p>Title: </p>
   * <p>Description: </p>
   * <p>Copyright: Copyright (c) 2004</p>
   * <p>Company: </p>
   * @author Saji Venugopalan
   * @version 1.0
   */
  public static class Status {

    private StringBuffer strBuff = null;
    private boolean overallStatus = false;
    private boolean processTerminationStatus = false;

    /**
     *
     */
    Status() {
      strBuff = new StringBuffer("\n");
    }

    /**
     *
     * @return boolean
     */
    public boolean getOverAllStatus() {
      return overallStatus;
    }

    /**
     *
     * @return boolean
     */
    public boolean getProcessTerminationStatus() {
      return processTerminationStatus;
    }

    /**
     *
     * @return String
     */
    public String getStatusReport() {
      return strBuff.toString();
    }

    /**
     *
     * @param dependantName String
     * @param status boolean
     * @param description String
     */
    void setStatus(String dependantName, boolean status,
                   boolean processTermStatus, String description) {

      if (! (overallStatus) && (status)) {
        overallStatus = status;
      }

      if (! (processTerminationStatus) && (processTermStatus)) {
        processTerminationStatus = processTermStatus;
      }

      strBuff.append("[").append(dependantName).append("][").append("Status:" +
          ( (status) ? "clear" :
           "unclear")).append("][").append("Process Termination:" +
                                           ( (processTermStatus) ? "required" :
                                            "not required")).append("][").
          append(description).append("]\n");
    }
  }

}
