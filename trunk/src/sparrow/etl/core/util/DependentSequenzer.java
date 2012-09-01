package sparrow.etl.core.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import sparrow.etl.core.config.DependentIndexingSupport;
import sparrow.etl.core.config.LookUpConfig;
import sparrow.etl.core.exception.DependancyCheckException;
import sparrow.etl.core.exception.InitializationException;


/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public class DependentSequenzer {

  /**
   *
   */
  protected DependentSequenzer() {

  }

  /**
   *
   * @param items List
   * @return List
   */
  public static final List sequenceDependent(List items) {

    Map itemsInMap = getItemsInMap(items);
    List itemStore = new ArrayList();

    for (Iterator iter = items.iterator(); iter.hasNext(); ) {

      DependentIndexingSupport item = (DependentIndexingSupport) iter.next();

      if (!checkDependency(item)) {
        if (!itemStore.contains(item.getName())) {
          addItem(item, itemStore);
        }
      }
      else {

        try {
          checkDependencyAndSequence( ( (DependentIndexingSupport) itemsInMap.
                                       get(
              item.
              getDepends())), item, itemsInMap, itemStore);
        }
        catch (DependancyCheckException ex) {
          throw new InitializationException(ex);
        }
      }
    }

    return itemStore;
  }


  /**
   *
   * @param depends DependentIndexingSupport
   * @return boolean
   */
  protected static boolean checkDependency(DependentIndexingSupport depends) {

    if (depends != null) {
      String depend = depends.getDepends();
      return! (depend == null || depend.trim().equals(""));
    }
    else {
      return false;
    }
  }

  /**
   *
   * @param item DependentIndexingSupport
   * @param itemStore List
   */
  protected static void addItem(DependentIndexingSupport item, List itemStore) {
    itemStore.add(item.getName());
  }

  /**
   *
   * @param depend DependentIndexingSupport
   * @param main DependentIndexingSupport
   * @param itemsInMap Map
   * @param itemStore List
   * @throws DependancyCheckException
   */
  protected static void checkDependencyAndSequence(DependentIndexingSupport
                                                 depend,
                                                 DependentIndexingSupport main,
                                                 Map itemsInMap, List itemStore) throws
      DependancyCheckException {

    if (depend == null) {
      throw new DependancyCheckException("Missing Dependency [" +
                                         main.getDepends() + "] <--<" +
                                         main.getName() + "> ");
    }

    //---------------------------------------------------------------------
     additionalCheck(depend,main);
     //---------------------------------------------------------------------

    if (!checkDependency(depend)) {
      if (!itemStore.contains(depend.getName())) {
        addItem(depend, itemStore);
      }
      if (!itemStore.contains(main.getName())) {
        addItem(main, itemStore);
      }
    }
    else {
      DependentIndexingSupport preDepend = (DependentIndexingSupport)
          itemsInMap.get(depend.
                         getDepends());
      checkDependencyAndSequence(preDepend, depend, itemsInMap, itemStore);
      if (!itemStore.contains(main.getName())) {
        addItem(main, itemStore);
      }
    }
  }


  /**
   *
   * @param lookups List
   * @return Map
   */
  public static final Map getItemsInMap(List items) {
    Map tempMap = new HashMap();
    for (Iterator iter = items.iterator(); iter.hasNext(); ) {
      DependentIndexingSupport item = (DependentIndexingSupport) iter.next();
      tempMap.put(item.getName(), item);
    }
    return tempMap;
  }


  /**
   *
   * @param depend DependentIndexingSupport
   * @param main DependentIndexingSupport
   * @param itemsInMap Map
   * @param itemStore List
   * @throws DependancyCheckException
   */
  protected static void checkDependencyAndSequence(DependentIndexingSupport
                                                 depend,
                                                 DependentIndexingSupport main,
                                                 Map itemsInMap,
                                                 Stack lookupStack) throws
      DependancyCheckException {

    if (depend == null) {
      throw new DependancyCheckException("Missing Dependency [" +
                                         main.getDepends() + "] <--<" +
                                         main.getName() + "> ");
    }
    //---------------------------------------------------------------------
    additionalCheck(depend,main);
    //---------------------------------------------------------------------

    if (!checkDependency(depend)) {
      if (!lookupStack.contains(depend.getName())) {
        lookupStack.push(depend.getName());
      }
      if (!lookupStack.contains(main.getName())) {
        lookupStack.push(main.getName());
      }
    }
    else {
      DependentIndexingSupport preDepend = (DependentIndexingSupport)
          itemsInMap.get(depend.
                         getDepends());
      checkDependencyAndSequence(preDepend, depend, itemsInMap, lookupStack);

      if (!lookupStack.contains(main.getName())) {
        lookupStack.push(main.getName());
      }

    }
  }

  /**
   *
   */
  protected static void additionalCheck(DependentIndexingSupport depend,DependentIndexingSupport main) throws
      DependancyCheckException{

    if (depend instanceof LookUpConfig) {
      LookUpConfig dpLkp = (LookUpConfig) depend;
      if (Constants.LOAD_TYPE_LAZY.equals(dpLkp.getLoadType())) {
        throw new DependancyCheckException(
            "AUTO lookup cannot refer a LAZY lookup as DEPENDENT[" +
            main.getDepends() + " - LOAD-TYPE:LAZY] <--< [" +
            main.getName() + " - LOAD-TYPE:AUTO] ");
      }
    }
  }

}
