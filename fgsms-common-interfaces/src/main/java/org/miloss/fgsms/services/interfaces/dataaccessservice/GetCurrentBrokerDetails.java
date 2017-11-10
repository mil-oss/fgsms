
package org.miloss.fgsms.services.interfaces.dataaccessservice;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="GetCurrentBrokerDetailsRequest" type="{urn:org:miloss:fgsms:services:interfaces:dataAccessService}GetCurrentBrokerDetailsRequestMsg"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "getCurrentBrokerDetailsRequest"
})
@XmlRootElement(name = "GetCurrentBrokerDetails")
public class GetCurrentBrokerDetails
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "GetCurrentBrokerDetailsRequest", required = true)
    protected GetCurrentBrokerDetailsRequestMsg getCurrentBrokerDetailsRequest;

    /**
     * Gets the value of the getCurrentBrokerDetailsRequest property.
     * 
     * @return
     *     possible object is
     *     {@link GetCurrentBrokerDetailsRequestMsg }
     *     
     */
    public GetCurrentBrokerDetailsRequestMsg getGetCurrentBrokerDetailsRequest() {
        return getCurrentBrokerDetailsRequest;
    }

    /**
     * Sets the value of the getCurrentBrokerDetailsRequest property.
     * 
     * @param value
     *     allowed object is
     *     {@link GetCurrentBrokerDetailsRequestMsg }
     *     
     */
    public void setGetCurrentBrokerDetailsRequest(GetCurrentBrokerDetailsRequestMsg value) {
        this.getCurrentBrokerDetailsRequest = value;
    }

    public boolean isSetGetCurrentBrokerDetailsRequest() {
        return (this.getCurrentBrokerDetailsRequest!= null);
    }

}
