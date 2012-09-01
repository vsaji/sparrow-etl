//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.07.23 at 07:01:14 PM SGT 
//


package sparrow.etl.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for MODULEType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="MODULEType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="NAME" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="PROCESS-ID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="DESCRIPTION" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="LOAD-BALANCE" type="{}LOADBALANCEType" minOccurs="0"/>
 *         &lt;element name="CYCLE-DEPENDENCIES" type="{}CYCLEDEPENDENCIESype" minOccurs="0"/>
 *         &lt;element name="MODULE-PARAM" type="{}MODULE-PARAMType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MODULEType", propOrder = {
    "name",
    "processid",
    "description",
    "loadbalance",
    "cycledependencies",
    "moduleparam"
})
public class MODULEType {

    @XmlElement(name = "NAME", required = true)
    protected String name;
    @XmlElement(name = "PROCESS-ID", required = true)
    protected String processid;
    @XmlElement(name = "DESCRIPTION")
    protected String description;
    @XmlElement(name = "LOAD-BALANCE")
    protected LOADBALANCEType loadbalance;
    @XmlElement(name = "CYCLE-DEPENDENCIES")
    protected CYCLEDEPENDENCIESype cycledependencies;
    @XmlElement(name = "MODULE-PARAM")
    protected MODULEPARAMType moduleparam;

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
     * Gets the value of the processid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPROCESSID() {
        return processid;
    }

    /**
     * Sets the value of the processid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPROCESSID(String value) {
        this.processid = value;
    }

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDESCRIPTION() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDESCRIPTION(String value) {
        this.description = value;
    }

    /**
     * Gets the value of the loadbalance property.
     * 
     * @return
     *     possible object is
     *     {@link LOADBALANCEType }
     *     
     */
    public LOADBALANCEType getLOADBALANCE() {
        return loadbalance;
    }

    /**
     * Sets the value of the loadbalance property.
     * 
     * @param value
     *     allowed object is
     *     {@link LOADBALANCEType }
     *     
     */
    public void setLOADBALANCE(LOADBALANCEType value) {
        this.loadbalance = value;
    }

    /**
     * Gets the value of the cycledependencies property.
     * 
     * @return
     *     possible object is
     *     {@link CYCLEDEPENDENCIESype }
     *     
     */
    public CYCLEDEPENDENCIESype getCYCLEDEPENDENCIES() {
        return cycledependencies;
    }

    /**
     * Sets the value of the cycledependencies property.
     * 
     * @param value
     *     allowed object is
     *     {@link CYCLEDEPENDENCIESype }
     *     
     */
    public void setCYCLEDEPENDENCIES(CYCLEDEPENDENCIESype value) {
        this.cycledependencies = value;
    }

    /**
     * Gets the value of the moduleparam property.
     * 
     * @return
     *     possible object is
     *     {@link MODULEPARAMType }
     *     
     */
    public MODULEPARAMType getMODULEPARAM() {
        return moduleparam;
    }

    /**
     * Sets the value of the moduleparam property.
     * 
     * @param value
     *     allowed object is
     *     {@link MODULEPARAMType }
     *     
     */
    public void setMODULEPARAM(MODULEPARAMType value) {
        this.moduleparam = value;
    }

}
