//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.07.23 at 07:01:14 PM SGT 
//


package sparrow.etl.jaxb;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for WRITERType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WRITERType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="PARAM" type="{}PARAMType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="NAME" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="CLASS" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="TYPE" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="DEPENDS" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="TRIGGER-EVENT" type="{http://www.w3.org/2001/XMLSchema}string" default="request" />
 *       &lt;attribute name="SINGLETON" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WRITERType", propOrder = {
    "param"
})
public class WRITERType {

    @XmlElement(name = "PARAM")
    protected List<PARAMType> param;
    @XmlAttribute(name = "NAME", required = true)
    protected String name;
    @XmlAttribute(name = "CLASS")
    protected String _class;
    @XmlAttribute(name = "TYPE")
    protected String type;
    @XmlAttribute(name = "DEPENDS")
    protected String depends;
    @XmlAttribute(name = "TRIGGER-EVENT")
    protected String triggerevent;
    @XmlAttribute(name = "SINGLETON")
    protected Boolean singleton;

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
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNAME() {
        return name;
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
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTYPE() {
        return type;
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

    /**
     * Gets the value of the depends property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDEPENDS() {
        return depends;
    }

    /**
     * Sets the value of the depends property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDEPENDS(String value) {
        this.depends = value;
    }

    /**
     * Gets the value of the triggerevent property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTRIGGEREVENT() {
        if (triggerevent == null) {
            return "request";
        } else {
            return triggerevent;
        }
    }

    /**
     * Sets the value of the triggerevent property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTRIGGEREVENT(String value) {
        this.triggerevent = value;
    }

    /**
     * Gets the value of the singleton property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isSINGLETON() {
        if (singleton == null) {
            return false;
        } else {
            return singleton;
        }
    }

    /**
     * Sets the value of the singleton property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setSINGLETON(Boolean value) {
        this.singleton = value;
    }

}