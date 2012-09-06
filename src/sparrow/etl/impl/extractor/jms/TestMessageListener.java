package sparrow.etl.impl.extractor.jms;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import sparrow.etl.core.config.SparrowDataExtractorConfig;

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public class TestMessageListener extends AbstractSparrowMessageListener {

  /**
   *
   * @param config SparrowDataExtractorConfig
   */
  public TestMessageListener(SparrowDataExtractorConfig config) {
    super(config);
  }

  /**
   * onMessage
   *
   * @param message Message
   * @return String[]
   */
  public String[] onMessage(SparrowJMSMessage message) {
    String rtn[] = null;

    try {
      String value = message.getMessage();
      rtn = value.split("[,]");
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
    return rtn;
    /*SAXParser sp = null;
         TestSAXHandler handler = null;
         try {
      String value = ( (TextMessage) message).getText();
      InputStream in = new ByteArrayInputStream(value.getBytes());
      sp = SAXParserFactory.newInstance().newSAXParser();
      handler = new TestSAXHandler();
      sp.parse(in, handler);
      in.close();
         }
         catch (Exception ex) {
      ex.printStackTrace();
         }

         return handler.getValues();*/
  }

  public class TestSAXHandler
      extends DefaultHandler {

    ArrayList al = new ArrayList();
    StringBuffer sb = new StringBuffer();

    /**
     *
     * @param uri String
     * @param localName String
     * @param qName String
     * @param attributes Attributes
     * @throws SAXException
     */
    public void startElement(String uri, String localName, String qName,
                             Attributes attributes) throws SAXException {
      sb.setLength(0);
    }

    public void endElement(String uri, String localName, String qName) throws
        SAXException {
      if (! ("row".indexOf(qName) != -1)) {
        String temp = sb.toString();
        temp = (temp != null && temp.trim().equals("null")) ? null : temp;
        al.add(temp);
      }
    }

    public void characters(char ch[], int start, int length) throws
        SAXException {
      sb.append(ch, start, length);
    }

    /**
     *
     * @return String[]
     */
    String[] getValues() {
      return (String[]) al.toArray(new String[al.size()]);
    }

  }
}
