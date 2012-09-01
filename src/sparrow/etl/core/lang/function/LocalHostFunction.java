package sparrow.etl.core.lang.function;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;

import sparrow.etl.core.exception.ParserException;
import sparrow.etl.core.log.SparrowLogger;
import sparrow.etl.core.log.SparrowrLoggerFactory;


/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */
public class LocalHostFunction
    extends AbstractFunction {

  private static final String HOST_NAME = "NAME";
  private static final String HOST_IP = "IP";

  protected static final SparrowLogger logger = SparrowrLoggerFactory.
      getCurrentInstance(
      LocalHostFunction.class);


  /**
   * @param functionName
   */
  public LocalHostFunction(String funcName) {
    super(funcName, 1);
  }

  /* (non-Javadoc)
   * @see sparrow.elt.core.lang.function.Expression#getValue(java.util.Map)
   */
  public String getValue(Map values) {
    String string = arguments[0].getValue(values);
    try {
      if (HOST_NAME.equals(string)) {
        return InetAddress.getLocalHost().getHostName();
      }
      else if (HOST_IP.equals(string)) {
        return InetAddress.getLocalHost().getHostAddress();
      }else{
        logger.error("Unknown Argument ["+string+"] for ["+getFunctionName()+"]");
      }
    }
    catch (UnknownHostException ex) {
      ex.printStackTrace();
      return null;
    }
    return null;
  }

  /* (non-Javadoc)
   * @see sparrow.elt.core.lang.function.AbstractFunction#resolveArguments()
   */
  void resolveArgumentType(List args) throws ParserException {
    arguments = new Expression[1];
    String arg1 = args.get(0).toString();
    arguments[0] = evaluateStringArgument(arg1);
  }

}
