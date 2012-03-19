package sparrow.elt.core.initializer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import sparrow.elt.core.config.AsserterConfig;
import sparrow.elt.core.config.ConfigParam;
import sparrow.elt.core.context.SparrowApplicationContext;
import sparrow.elt.core.cycledependency.CycleDependencyChecker;
import sparrow.elt.core.cycledependency.CycleEventListener;
import sparrow.elt.core.exception.InitializationException;
import sparrow.elt.core.exception.ObjectCreationException;
import sparrow.elt.core.log.SparrowLogger;
import sparrow.elt.core.log.SparrowrLoggerFactory;
import sparrow.elt.core.monitor.CycleMonitor;
import sparrow.elt.core.util.SparrowUtil;


/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */
public class CycleDependencyInitializer {

  /**
   *
   */
  private static final SparrowLogger logger = SparrowrLoggerFactory.
      getCurrentInstance(
      CycleDependencyInitializer.class);

  private ArrayList SPEAR_SPECIFIC_CLASSES = new ArrayList() {
    {
      Map impls = SparrowUtil.getImplConfig("cycledependency");
      for (Iterator it = impls.values().iterator(); it.hasNext(); ) {
        add(it.next());
      }
    }
  };

  /**
   *
   * @param context SparrowApplicationContextImpl
   */
  CycleDependencyInitializer() {
  }

  /**
   *
   * @throws InitializationException
   */
  void initialize(SparrowApplicationContext context, CycleMonitor cycleObervable) {

    List dependents = context.getConfiguration().getModule().
        getCycleDependencyAsserters();
    String currentClass = null;

    try {
      initializeSpearSpecificDependables(context, cycleObervable);

      for (Iterator iter = dependents.iterator(); iter.hasNext(); ) {
        AsserterConfig item = (AsserterConfig) iter.next();
        currentClass = item.getClassName();

        if (SPEAR_SPECIFIC_CLASSES.contains(currentClass)) {
          continue;
        }

        CycleEventListener preDependant = (CycleEventListener) SparrowUtil.createObject(
            currentClass, new Class[] {ConfigParam.class,
            SparrowApplicationContext.class}
            ,
            new Object[] {item, context});
        cycleObervable.addObserver(preDependant);
        CycleDependencyChecker.registerPreDependant(preDependant);
        logger.debug("Cycle pre-dependency [" + currentClass +
                     "] has been registered");

      }
      SPEAR_SPECIFIC_CLASSES.clear();
      SPEAR_SPECIFIC_CLASSES = null;

    }
    catch (ObjectCreationException ex) {
      throw ex;
    }
    catch (Exception ex) {
      throw new InitializationException(
          "Exception occured while initializing :" + currentClass, ex);
    }
  }

  /**
   *
   */
  private void initializeSpearSpecificDependables(SparrowApplicationContext
                                                  context,
                                                  CycleMonitor cycleObervable) {
    Map impls = SparrowUtil.getImplConfig("cycledependency");

    for (Iterator iter = impls.values().iterator(); iter.hasNext(); ) {
      String currentClass = (String) iter.next();
      CycleEventListener preDependant = (CycleEventListener) SparrowUtil.createObject(
          currentClass, new Class[] {ConfigParam.class,
          SparrowApplicationContext.class}
          , new Object[] {null, context});
      cycleObervable.addObserver(preDependant);
      CycleDependencyChecker.registerPreDependant(preDependant);
      logger.debug("Spear specific cycle pre-dependency [" + currentClass +
                   "] has been registered");
    }
  }

}
