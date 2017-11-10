
package org.miloss.fgsms.services.interfaces.faults;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 *             Occurs when an underlying dependency fails or is unavailable.
 *             
 * 
 * <p>Java class for ServiceUnavailableException complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ServiceUnavailableException">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="code" type="{urn:org:miloss:fgsms:services:interfaces:faults}ServiceUnavailableFaultCodes"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ServiceUnavailableException", propOrder = {
    "code"
})
public class ServiceUnavailableException {

    @XmlElement(required = true)
    protected ServiceUnavailableFaultCodes code;

    /**
     * Gets the value of the code property.
     * 
     * @return
     *     possible object is
     *     {@link ServiceUnavailableFaultCodes }
     *     
     */
    public ServiceUnavailableFaultCodes getCode() {
        return code;
    }

    /**
     * Sets the value of the code property.
     * 
     * @param value
     *     allowed object is
     *     {@link ServiceUnavailableFaultCodes }
     *     
     */
    public void setCode(ServiceUnavailableFaultCodes value) {
        this.code = value;
    }

}
