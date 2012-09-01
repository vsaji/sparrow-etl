package sparrow.etl.core.lookup;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import java.util.HashMap;
import java.util.Stack;
import java.util.Arrays;

import sparrow.etl.core.config.DependentIndexingSupport;
import sparrow.etl.core.config.LookUpConfig;
import sparrow.etl.core.exception.DependancyCheckException;
import sparrow.etl.core.exception.InitializationException;
import sparrow.etl.core.util.Constants;
import sparrow.etl.core.util.DependentSequenzer;

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */
public class LookupUtils
    extends DependentSequenzer {

  protected LookupUtils() {
    super();
  }

  /**
   *
   * @param items List
   * @return List
   */
  public static final List sequenceDependentForAutoLookup(List items) {

    Map itemsInMap = getItemsInMap(items);
    List itemStore = new ArrayList();

    for (Iterator iter = items.iterator(); iter.hasNext(); ) {

      LookUpConfig item = (LookUpConfig) iter.next();

      if (Constants.LOAD_TYPE_LAZY.equals(item.getLoadType())) {
        continue;
      }

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
   * @param items List
   * @return Map
   */
  public static final Map getLazyLookupInMap(List items) {
    Map tempMap = new HashMap();
    for (Iterator iter = items.iterator(); iter.hasNext(); ) {
      LookUpConfig item = (LookUpConfig) iter.next();
      if (Constants.LOAD_TYPE_LAZY.equals(item.getLoadType())) {
        tempMap.put(item.getName(), item);
      }
    }
    return tempMap;
  }

  /**
   *
   * @param items List
   * @return Map
   */
  public static final Map getLookupDependStacks(List items) {
    Map itemsInMap = getItemsInMap(items);
    Map lookupDepInStack = new HashMap(items.size());

    for (Iterator iter = items.iterator(); iter.hasNext(); ) {

      LookUpConfig item = (LookUpConfig) iter.next();

      if (!checkDependency(item)) {
        Stack stk = new Stack();
        stk.push(item.getName());
        lookupDepInStack.put(item.getName(), stk);
      }
      else {

        try {
          Stack stk = new Stack();
          lookupDepInStack.put(item.getName(), stk);
          checkDependencyAndSequence( ( (DependentIndexingSupport) itemsInMap.
                                       get(
              item.
              getDepends())), item, itemsInMap, stk);
        }
        catch (DependancyCheckException ex) {
          throw new InitializationException(ex);
        }
      }
    }
    return lookupDepInStack;
  }

  /**
   *
   * @param items List
   * @return Map
   */
  public static final Map getLookUpDependencies(List items) {
    Map lookupDepInStack = new HashMap(items.size());
    for (Iterator iter = items.iterator(); iter.hasNext(); ) {

      DependentIndexingSupport item = (DependentIndexingSupport) iter.next();

      if (!checkDependency(item)) {
        lookupDepInStack.put(item.getName(), new ArrayList(0));
      }
      else {
        lookupDepInStack.put(item.getName(),
                             Arrays.asList(item.getDepends().split("[,]")));
      }
    }
    return lookupDepInStack;
  }
}

