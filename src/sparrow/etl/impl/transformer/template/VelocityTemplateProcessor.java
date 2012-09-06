package sparrow.etl.impl.transformer.template;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.runtime.RuntimeInstance;
import org.apache.velocity.runtime.parser.ParseException;
import org.apache.velocity.runtime.parser.node.SimpleNode;

import sparrow.etl.core.exception.EvaluatorException;
import sparrow.etl.core.exception.ParserException;
import sparrow.etl.core.log.SparrowLogger;
import sparrow.etl.core.log.SparrowrLoggerFactory;
import sparrow.etl.core.util.SparrowUtil;

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public class VelocityTemplateProcessor
    implements TemplateProcessor {

  private static SimpleNode nodeTree;
  private static RuntimeInstance ri = new RuntimeInstance();
  private static final SparrowLogger logger = SparrowrLoggerFactory.
      getCurrentInstance(
      VelocityTemplateProcessor.class);

  public VelocityTemplateProcessor() {
  }

  /**
   * parse
   *
   * @param expression String
   */
  public void parse(String expression) throws ParserException {
    try {
      nodeTree = ri.parse(expression, "sparrow");
    }
    catch (ParseException ex) {
      throw new ParserException(
          "Velocity ParserException occured while parsing the expression [" +
          expression + "]", ex);
    }
  }

  /**
   * render
   *
   * @param ds DataSet
   * @param ctx SparrowContext
   * @return String
   */
  public String render(Map values) throws EvaluatorException {
    VelocityContext ctxt = new SparrowVelocityContext(values);
    StringWriter sw = new StringWriter();
    try {
      ri.render(ctxt, sw, "sparrow", nodeTree);
    }
    catch (Exception ex) {
      throw new EvaluatorException(
          "IOException occured while rendering velocity expression", ex);
    }
    return sw.toString();
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
  private static class SparrowVelocityContext
      extends VelocityContext {

    /**
     *
     */
    public SparrowVelocityContext() {
    }

    /**
     *
     * @param context Map
     */
    public SparrowVelocityContext(Map context) {
      super(context);
    }

    /**
     *
     * @param key String
     * @return Object
     */
    public Object internalGet(String key) {
      key = (key.indexOf("-") > 0) ? SparrowUtil.replaceFirst(key, "-", "$") :
          key;
      return super.internalGet(key);
    }
  }

}
