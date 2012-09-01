package sparrow.etl.core.config;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import sparrow.etl.jaxb.ERRORType;
import sparrow.etl.jaxb.HANDLEType;


/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */
public interface Handle {

  abstract String getExceptionClass();

  abstract String getHandlerName();

  abstract List getCodeList();

  abstract List getDescList();

  abstract List getTypeList();

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
class HandleImpl
    implements Handle {

  private String exceptionClass = null;
  private String handlerName = null;
  private List codeList = null;
  private List descList = null;
  private List typeList = null;

  /**
   *
   * @param handle HANDLEType
   */
  HandleImpl(HANDLEType handle) {
    exceptionClass = handle.getEXCEPTION();
    handlerName = handle.getHANDLER();
    bind(handle.getERROR());
  }

  /**
   * getExceptionClass
   *
   * @return String
   */
  public String getExceptionClass() {
    return exceptionClass;
  }

  /**
   * getHandlerName
   *
   * @return String
   */
  public String getHandlerName() {
    return handlerName;
  }

  public List getTypeList() {
    return typeList;
  }

  public List getDescList() {
    return descList;
  }

  public List getCodeList() {
    return codeList;
  }

  /**
   *
   * @param errors List
   */
  private void bind(List errors) {

    if (!errors.isEmpty()) {
      codeList = new ArrayList(errors.size());
      descList = new ArrayList(errors.size());
      typeList = new ArrayList(errors.size());

      for (Iterator iter = errors.iterator(); iter.hasNext(); ) {
        ERRORType item = (ERRORType) iter.next();
        addCode(item.getCODE());
        addDesc(item.getVALUE());
        addType(item.getTYPE());
      }
    }
  }

  /**
   *
   * @param error Error
   */
  private void addCode(String code) {
    codeList.add(code);
  }

  /**
   *
   * @param error Error
   */
  private void addDesc(String desc) {
    descList.add(desc);
  }

  /**
   *
   * @param error Error
   */
  private void addType(String type) {
    typeList.add(type);
  }

}
