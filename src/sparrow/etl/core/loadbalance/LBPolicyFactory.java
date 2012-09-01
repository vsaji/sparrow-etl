package sparrow.etl.core.loadbalance;

import java.util.Map;

import sparrow.etl.core.config.ConfigParam;
import sparrow.etl.core.config.LoadBalancerConfig;
import sparrow.etl.core.config.SparrowConfig;
import sparrow.etl.core.context.SparrowApplicationContext;
import sparrow.etl.core.context.SparrowContext;
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
public class LBPolicyFactory {

	
	private static RequestAssignerPolicy trasformerLB;
	private static RequestAssignerPolicy writerLB;
  /**
   *
   * @param fifos Map
   * @param context SparrowApplicationContext
   * @return KeyAssigner
   */
  public static final RequestAssignerPolicy newInstance(Map fifos,
      SparrowContext context) {
    RequestAssignerPolicy assigner = null;
    LoadBalancerConfig config = ((SparrowApplicationContext) context).getConfiguration().getModule().getLoadBalancer();
    assigner = (RequestAssignerPolicy) SparrowUtil.createObject( config.getClassName(), new Class[] {Map.class,
        SparrowConfig.class}, new Object[] {fifos, new SpearLoadBalancerConfigImpl(config,context)});

    return assigner;
  }

  
  /**
   * 
   * @param fifos
   * @param context
   * @return
   */
  public static final RequestAssignerPolicy getTransFormerLBInstance(Map fifos,
	      SparrowContext context) {
	  if(trasformerLB==null){
		  return (trasformerLB=newInstance(fifos, context));
	  }
	  return trasformerLB;
  }

  
  /**
   * 
   * @param fifos
   * @param context
   * @return
   */
  public static final RequestAssignerPolicy getWriterLBInstance(Map fifos,
	      SparrowContext context) {
	  if(writerLB==null){
		  return (writerLB=newInstance(fifos, context));
	  }
	  return writerLB;
  }
  
  /**
   *
   * <p>Title: </p>
   * <p>Description: </p>
   * <p>Copyright: Copyright (c) 2004</p>
   * <p>Company: </p>
   * @author not attributable
   * @version 1.0
   */
  private static class SpearLoadBalancerConfigImpl implements SparrowConfig{


    private final LoadBalancerConfig config;
    private final SparrowContext context;
    /**
     * getContext
     *
     * @return SparrowContext
     */
    public SpearLoadBalancerConfigImpl(LoadBalancerConfig config,SparrowContext context){
      this.config = config;
      this.context = context;
    }


    public SparrowContext getContext() {
      return this.context;
    }

    /**
     * getInitParameter
     *
     * @return ConfigParam
     */
    public ConfigParam getInitParameter() {
      return config;
    }

    /**
     * getName
     *
     * @return String
     */
    public String getName() {
      return config.getPolicy() +"=>"+config.getClassName();
    }
  }


}
