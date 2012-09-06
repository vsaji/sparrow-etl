package sparrow.etl.core.cycledependency;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import sparrow.etl.core.config.ConfigParam;
import sparrow.etl.core.context.SparrowApplicationContext;
import sparrow.etl.core.exception.SparrowRuntimeException;
import sparrow.etl.core.log.SparrowLogger;
import sparrow.etl.core.log.SparrowrLoggerFactory;
import sparrow.etl.core.util.Constants;
import sparrow.etl.core.util.Sortable;
import sparrow.etl.core.util.SparrowUtil;


/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */
public class ShutdownTimeListener
    implements CycleEventListener {

  private static final SparrowLogger logger = SparrowrLoggerFactory.
      getCurrentInstance(
      ShutdownTimeListener.class);

  private final boolean shutdownEntryConfigured;
  private int procShutFrmt;
  private String procShutValue;
  private boolean result, endCycle, tempResult = false;

  private String lastRunDay = null;
  private String today = null;
  private String tomorrow = null;

  private boolean restartedAftrShutdown = false;
  private boolean ignoreCycleStrategy = false;

  /**
   *
   * @param context SparrowApplicationContext
   */
  public ShutdownTimeListener(ConfigParam param,
                              SparrowApplicationContext context) {

    shutdownEntryConfigured = context.getConfiguration().getModule().
        isParameterExist(Constants.SPARROW_SHUTDOWN);
    this.ignoreCycleStrategy = SparrowUtil
	.performTernary(context.getConfiguration().getModule(),
			"ignore.cycle.strategy", false);
    if (shutdownEntryConfigured) {
      procShutValue = context.getConfiguration().getModule().getParameterValue(
          Constants.SPARROW_SHUTDOWN);
      procShutFrmt = procShutValue.split("[:]").length;

      if (procShutFrmt > 3) {
        throw new SparrowRuntimeException(" Format [" + procShutFrmt +
                                        "] does not support by SPARROW.etl. Please specify EEE:HH:mm or HH:mm");
      }

      lastRunDay = SparrowUtil.formatDate(new Date(), "EEE");
      restartedAftrShutdown = checkStartAfterShutdown();
      tomorrow = SparrowUtil.formatDate(getNextDay(), "EEE");

      if (restartedAftrShutdown) {
        logger.warn(
            "Encountered process restart after the scheduled shutdown:Process restarted @ [" +
            SparrowUtil.formatDate(new Date(), "EEE dd-MMM-yyyy HH:mm") +
            "]. Scheduled shutdown time[" + procShutValue + "]");
      }

    }

  }

  /**
   * checkDependency
   *
   * @return boolean
   */
  public boolean checkDependency() {
	 //System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@");
    if (!tempResult) {

      boolean rtnVal = false;
      if (shutdownEntryConfigured) {
        switch (procShutFrmt) {
          case 2:
        	  if(SparrowUtil.formatDate(new Date(), "EEE").equals(lastRunDay)){
        		  rtnVal = checkFormat_HH_MM(0);
        	  }else{
        		  rtnVal = true;
        	  }
            break;
          case 3:
        	lastRunDay = SparrowUtil.formatDate(getDate(), "EEE");
            rtnVal = checkFormat_EEE_HH_MM();
            break;
        }
      }

      tempResult = rtnVal;

      if (rtnVal) {
        result = rtnVal && checkEndCycle();
      }
    }
    else {
      result = tempResult && checkEndCycle();
    }
    //System.out.println("tempResult && endCycle"+tempResult +"=="+ checkEndCycle());
    return result;
  }

  
  /**
   * 
   * @return
   */
  private boolean checkEndCycle(){
	  return (ignoreCycleStrategy) ? true : endCycle;
  }
  
  /**
   *
   * @return boolean
   */
  private boolean checkStartAfterShutdown() {

    boolean rtnVal = false;
    lastRunDay = SparrowUtil.formatDate(getDate(), "EEE");

    if (shutdownEntryConfigured) {
      switch (procShutFrmt) {
        case 2:
          rtnVal = checkFormat_HH_MM(0);
          break;
        case 3:
          rtnVal = checkFormat_EEE_HH_MM();
          break;
      }
    }
    return rtnVal;
  }

  /**
   *
   * @return boolean
   */
  public boolean checkFormat_EEE_HH_MM() {

    boolean returnVal = false;
    String today = SparrowUtil.formatDate(getDate(), "EEE");
    String configDay = SparrowUtil.getDayName(procShutValue);

    if (lastRunDay.equals(today) && today.equals(configDay)) {
      returnVal = checkFormat_HH_MM( -1);
    }
    else {
      if (lastRunDay.equals(configDay)) {
        returnVal = true;
      }
    }

    return returnVal;
  }

  /**
   *
   * @return boolean
   */
  public boolean checkFormat_HH_MM(int format) {

    int year = Integer.parseInt(SparrowUtil.formatDate(getDate(), "yyyy"));
    int month = Integer.parseInt(SparrowUtil.formatDate(getDate(), "MM"));
    int day = Integer.parseInt(SparrowUtil.formatDate(getDate(), "dd"));

    int cfgHH = SparrowUtil.getHour(procShutValue, format);
    int cfgMM = SparrowUtil.getMinute(procShutValue);

    String currHHMM = SparrowUtil.formatDate(getDate(),
                                           Constants.SPARROW_SHUTDOWN_FORMAT_HHMM);
    int curHH = SparrowUtil.getHour(currHHMM, 0);
    int curMM = SparrowUtil.getMinute(currHHMM);

    GregorianCalendar configTime = new GregorianCalendar(year, month, day,
        cfgHH, cfgMM);
    GregorianCalendar currTime = new GregorianCalendar(year, month, day, curHH,
        curMM);

    Date cfgDt = configTime.getTime();
    Date curDt = currTime.getTime();

    long l1 = cfgDt.getTime();
    long l2 = curDt.getTime();

    long difference = l2 - l1;

    return (difference >= 0);
  }

  /**
   * getName
   *
   * @return String
   */
  public String getName() {
    return "ShutdownTimeCheckDependant";
  }

  /**
   *
   * @return Date
   */
  private Date getDate() {

    Date date = null;
    today = SparrowUtil.formatDate(new Date(), "EEE");

    if (restartedAftrShutdown) {

      if (today.equals(tomorrow)) {
        restartedAftrShutdown = false;
        date = new Date();
      }
      else {
        date = getNextDay();
      }
    }
    else {
      date = new Date();
    }

    return date;
  }

  /**
   * beginCycle
   */
  public void beginCycle() {
    endCycle = false;
  }

  /**
   * endCycle
   */
  public void endCycle() {
    endCycle = true;
  }

  /**
   * isProcessTerminationRequired
   *
   * @return boolean
   */
  public boolean isProcessTerminationRequired() {
    if (result) {
      logger.warn(getStatusDescription());
    }

    return result;
  }

  /**
   *
   * @return Date
   */
  private Date getNextDay() {
    Calendar c = Calendar.getInstance();
    c.setTime(new Date());
    c.add(Calendar.DATE, 1);
    c.set(Calendar.HOUR, 00);
    c.set(Calendar.MINUTE, 01);
    c.set(Calendar.SECOND, 01);
    c.set(Calendar.AM_PM, Calendar.AM);

    return c.getTime();
  }

  /**
   * getStatusDescription
   *
   * @return String
   */
  public String getStatusDescription() {
    return "SCHEDULED SHUTDOWN initiated. [" +
        SparrowUtil.formatDate(getDate(), "dd-EEE-yyyy HH:mm:ss") + "].[sparrow.shutdown="+procShutValue+"]";
  }

  /**
   * getPriority
   *
   * @return int
   */
  public int getPriority() {
    return Sortable.PRIORITY_ABOVE_LOW;
  }

}
