//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.0 in JDK 1.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.03.20 at 07:57:22 AM SGT 
//


package sparrow.elt.jaxb;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for EXCEPTIONHANDLERType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="EXCEPTIONHANDLERType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="HANDLER" type="{}HANDLERType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="HANDLE" type="{}HANDLEType" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EXCEPTIONHANDLERType", propOrder = {
    "handler",
    "handle"
})
public class EXCEPTIONHANDLERType {

    @XmlElement(name = "HANDLER")
    protected List<HANDLERType> handler;
    @XmlElement(name = "HANDLE", required = true)
    protected List<HANDLEType> handle;

    /**
     * Gets the value of the handler property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the handler property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getHANDLER().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link HANDLERType }
     * 
     * 
     */
    public List<HANDLERType> getHANDLER() {
        if (handler == null) {
            handler = new ArrayList<HANDLERType>();
        }
        return this.handler;
    }

    /**
     * Gets the value of the handle property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the handle property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getHANDLE().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link HANDLEType }
     * 
     * 
     */
    public List<HANDLEType> getHANDLE() {
        if (handle == null) {
            handle = new ArrayList<HANDLEType>();
        }
        return this.handle;
    }

}