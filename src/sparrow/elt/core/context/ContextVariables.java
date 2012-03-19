package sparrow.elt.core.context;

import java.util.List;
import java.util.ArrayList;

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */
public interface ContextVariables {
  public static final String FETCH_COUNT = "@FETCH_COUNT";
  public static final String REJECT_COUNT = "@REJECT_COUNT";
  public static final String EXCEPTION_COUNT = "@EXCEPTION_COUNT";
  public static final String CURRENT_DATE = "@CURRENT_DATE";
  public static final String NEXT_DATE = "@NEXT_DATE";
  public static final String PREVIOUS_DATE = "@PREVIOUS_DATE";
  public static final String RECORD_COUNT = "@RECORD_COUNT";
  public static final String COLUMN_NAMES = "@COLUMN_NAMES";
  public static final String FAIL_PROCESS = "@FAIL_PROCESS";
  public static final String REJECT_REC_FILE = "@REJECT_REC_FILE";  
  public static final String CSV_ROW_REJECT_FILE = "@CSV_ROW_REJECT_FILE";  
  public static final String SPEAR_PID = "@SPEAR_PID";

  public static final List SPEAR_CONTEXT_VARS = new ArrayList(){
    {
      add(FETCH_COUNT);
      add(REJECT_COUNT);
      add(EXCEPTION_COUNT);
      add(CURRENT_DATE);
      add(NEXT_DATE);
      add(PREVIOUS_DATE);
      add(RECORD_COUNT);
      add(COLUMN_NAMES);
      add(FAIL_PROCESS);
    }
  };
}
