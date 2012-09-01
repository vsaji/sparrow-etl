package sparrow.etl.impl.script;

import java.io.StringReader;
import java.io.StringWriter;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import sparrow.etl.core.DataSet;
import sparrow.etl.core.config.SparrowDataTransformerConfig;
import sparrow.etl.core.exception.InitializationException;
import sparrow.etl.core.exception.ScriptException;
import sparrow.etl.core.script.AbstractScriptEngine;
import sparrow.etl.core.transformer.DataTransformer;
import sparrow.etl.core.transformer.PlaceHolder;
import sparrow.etl.core.transformer.PlaceHolderFactory;
import sparrow.etl.core.util.ConfigKeyConstants;
import sparrow.etl.core.util.Constants;
import sparrow.etl.core.util.SparrowUtil;
import sparrow.etl.core.vo.DataOutputHolder;


/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public class XSLTScriptEngine
    extends AbstractScriptEngine {

  protected Source xsltSource = null;
  protected static Templates tmp = null;
  protected static PlaceHolder[] placeHolder;
  protected static String key;

  /**
   *
   * @param config SparrowDataTransformerConfig
   */
  public XSLTScriptEngine(SparrowDataTransformerConfig config) {
    super(config);
  }

  public XSLTScriptEngine() {
    super();
  }

  public XSLTScriptEngine(String expression) {
    super(expression);
  }

  /**
   *
   */
  public void initialize() {
    try {
      xsltSource = new StreamSource(new StringReader(sc.getContent()));
      TransformerFactory transFact =
          TransformerFactory.newInstance();
      tmp = transFact.newTemplates(xsltSource);

      String placeHlder = SparrowUtil.performTernary(config.getInitParameter(),
          ConfigKeyConstants.PARAM_PLACEHOLDER,Constants.OBJECT);

      String[] plcHlders = (placeHlder.indexOf(",") > 0) ?
          placeHlder.split("[,]") : new String[] {placeHlder};

      placeHolder = new PlaceHolder[plcHlders.length];

      for (int i = 0; i < plcHlders.length; i++) {
        placeHolder[i] = PlaceHolderFactory.resolvePlaceHolder(plcHlders[i]);
      }

      key = SparrowUtil.performTernary(config.getInitParameter(),
                                     ConfigKeyConstants.PARAM_KEY_NAME,
                                     config.getName());

      super.initialize();
    }
    catch (Exception ex) {
      throw new InitializationException(ex);
    }
  }

  /**
   * evaluate
   *
   * @param dataSet DataSet
   * @param dt DataTransformer
   * @return Object
   */
  public Object evaluate(DataSet dataSet, DataTransformer dt) throws
      ScriptException {

    DataOutputHolder dataout = new DataOutputHolder();
    String xml = dataSet.getDataSetAsXML();
    try {
      Source xmlSource = new StreamSource(new StringReader(xml));
      StringWriter sw = new StringWriter();
      Transformer trans = tmp.newTransformer();
      //trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,"yes");
      //trans.setOutputProperty(OutputKeys.STANDALONE,"yes");
      trans.transform(xmlSource, new StreamResult(sw));

      for(int i=0; i < placeHolder.length; i++){
       placeHolder[i].setValue(dataout, sw.toString(), key);
     }

    }
    catch (Exception ex) {
      ex.printStackTrace();
      throw new ScriptException("XSLT", ex.getMessage(), ex);
    }

    return dataout;
  }

}
