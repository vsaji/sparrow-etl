package sparrow.etl.impl.transformer.template;

import java.util.Map;

import sparrow.etl.core.exception.InitializationException;
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
public class SparrowTemplateFactory {

  /**
   *
   */
  private static final Map CLASS_REPOSITORY = SparrowUtil.getImplConfig(
      "template");

  /**
   *
   */
  private SparrowTemplateFactory() {
  }

  /**
   *
   * @param name String
   * @return SparrowTemplate
   */
  public static final TemplateProcessor createTemplateProcessor(String name){

    String templateProcessorClass = (String) CLASS_REPOSITORY.get(
        name);
    if (templateProcessorClass == null) {
      throw new InitializationException("template.processor [" +
                                        name + "] is not found");
    }
    return (TemplateProcessor) SparrowUtil.createObject(templateProcessorClass);
  }

}
