package sparrow.etl.core.util;

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */
public class CounterObject {

  private volatile int counter;
  private final int initialCount;
  private final int max;

  /**
   * 
   */
  public CounterObject() {
	  this(0,-1);
  }

  
  /**
   * 
   * @param initialCount
   */
  public CounterObject(int initialCount) {
	  this(initialCount,-1);
  }

  /**
   * 
   * @param initialCount
   * @param max
   */
  public CounterObject(int initialCount,int max) {
	  	this.initialCount = initialCount;
	    this.max = max;
	    this.counter = initialCount;
  }
   
   
  public int increment() {
    return ++counter;
  }

  public int getCount() {
    return counter;
  }

  public synchronized void reset() {
    counter = initialCount;
  }

  /**
   * 
   * @return
   */
  public int checkAndIncrement(){
	  int cntr = ( (counter + 1) < max) ? ++counter : (counter=initialCount);
	  return (cntr>=max) ? (max-1) :  cntr;
  }
  
}
