
package org.miloss.fgsms.services.interfaces.policyconfiguration;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 * sla registration object
 * 			
 * 
 * <p>Java class for SLAregistration complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SLAregistration">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="SLA_ID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="service_uri" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SLAregistration", propOrder = {
    "slaid",
    "serviceUri"
})
public class SLAregistration
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "SLA_ID", required = true)
    protected String slaid;
    @XmlElement(name = "service_uri", required = true)
    protected String serviceUri;

    /**
     * Gets the value of the slaid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSLAID() {
        return slaid;
    }

    /**
     * Sets the value of the slaid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSLAID(String value) {
        this.slaid = value;
    }

    public boolean isSetSLAID() {
        return (this.slaid!= null);
    }

    /**
     * Gets the value of the serviceUri property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getServiceUri() {
        return serviceUri;
    }

    /**
     * Sets the value of the serviceUri property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setServiceUri(String value) {
        this.serviceUri = value;
    }

    public boolean isSetServiceUri() {
        return (this.serviceUri!= null);
    }

}
