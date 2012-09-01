package sparrow.etl.core.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.DOMBuilder;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */
public final class XmlUtil {

  private static final String XML_SAX_PARSER =
      "org.apache.xerces.parsers.SAXParser";

  /* To hold SAXBuilder features, will be used while validating XML against XSD*/
  private static final String XML_SCHEMA_FEATURE =
      "http://apache.org/xml/features/validation/schema";

  /* To hold SAXBuilder property, will be used while validating XML against XSD*/
  private static final String XML_SCHEMA_PROPERTY =
      "http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation";

  /**
   * This method returns the Element node for the given Xpath
   * getElement
   *
   * @param xPath String
   * @param node Element
   * @throws JDOMException
   * @return Element
   */
  public static Element getElement(String xPath, Element node) throws
      JDOMException {
    if (xPath.equals("") || node == null) {
      return null;
    }
    return (Element) XPath.selectSingleNode(node, xPath);
  }

  /**
   * This method returns the value of the TEXTNODE
   * @param element Element - node element
   * @return String returns the actual value or ""
   */
  public static String getText(Element node) {
    if (node == null) {
      return null;
    }
    return node.getText();
  }

  /**
   * This method returns the value of the TEXTNODE
   * @param element Element - node element
   * @return String returns the actual value or ""
   */
  public static String getText(Element rootElement, String xPath) throws
      JDOMException {
    String value = null;
    if (rootElement == null) {
      value = null;
    }
    Element e = (Element) XPath.selectSingleNode(rootElement, xPath);

    if (e != null) {
      value = e.getText();
    }
    return value;
  }

  /**
   * This method returns name of the element node
   * @param element Element - node element
   * @return String returns the actual value or ""
   */
  public static String getElementName(Element node) {
    if (node == null) {
      return null;
    }
    return node.getName();
  }

  /**
   * This method is used to apply Xpath on the node passed as parameter
   * and the returns the result as List.
   *
   * @param xPath String - xPath for the element.
   * @param node Element - node element.
   * @return Element returns the Element achieved after applying XPath.
   */
  public static List getElementList(String xPath, Element node) throws
      JDOMException {
    if (xPath.equals("") || node == null) {
      return null;
    }
    return XPath.selectNodes(node, xPath);
  }

  /**
   * Get root element of the given document.
   *
   * @param doc Document
   * @return Element
   */
  public static Element getRoot(Document doc) {
    if (doc == null) {
      return null;
    }
    return doc.getRootElement();
  }

  /**
   * This method provides the functionality to set the TAG values to XML template
   * based on the xpath given as argument.
   *
   * @param path String
   * @param value String
   * @throws Exception
   */
  public static void setText(String path, String value, Element rootElement) throws
      Exception {

    XPath xpath = XPath.newInstance(path);
    Element element = (Element) xpath.selectSingleNode(rootElement);
    element.setText(value);
  }

  /**
   * This method provides the functionality to remove the XML element
   * based on the xpath given as argument.
   *
   * @param node String
   * @throws SystemException
   */
  public static void removeElement(String node, Element rootElement) throws
      Exception {

    rootElement.removeChild(node);

  }

  /**
   * This method return the populated XML format as String
   *
   * @throws SystemException
   * @return String
   */
  public static String getXMLAsString(Document document) {

    XMLOutputter xmloutputter = new XMLOutputter();
    String xmlContent = xmloutputter.outputString(document);
    xmlContent = xmlContent.replaceAll("[\r\n][\r\n\t]*[\r\n]", "\n\t");

    return xmlContent;
  }

  /**
   * This methods takes input as the XML String and returns the JDOM object
   * parseXMLData
   *
   * @param xmlString String
   * @throws Exception
   * @return Document
   */
  public static Document getDocument(String xmlString) throws Exception {
    if (xmlString.equals("") || xmlString == null) {
      return null;
    }
    Document document = new Document();
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = factory.newDocumentBuilder();
    org.w3c.dom.Document doc = builder.parse(
        new InputSource(new StringReader(xmlString)));
    DOMBuilder jdomBuilder = new DOMBuilder();
    document = jdomBuilder.build(doc);
    return document;
  }

  /**
   * This methods takes input as the XML String and returns the JDOM object
   * parseXMLData
   *
   * @param xmlString String
   * @throws Exception
   * @return Document
   */
  public static Document getDocument(String xmlString, String xsdPath) throws   Exception {
    if (xmlString.equals("") || xmlString == null) {
      return null;
    }

    SAXBuilder builder = new SAXBuilder(XML_SAX_PARSER);
    builder.setValidation(true);
    builder.setFeature(XML_SCHEMA_FEATURE, true);
    builder.setProperty(XML_SCHEMA_PROPERTY, xsdPath);
    builder.setEntityResolver(new SchemaLoader(xsdPath));

    ByteArrayInputStream is = new ByteArrayInputStream(xmlString.getBytes());
    builder.build(is);
    is.close();

    return getDocument(xmlString);
  }

  /**
   * This methold validates the XML template agains the given schema and returns
   * <code> true </code> if schema compliance with xml string else returns <code>
   * false</code>
   *
   * @param String xsdPath
   * @return boolean
   */
  public static boolean validateXML(String xsdPath, Document document) throws
      Exception {
    getDocument(getXMLAsString(document),xsdPath);
    return true;
  }

  /**
   * This method return <code>Document</code> object after parsing the <code>
   * xmlFile</code>. xmlFile should be avaibale in the classpath.
   *
   * @param xmlFileName String
   * @return org.jdom.Document
   * @throws Exception
   */
  public static Document getDocumentFromFile(String xmlFileName) throws
      Exception {

    SAXBuilder builder = new SAXBuilder(XML_SAX_PARSER);
    builder.setValidation(false);
    InputStream in = XmlUtil.class.getClassLoader().getResourceAsStream(
        xmlFileName);
    Document document = builder.build(in);
    return document;
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
  static class SchemaLoader
      implements EntityResolver {

    private String xsdFile = null;

    public SchemaLoader(String xsdFile) {
      this.xsdFile = xsdFile;
    }

    /**
     * resolveEntity
     *
     * @param publicId String
     * @param systemId String
     * @throws SAXException
     * @throws IOException
     * @return InputSource
     */
    public InputSource resolveEntity(String publicId, String systemId) throws
        SAXException, IOException {
      InputStream is = null;
      try {
        is = SparrowUtil.getFileAsStream(xsdFile);
      }
      catch (Exception ex) {
        throw new SAXException(ex);
      }
      return new InputSource(is);
    }

  }

}
