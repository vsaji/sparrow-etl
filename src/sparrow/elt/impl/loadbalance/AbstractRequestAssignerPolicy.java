package sparrow.elt.impl.loadbalance;

import java.util.Arrays;
import java.util.Map;

import sparrow.elt.core.config.SparrowConfig;
import sparrow.elt.core.dao.impl.ResultRow;
import sparrow.elt.core.fifo.FIFO;
import sparrow.elt.core.loadbalance.RequestAssignerPolicy;
import sparrow.elt.core.util.CounterObject;
import sparrow.elt.core.vo.SparrowResultHolder;


/**
 * 
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author not attributable
 * @version 1.0
 */
public abstract class AbstractRequestAssignerPolicy implements
		RequestAssignerPolicy {

	protected final FIFO[] fifoQueues;
	protected final int QSIZE;
	protected final CounterObject co;

	protected volatile int qPointer = 0;
	
	/**
	 * 
	 * @param fifos
	 *            Map
	 * @param config
	 *            SparrowConfig
	 */
	protected AbstractRequestAssignerPolicy(Map fifos, SparrowConfig config) {

		Object[] QNAMES = fifos.keySet().toArray();
		Arrays.sort(QNAMES);
		QSIZE = QNAMES.length;
		this.fifoQueues=new FIFO[QSIZE];

		for(int i=0;i<QSIZE;i++){
			fifoQueues[i]=(FIFO)fifos.get(QNAMES[i]);
		}
		this.co = new CounterObject(0, QSIZE);
	}

	/**
	 * 
	 */
	public abstract void assign(ResultRow rr);


	/**
	 * 
	 */
	public void assign(SparrowResultHolder srh) {
		assign(srh,0);
	}

	/**
	 * 	
	 */
	public void assign(Object obj, int temp) {
      FIFO fifo = fifoQueues[co.checkAndIncrement()];
      fifo.produce(obj);
      //qPointer = ( (qPointer + 1) < QSIZE) ? ++qPointer : 0;		
	}
	
	
	
}
