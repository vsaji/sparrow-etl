package sparrow.elt.impl.transformer.template;

import java.util.Map;

import sparrow.elt.core.exception.EvaluatorException;
import sparrow.elt.core.exception.ParserException;


/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */
public interface TemplateProcessor {

  public void parse(String expression) throws ParserException;
  public String render(Map values) throws EvaluatorException;

}
