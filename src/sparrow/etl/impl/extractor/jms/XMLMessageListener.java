package sparrow.etl.impl.extractor.jms;

import org.jdom.Document;
import org.jdom.Element;

import sparrow.etl.core.config.SparrowDataExtractorConfig;
import sparrow.etl.core.dao.metadata.ColumnAttributes;
import sparrow.etl.core.exception.DataException;
import sparrow.etl.core.exception.ParserException;
import sparrow.etl.core.util.XmlUtil;

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public class XMLMessageListener
    extends GenericMessageListener {

  private Document doc = null;
  private Element root = null;

  /**
   *
   * @param config SparrowDataExtractorConfig
   */
  public XMLMessageListener(SparrowDataExtractorConfig config) {
    super(config);
  }

  /**
   * onMessage
   *
   * @param message SparrowJMSMessage
   * @return String[]
   */
  public String[] onMessage(SparrowJMSMessage message) throws DataException,
      ParserException {

    String msg = message.getMessage();
    String messg[] = null;

    if (msg != null) {
      try {
        doc = XmlUtil.getDocument(msg);
        root = XmlUtil.getRoot(doc);
        ColumnAttributes[] colAttribs = getColDefAttributes();
        messg = new String[colAttribs.length];

        for (int i = 0; i < colAttribs.length; i++) {

          String xPath = colAttribs[i].getXPath();

          if (xPath == null || xPath.trim().equals("")) {
            throw new ParserException(
                "Attribute [xpath] is null or empty for column [" +
                colAttribs[i].getColumnName() + "]");
          }

          if (xPath.startsWith(RESERVE_TOCKEN)) {
            messg[i] = getValue(xPath, message);
            continue;
          }
          else {
            messg[i] = getValue(colAttribs[i]);
          }
        }
      }
      catch (Exception ex) {
        throw new ParserException(
            "Exception occured while processing xml message [" + msg + "]",ex);
      }
      finally {
        doc = null;
        root = null;
      }
    }
    else {
      throw new DataException("Message is Null or Empty. MSGID [" +
                              message.getMessageId() + "]");
    }

    return messg;
  }

  /**
   *
   * @param attrib ColumnAttributes
   * @return String
   */
  protected String getValue(ColumnAttributes attrib) throws
      Exception {
    return XmlUtil.getText(root, attrib.getXPath());
  }

}
