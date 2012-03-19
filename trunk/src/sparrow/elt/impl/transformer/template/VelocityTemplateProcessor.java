package sparrow.elt.impl.transformer.template;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.runtime.RuntimeInstance;
import org.apache.velocity.runtime.parser.ParseException;
import org.apache.velocity.runtime.parser.node.SimpleNode;

import sparrow.elt.core.exception.EvaluatorException;
import sparrow.elt.core.exception.ParserException;
import sparrow.elt.core.log.SparrowLogger;
import sparrow.elt.core.log.SparrowrLoggerFactory;
import sparrow.elt.core.util.SparrowUtil;

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
      nodeTree = ri.parse(expression, "spear");
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
    VelocityContext ctxt = new SpearVelocityContext(values);
    StringWriter sw = new StringWriter();
    try {
      ri.render(ctxt, sw, "spear", nodeTree);
    }
    catch (IOException ex) {
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
  private static class SpearVelocityContext
      extends VelocityContext {

    /**
     *
     */
    public SpearVelocityContext() {
    }

    /**
     *
     * @param context Map
     */
    public SpearVelocityContext(Map context) {
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
