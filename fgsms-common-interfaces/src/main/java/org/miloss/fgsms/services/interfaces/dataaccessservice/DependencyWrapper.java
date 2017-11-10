
package org.miloss.fgsms.services.interfaces.dataaccessservice;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DependencyWrapper complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DependencyWrapper">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="sourceuri" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="sourceaction" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="destinationuri" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="destinationaction" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DependencyWrapper", propOrder = {
    "sourceuri",
    "sourceaction",
    "destinationuri",
    "destinationaction"
})
public class DependencyWrapper
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(required = true)
    protected String sourceuri;
    @XmlElement(required = true)
    protected String sourceaction;
    @XmlElement(required = true)
    protected String destinationuri;
    @XmlElement(required = true)
    protected String destinationaction;

    /**
     * Gets the value of the sourceuri property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSourceuri() {
        return sourceuri;
    }

    /**
     * Sets the value of the sourceuri property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSourceuri(String value) {
        this.sourceuri = value;
    }

    public boolean isSetSourceuri() {
        return (this.sourceuri!= null);
    }

    /**
     * Gets the value of the sourceaction property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSourceaction() {
        return sourceaction;
    }

    /**
     * Sets the value of the sourceaction property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSourceaction(String value) {
        this.sourceaction = value;
    }

    public boolean isSetSourceaction() {
        return (this.sourceaction!= null);
    }

    /**
     * Gets the value of the destinationuri property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDestinationuri() {
        return destinationuri;
    }

    /**
     * Sets the value of the destinationuri property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDestinationuri(String value) {
        this.destinationuri = value;
    }

    public boolean isSetDestinationuri() {
        return (this.destinationuri!= null);
    }

    /**
     * Gets the value of the destinationaction property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDestinationaction() {
        return destinationaction;
    }

    /**
     * Sets the value of the destinationaction property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDestinationaction(String value) {
        this.destinationaction = value;
    }

    public boolean isSetDestinationaction() {
        return (this.destinationaction!= null);
    }

}
