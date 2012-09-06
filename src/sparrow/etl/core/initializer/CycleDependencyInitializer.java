package sparrow.etl.core.initializer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import sparrow.etl.core.config.AsserterConfig;
import sparrow.etl.core.config.ConfigParam;
import sparrow.etl.core.context.SparrowApplicationContext;
import sparrow.etl.core.cycledependency.CycleDependencyChecker;
import sparrow.etl.core.cycledependency.CycleEventListener;
import sparrow.etl.core.exception.InitializationException;
import sparrow.etl.core.exception.ObjectCreationException;
import sparrow.etl.core.log.SparrowLogger;
import sparrow.etl.core.log.SparrowrLoggerFactory;
import sparrow.etl.core.monitor.CycleMonitor;
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
public class CycleDependencyInitializer {

  /**
   *
   */
  private static final SparrowLogger logger = SparrowrLoggerFactory.
      getCurrentInstance(
      CycleDependencyInitializer.class);

  private ArrayList SPARROW_SPECIFIC_CLASSES = new ArrayList() {
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
      initializeSparrowSpecificDependables(context, cycleObervable);

      for (Iterator iter = dependents.iterator(); iter.hasNext(); ) {
        AsserterConfig item = (AsserterConfig) iter.next();
        currentClass = item.getClassName();

        if (SPARROW_SPECIFIC_CLASSES.contains(currentClass)) {
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
      SPARROW_SPECIFIC_CLASSES.clear();
      SPARROW_SPECIFIC_CLASSES = null;

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
  private void initializeSparrowSpecificDependables(SparrowApplicationContext
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
      logger.debug("Sparrow specific cycle pre-dependency [" + currentClass +
                   "] has been registered");
    }
  }

}
