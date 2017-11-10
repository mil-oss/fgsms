
package org.miloss.fgsms.services.interfaces.policyconfiguration;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PropertyPair complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PropertyPair">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="propertyname" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="propertyvalue" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PropertyPair", propOrder = {
    "propertyname",
    "propertyvalue"
})
public class PropertyPair
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(required = true)
    protected String propertyname;
    @XmlElement(required = true)
    protected String propertyvalue;

    /**
     * Gets the value of the propertyname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPropertyname() {
        return propertyname;
    }

    /**
     * Sets the value of the propertyname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPropertyname(String value) {
        this.propertyname = value;
    }

    public boolean isSetPropertyname() {
        return (this.propertyname!= null);
    }

    /**
     * Gets the value of the propertyvalue property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPropertyvalue() {
        return propertyvalue;
    }

    /**
     * Sets the value of the propertyvalue property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPropertyvalue(String value) {
        this.propertyvalue = value;
    }

    public boolean isSetPropertyvalue() {
        return (this.propertyvalue!= null);
    }

}
