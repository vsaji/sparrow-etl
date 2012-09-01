package sparrow.etl.core.exception;

import java.util.List;

import sparrow.etl.core.config.Handle;
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
public class ErrorConfig {

  private final List errCode;
  private final List errDesc;
  private final List errType;

  /**
   *
   * @param handle Handle
   */
  public ErrorConfig(Handle handle) {
    errCode = handle.getCodeList();
    errDesc = handle.getDescList();
    errType = handle.getTypeList();
  }

  /**
   *
   * @param errorCode String
   * @return String
   */
  public String getErrorTypeByCode(String errorCode) {

	for (int i =0; i<errCode.size(); i++) {
		String element = (String) errCode.get(i);
		if(SparrowUtil.matchString(element,errorCode)){
			return (String)errType.get(i);
		}
	}
	return "";
	  
//    int index = errCode.indexOf("*");
//    index = (index==-1) ? errCode.indexOf(errorCode) : index;
//    String errorType = (index != -1) ? (String) errType.get(index) : null;
//    return (errorType == null) ? "" : errorType;
  }

  /**
   *
   * @param description String
   * @return String
   */
  public String getErrorTypeByDescription(String description) {
		for (int i =0; i<errDesc.size(); i++) {
			String element = (String) errDesc.get(i);
			if(SparrowUtil.matchString(element,description)){
				return (String)errType.get(i);
			}
		}
		return "";

//    int index = errDesc.indexOf("*");
//    boolean found = false;
//
//    if (index==-1){
//      index=0;
//      for (Iterator it = errDesc.iterator(); it.hasNext(); index++) {
//        String configDesc = (String) it.next();
//        if (!configDesc.trim().equals("")) {
//          int indx = description.indexOf(configDesc);
//          if (indx != -1) {
//            found = true;
//            break;
//          }
//        }
//      }
//    }
//    else{
//      found = true;
//    }
//
//    String errorType = (found) ? (String) errType.get(index) : null;
//    return (errorType == null) ? "" : errorType;
  }

  /**
   *
   * @param errorCode String
   * @return String
   */
  public String getErrorDescription(String errorCode) {
    int index = errCode.indexOf(errorCode);
    return (index != -1) ? (String) errDesc.get(index) : null;
  }

  /**
   *
   * @param description String
   * @return String
   */
  public String getErrorCode(String description) {
    int index = errDesc.indexOf(description);
    return (index != -1) ? (String) errCode.get(index) : null;
  }

}
