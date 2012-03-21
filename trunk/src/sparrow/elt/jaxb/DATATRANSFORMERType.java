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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DATATRANSFORMERType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DATATRANSFORMERType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="PARAM" type="{}PARAMType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="CLASS" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="NAME" type="{http://www.w3.org/2001/XMLSchema}string" default="datatransformer" />
 *       &lt;attribute name="POOL-SIZE" type="{http://www.w3.org/2001/XMLSchema}int" default="25" />
 *       &lt;attribute name="THREAD-COUNT" type="{http://www.w3.org/2001/XMLSchema}int" default="5" />
 *       &lt;attribute name="TYPE" type="{http://www.w3.org/2001/XMLSchema}string" default="DEFAULT" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DATATRANSFORMERType", propOrder = {
    "param"
})
public class DATATRANSFORMERType {

    @XmlElement(name = "PARAM")
    protected List<PARAMType> param;
    @XmlAttribute(name = "CLASS")
    protected String _class;
    @XmlAttribute(name = "NAME")
    protected String name;
    @XmlAttribute(name = "POOL-SIZE")
    protected Integer poolsize;
    @XmlAttribute(name = "THREAD-COUNT")
    protected Integer threadcount;
    @XmlAttribute(name = "TYPE")
    protected String type;

    /**
     * Gets the value of the param property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the param property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPARAM().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PARAMType }
     * 
     * 
     */
    public List<PARAMType> getPARAM() {
        if (param == null) {
            param = new ArrayList<PARAMType>();
        }
        return this.param;
    }

    /**
     * Gets the value of the class property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCLASS() {
        return _class;
    }

    /**
     * Sets the value of the class property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCLASS(String value) {
        this._class = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNAME() {
        if (name == null) {
            return "datatransformer";
        } else {
            return name;
        }
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNAME(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the poolsize property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public int getPOOLSIZE() {
        if (poolsize == null) {
            return  25;
        } else {
            return poolsize;
        }
    }

    /**
     * Sets the value of the poolsize property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setPOOLSIZE(Integer value) {
        this.poolsize = value;
    }

    /**
     * Gets the value of the threadcount property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public int getTHREADCOUNT() {
        if (threadcount == null) {
            return  5;
        } else {
            return threadcount;
        }
    }

    /**
     * Sets the value of the threadcount property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setTHREADCOUNT(Integer value) {
        this.threadcount = value;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTYPE() {
        if (type == null) {
            return "DEFAULT";
        } else {
            return type;
        }
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTYPE(String value) {
        this.type = value;
    }

}
