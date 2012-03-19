package sparrow.elt.core.fifo;

import sparrow.elt.core.exception.FIFOException;
import sparrow.elt.core.log.SparrowLogger;
import sparrow.elt.core.log.SparrowrLoggerFactory;

public class FIFO {

  public static final Object BEGIN_CYCLE_MESSAGE = new Object();
  public static final Object END_CYCLE_MESSAGE = new Object();

  protected String m_name;
  protected Object[] m_objArr = null;
  protected int m_readPtr = 0, m_writePtr = 0;
  protected int m_queueStatus = 1; // Queue is not in cycle = 1, Queue is in midst of a cycle = 0
  protected int initQSize;

  public FIFO() {
    m_name = "FIFO" + hashCode();
    initQSize = 100;
  }

  public FIFO(String pName) {
    m_name = pName;
    initQSize = 100;
  }

  public FIFO(String pName, int size) {
    m_name = pName;
    m_objArr = new Object[size];
    initQSize = size;
  }

  public void setQueueSize(int size) {
    if (m_objArr != null) {
      throw new FIFOException("Queue already exists for " + m_name);
    }
    m_objArr = new Object[size];
   initQSize = size;
  }

  public String getName() {
    return (m_name);
  }

  // Note . All the following functions need to be called in synchronized fashion. (methods calling them should be syncing on the object monitor)
  protected int incrPtr(int ptr1) {
    ptr1++;
    if (ptr1 >= m_objArr.length) {
      ptr1 = 0;
    }
    return (ptr1);
  }

  /**
   *
   * @return int
   */
  public int getDepth() {
    int size = ( (initQSize + (m_writePtr - m_readPtr)) % initQSize);
    return size;
  }

  public boolean isQueueEmpty() {
    return (m_readPtr == m_writePtr);
  }

  public int getQueueDepth() {
    return m_writePtr;
  }

  protected boolean isQueueFull() {
    //   m_logger.log("incrPtr(m_writePtr):"+ incrPtr(m_writePtr)+ "=" +m_readPtr+":m_readPtr => "+ getName(),  m_logger.INFO);
    return (incrPtr(m_writePtr) == m_readPtr);
  }

  protected void setObjItem(Object pObj) {
    m_objArr[m_writePtr] = pObj;
  }

  protected Object getObjItem() {
    return (m_objArr[m_readPtr]);
  }

  protected boolean isInCycle() {
    return (m_queueStatus == 0);
  }

