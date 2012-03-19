package sparrow.elt.core.report;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */
public class RejectedEntry {

  public static final String REPORT_SOURCE_NAME = "report.source";
  public static final String REJECTED_ENTRY = "rejected.entry";
  public static final String PRIMARY_VALUE = "primary.value";
  public static final String REJECT_REASON = "rejected.reason";
  public static final String REJECT_DATE = "rejected.date";
  private static final SimpleDateFormat dateFormat = new SimpleDateFormat(
      "dd-MM-yyyy HH:mm:ss");

  private HashMap entry = new HashMap();

  public void setPrimaryValue(String primaryValue) {
    entry.put(PRIMARY_VALUE, primaryValue);
  }

  public void setReporterSource(String reporterSource) {
    entry.put(REPORT_SOURCE_NAME, reporterSource);
  }

  public void setRejectedEntry(String rejectedEntry) {
    entry.put(REJECTED_ENTRY, rejectedEntry);
  }

  public void setRejectReason(String rejectReason) {
    entry.put(REJECT_REASON, rejectReason);
  }

  HashMap getEntry() {
    return entry;
  }

  /**
   *
   */
  public RejectedEntry() {
    entry.put(REJECT_DATE, dateFormat.format(new Date()));
  }

  /**
   *
   * @param reporterSource String
   * @param rejectedEntry String
   * @param entryNumber int
   */
  public RejectedEntry(String reporterSource, String rejectedEntry,
                       int entryNumber) {
    entry.put(PRIMARY_VALUE, new Integer(entryNumber));
    entry.put(REPORT_SOURCE_NAME, reporterSource);
    entry.put(REJECTED_ENTRY, rejectedEntry);
    entry.put(REJECT_DATE, dateFormat.format(new Date()));
  }

  /**
   *
   */
  void close() {
    entry.clear();
    entry = null;
  }

}
