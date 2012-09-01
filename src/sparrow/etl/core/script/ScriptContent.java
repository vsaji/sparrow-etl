package sparrow.etl.core.script;

import sparrow.etl.core.config.ConfigParam;
import sparrow.etl.core.config.ConfigParamImpl;
import sparrow.etl.core.exception.InitializationException;
import sparrow.etl.core.util.ConfigKeyConstants;
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
public abstract class ScriptContent {

  private static ScriptContent instance = null;
  private static final String SOURCE_FILE = "file";
  private static final String SOURCE_INLINE = "inline";

  /**
   *
   * @param param ConfigParam
   */
  public static final ScriptContent getScriptContent(ConfigParam param) {
    if (instance == null) {
      String scriptType = SparrowUtil.performTernary(param,
          ConfigKeyConstants.PARAM_SCRIPT_INPUT, SOURCE_INLINE);
      instance = (SOURCE_FILE.equals(scriptType)) ?
          (ScriptContent)new FileScriptContent(param) :
          new InlineScriptContent(param);
    }
    return instance;
  }

  abstract String getType();

  public abstract String getContent();

  /**
   *
   * <p>Title: </p>
   * <p>Description: </p>
   * <p>Copyright: Copyright (c) 2004</p>
   * <p>Company: </p>
   * @author Saji Venugopalan
   * @version 1.0
   */
  static class FileScriptContent
      extends ScriptContent {

    private final String script;

    /**
     *
     * @param param ConfigParam
     */
    FileScriptContent(ConfigParam param) {
      script = resolve(param);
    }

    /**
     *
     * @param param ConfigParam
     * @return String
     */
    private String resolve(ConfigParam param) {

      String fileInfo = param.getParameterValue(ConfigKeyConstants.
                                                PARAM_SCRIPT_VALUE);
      String content = null;
      try {
        content = SparrowUtil.readTextFile(fileInfo);
        content = ConfigParamImpl.scanVars(content, false, null);
      }
      catch (Exception ex) {
        throw new InitializationException(
            "Exception while initializing FileTokenSource [" +
            fileInfo + "]", ex);
      }
      return content;
    }

    /**
     * getType
     *
     * @return String
     */
    String getType() {
      return "file";
    }

    /**
     * getContent
     *
     * @return String
     */
    public String getContent() {
      return script;
    }

  }

  /**
   *
   * <p>Title: </p>
   * <p>Description: </p>
   * <p>Copyright: Copyright (c) 2004</p>
   * <p>Company: </p>
   * @author Saji Venugopalan
   * @version 1.0
   */
  static final class InlineScriptContent
      extends ScriptContent {

    final String script;

    InlineScriptContent(ConfigParam param) {
      this.script = param.getParameterValue(ConfigKeyConstants.
                                            PARAM_SCRIPT_VALUE);
    }

    /**
     * getType
     *
     * @return String
     */
    String getType() {
      return "inline";
    }

    /**
     * getContent
     *
     * @return String
     */
    public String getContent() {
      return script;
    }

  }
}
