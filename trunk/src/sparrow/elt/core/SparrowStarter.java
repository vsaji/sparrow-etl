package sparrow.elt.core;

import sparrow.elt.core.util.Constants;

public class SparrowStarter {

  public SparrowStarter() {
  }

  public static void main(String[] args) {
    Thread.currentThread().setName("starter");
    SparrowCoreEngine coreEngine = new SparrowCoreEngine();
    try {
      coreEngine.initialize();
      Thread coreThread = new Thread(coreEngine, Constants.CORE_THREAD_NAME);
      coreThread.start();
    }
    catch (Throwable t) {
      t.printStackTrace();
      System.exit(Constants.ERROR_EXIT);
    }
    finally {
    }
  }

}