  /**
   * This method is the setter for the Producer/Consumer pattern. This will set the
   * resource to the string passed as param. The method will wait if the internal
   * queue is full. If it manages to set the resource
   * it will notify all objects waiting in this (basically wake up consumers if any
   * waiting).   There can be many producers trying to put data into the FIFO
   * @param pObj
   */
  public synchronized void produce(Object pObj) {

    if (m_objArr == null) {
      throw new FIFOException("Queue storeage does not exist for : " + m_name);
    }

    while (isQueueFull()) {

      //     m_logger.log("Queue is full in FIFO '" + getName() +
//                   "'. Going to sleep.", m_logger.INFO);
      try {
        //Thread.sleep(100);
        wait();
      }
      catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    setObjItem(pObj);
    m_writePtr = incrPtr(m_writePtr);

    /**    if (pObj == BEGIN_CYCLE_MESSAGE) {
          if (m_queueStatus == 0) {
            throw new CycleException("FIFO already within a cycle " + m_name);
          }
          ;
          m_queueStatus = 0;
        }
     **/
    notifyAll();
  }

  /**
   * This method is the getter for the Producer/Consumer pattern (Consumer Side) This will get the
   * resource to the string passed as param. The method will wait if the product is
   * not yet produced till its available. If it manages to get the product to process,
   * it will notify all objects waiting in this (basically wake up the producers
   * if any waiting).  There can be many consumers trying to get data to be processed from the FIFO.
   *
   */
  public synchronized Object consume() {

    if (m_objArr == null) {
      throw new FIFOException("Queue storeage does not exist for : " + m_name);
    }

    while (isQueueEmpty()) {
      try {
        wait();
      }
      catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

    Object retObj = getObjItem();

    m_readPtr = incrPtr(m_readPtr);

    /**if (retObj == END_CYCLE_MESSAGE) {
      if (m_queueStatus == 1) {
        throw new CycleException("FIFO already out of a cycle " + m_name);
      }
      ;
      m_queueStatus = 0;
      if (m_fifoMon != null) {
        m_fifoMon.endCycle();
      }
         }**/

    notifyAll();
    return (retObj);
  }

  /**
   * This method is the getter for the Producer/Consumer pattern (Consumer Side) This will get the
   * resource to the string passed as param. The method will wait if the product is
   * not yet produced till its available. If it manages to get the product to process,
   * it will notify all objects waiting in this (basically wake up the producers
   * if any waiting).  There can be many consumers trying to get data to be processed from the FIFO.
   *
   */
  public synchronized Object getNextObject() {

    if (m_objArr == null) {
      throw new FIFOException("Queue storeage does not exist for : " + m_name);
    }

    while (isQueueEmpty()) {
      try {
        wait();
      }
      catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

    Object retObj = getObjItem();

    notifyAll();
    return (retObj);
  }

  /* Test the FIFO
    public static boolean testFIFO1() {

    int testNum = 1;
    System.out.println("Test " + testNum + " started.");
    FIFO fifo1 = new FIFO("TestFifo1", 10);
    fifo1.produce(FIFO.BEGIN_CYCLE_MESSAGE);

    try {
      fifo1.produce(FIFO.BEGIN_CYCLE_MESSAGE);
      System.err.println("Test Unsuccessful. FIFO '" + fifo1.getObjItem() +
                         "' accepted two consecutive Begin Cycle Message ");
    }
    catch (CycleException ex1) {
      System.out.println("Test Successful. FIFO '" + fifo1.getObjItem() +
                         "' rejected two consecutive Begin Cycle Message");
    }

    System.out.println("Test " + testNum++ +" finished.");
    System.out.println("Test " + testNum + " started.");
    fifo1 = new FIFO("TestFifo2", 10);
    fifo1.produce(FIFO.BEGIN_CYCLE_MESSAGE);
    fifo1.produce(FIFO.END_CYCLE_MESSAGE);

    try {
      fifo1.produce(FIFO.END_CYCLE_MESSAGE);
      System.err.println("Test Unsuccessful. FIFO '" + fifo1.getObjItem() +
                         "' accepted two consecutive End Cycle Message ");
    }
    catch (CycleException ex1) {
      System.out.println("Test Successful. FIFO '" + fifo1.getObjItem() +
                         "' rejected two consecutive End Cycle Message ");
    }



    System.out.println("Test " + testNum++ +" finished.");
    System.out.println("Test " + testNum + " started.");

    fifo1 = new FIFO("TestFifo3", 10);


    Runnable thr1 = new Runnable() {
      public void run() {
        FIFO fifo1 = new FIFO("TestFifo4", 10);
        Object obj1 = new Object();
        for (int i = 0; i < 10; i++) {
          fifo1.produce(obj1);
        }
        fifo1.produce(obj1);
        // Execution should not reach this point since the queue become full. This effectively should become a dead thread.
        System.err.println("Test unsuccessful. FIFO " + fifo1.getName() +
                           " returned even when the queue was full");
      }
    };

    System.out.println("Test " + testNum++ +" finished.");
    System.out.println("Test " + testNum + " started.");
    new Thread(thr1).start();

    // Don;t try to join thr1. It will never join.
    fifo1 = new FIFO("TestFifo5", 10);


    Runnable run1 = new Runnable() {

      FIFO myFif1 = fifo1;

      public void run() {
        // Object obj1 = new Object();
        for (int i = 0; i < 10; i++) {
          myFif1.produce(new String(i));
        }
      }
    };
    Runnable run2 = new Runnable() {
      FIFO myFif1 = fifo1;
      public void run() {
        for (int i = 0; i < 10; i++) {
          String str1 = (String) myFif1.consume();
          if (str1 != new String(i)) {
            System.err.println("Test unsuccessful. Was expecting '" + i +
                               "' but instead got '" + str1 + "' for queue " +
                               myFif1.getName());
          }
        }
      }
    };
    new Thread(run1).start();
    new Thread(run2).start();

    System.out.println("Test " + testNum++ +" finished.");
    System.out.println("Test " + testNum + " started.");
    fifo1 = new FIFO("TestFifo6", 10);
    run1 = new Runnable() {
      FIFO myFif1 = fifo1;
      public void run() {
        for (int i = 0; i < 20; i++) {
          myFif1.produce(new String(i));
        }
      }
    };
    run2 = new Runnable() {
      FIFO myFif1 = fifo1;
      public void run() {
        for (int i = 0; i < 20; i++) {
          String str1 = (String) myFif1.consume();
          if (str1 != new String(i)) {
            System.err.println("Test unsuccessful. Was expecting '" + i +
                               "' but instead got '" + str1 + "' for queue '" +
                               myFif1.getName() + "'");
          }
        }
      }
    };
    new Thread(run1).start();
    new Thread(run2).start();
    System.out.println("Test " + testNum++ +" finished.");
    System.out.println("Test " + testNum + " started.");
    fifo1 = new FIFO("TestFifo7", 10);
    run1 = new Runnable() {
      FIFO myFif1 = fifo1;
      public void run() {
        fifo1.produce(FIFO.BEGIN_CYCLE_MESSAGE);
        for (int i = 0; i < 25; i++) {
          myFif1.produce(new String(i));
        }
        fifo1.produce(FIFO.END_CYCLE_MESSAGE);
      }
    };
    run2 = new Runnable() {
      FIFO myFif1 = fifo1;
      Object obj1;
      public void run() {
        obj1 = fifo1.consume();
        if (obj1 != FIFO.BEGIN_CYCLE_MESSAGE) {
          System.err.println(
   "Test Unsuccessful. Could not see begin cycle message on the queue.");
        }
        for (int i = 0; i < 25; i++) {
          String str1 = (String) myFif1.consume();
          if (str1 != new String(i)) {
            System.err.println("Test unsuccessful. Was expecting '" + i +
                               "' but instead got '" + str1 + "'");
          }
        }
        if (obj1 != FIFO.BEGIN_CYCLE_MESSAGE) {
          System.err.println(
   "Test Unsuccessful. Could not see end cycle message on the queue '" +
              myFif1.getName() + "'");
        }
      }
    };
    Thread thr01 = new Thread(run1);
    Thread thr02 = new Thread(run2);
    thr01.start();
    thr01.start();
    thr01.join();
    thr02.join();
    System.out.println("Test " + testNum++ +" finished.");
    System.out.println("Test " + testNum + " started.");
    }
   */
}
