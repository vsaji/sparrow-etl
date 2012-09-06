package sparrow.etl.impl.extractor.jms;

import sparrow.etl.core.config.SparrowDataExtractorConfig;
import sparrow.etl.core.dao.metadata.ColumnAttributes;
import sparrow.etl.core.exception.DataException;
import sparrow.etl.core.exception.ParserException;

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */
public class GenericMessageListener
    extends AbstractSparrowMessageListener {

  protected static final String RESERVE_TOCKEN = "@";

  /**
   *
   * @param config SparrowDataExtractorConfig
   */
  public GenericMessageListener(SparrowDataExtractorConfig config) {
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

      ColumnAttributes[] colAttribs = getColDefAttributes();
      messg = new String[colAttribs.length];

      for (int i = 0; i < colAttribs.length; i++) {

        String xPath = colAttribs[i].getXPath();

       /** if (xPath == null || xPath.trim().equals("")) {
          throw new ParserException(
              "Attribute [xpath] is null or empty for column [" +
              colAttribs[i].getColumnName() + "]");
        }**/

       if (xPath == null || xPath.trim().equals("")) {
         xPath=colAttribs[i].getColumnName();
       }

        messg[i] = getValue(xPath, message);
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
   * @param xPath String
   * @param message SparrowJMSMessage
   * @return String
   */
  protected String getValue(String xPath, SparrowJMSMessage message) {

    if (xPath.startsWith(RESERVE_TOCKEN + "MSGID")) {
      return message.getMessageId();
    }
    else if (xPath.startsWith(RESERVE_TOCKEN + "MSG")) {
      return message.getMessage();
    }
    else if (xPath.startsWith(RESERVE_TOCKEN + "INTRL_MSGID")) {
      return message.getInternalMessageId();
    }
    else if (xPath.startsWith(RESERVE_TOCKEN + "RETRY_COUNT")) {
      return String.valueOf(message.getRetryCount());
    }
    else if (xPath.startsWith(RESERVE_TOCKEN + "PROP")) {
      String prop = xPath.substring(xPath.indexOf(":"));
      return message.getHeaderProperty(prop);
    }
    return null;
  }

}
