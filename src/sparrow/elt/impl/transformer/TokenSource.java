package sparrow.elt.impl.transformer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import sparrow.elt.core.config.ConfigParam;
import sparrow.elt.core.exception.InitializationException;


/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */
public abstract class TokenSource {

  private static TokenSource instance = null;
  private static final String SOURCE_FILE = "file";
  private static final String SOURCE_BATCH_FILE = "batch.file";
  private static final String SOURCE_BATCH_STRING = "batch.string";
  private static final String SOURCE_STRING = "string";
  private static String prefixTemplateValue = null;

  public abstract List getContents();

  /**
   *
   * @param param ConfigParam
   */
  static final void initialize(ConfigParam param) {
    if (instance == null) {
      String templateType = param.getParameterValue("template.type");
      prefixTemplateValue = "template";

      if (SOURCE_FILE.equals(templateType)) {
        instance = new FileTokenSource(param);
      }
      else if (SOURCE_STRING.equals(templateType)) {
        instance = new StringTokenSource(param);
      }
      else if (SOURCE_BATCH_FILE.equals(templateType)) {
        instance = new BatchFileTokenSource(param);
      }
      else if (SOURCE_BATCH_STRING.equals(templateType)) {
        instance = new BatchStringTokenSource(param);
      }
      else{
        throw new InitializationException("Uunrecognized template.type ["+templateType+"]. Supported [string,batch.string,file,batch.file]");
      }

    }
  }

  /**
   *
   * @return TokenSource
   */
  public static final TokenSource getTokenSource() {
    return instance;
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
  static class FileTokenSource
      extends TokenSource {

    private ArrayList contents = new ArrayList();

    FileTokenSource(ConfigParam param) {
      initialise(param);
    }

    private void initialise(ConfigParam param) {

      StringBuffer sb = new StringBuffer();
      FileInputStream in = null;
      String fileInfo = param.getParameterValue(prefixTemplateValue);

      try {

        in = new FileInputStream(fileInfo);
        InputStreamReader isr = new InputStreamReader(in);
        int ch = 0;

        while ( (ch = in.read()) > -1) {
          sb.append( (char) ch);
        }
        isr.close();
      }
      catch (FileNotFoundException ex) {
        throw new InitializationException(
            "FileNotFoundException while initializing FileTokenSource [" +
            fileInfo + "]", ex);

      }
      catch (IOException ex) {
        throw new InitializationException(
            "IOException while initializing FileTokenSource [" +
            fileInfo + "]", ex);
      }
      finally {
        if (in != null) {
          try {
            in.close();
          }
          catch (IOException ex1) {
          }
        }
      }
      contents.add(new Content(prefixTemplateValue, sb.toString()));
    }

    /**
     * getContent
     *
     * @return String
     */
    public List getContents() {
      return contents;
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
  static final class StringTokenSource
      extends TokenSource {

    final String value;
    private ArrayList contents = new ArrayList();

    StringTokenSource(ConfigParam param) {
      this.value = param.getParameterValue(prefixTemplateValue);
      contents.add(new Content(prefixTemplateValue, value));
    }

    /**
     * getContent
     *
     * @return String
     */
    public List getContents() {
      return contents;
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
  static final class BatchStringTokenSource
      extends TokenSource {

    private ArrayList contents = new ArrayList();

    BatchStringTokenSource(ConfigParam param) {
      String value = null;
      int i = 1;

      while ( (value = param.getParameterValue(prefixTemplateValue + "." + i)) != null) {
        contents.add(new Content(prefixTemplateValue + "." + i, value));
        i++;
      }
    }

    /**
     * getContent
     *
     * @return String
     */
    public List getContents() {
      return contents;
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
  static final class BatchFileTokenSource
      extends TokenSource {

    private ArrayList contents = new ArrayList();

    BatchFileTokenSource(ConfigParam param) {
      initialise(param);
    }

    private void initialise(ConfigParam param) {

      StringBuffer sb = new StringBuffer();
      FileInputStream in = null;
      String fileInfo = null;
      int i = 1;

      while ( (fileInfo = param.getParameterValue(prefixTemplateValue + "." + i)) != null) {
        try {
          sb.setLength(0);
          in = new FileInputStream(fileInfo);
          InputStreamReader isr = new InputStreamReader(in);
          int ch = 0;

          while ( (ch = in.read()) > -1) {
            sb.append( (char) ch);
          }
          isr.close();
        }
        catch (FileNotFoundException ex) {
          throw new InitializationException(
              "FileNotFoundException while initializing FileTokenSource [" +
              fileInfo + "]", ex);

        }
        catch (IOException ex) {
          throw new InitializationException(
              "IOException while initializing FileTokenSource [" +
              fileInfo + "]", ex);
        }
        finally {
          if (in != null) {
            try {
              in.close();
            }
            catch (IOException ex1) {
            }
          }
        }
        contents.add(new Content(prefixTemplateValue + "." + i, sb.toString()));
        i++;
      }

    }

    /**
     * getContent
     *
     * @return String
     */
    public List getContents() {
      return contents;
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
  public static final class Content {

    private final String name;
    private final String content;

    Content(String name, String content) {
      this.name = name;
      this.content = content;
    }

    public String getName() {
      return name;
    }

    public String getContent() {
      return content;
    }
  }

}
