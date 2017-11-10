
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
 *         &lt;element name="GetHistoricalBrokerDetailsRequest" type="{urn:org:miloss:fgsms:services:interfaces:dataAccessService}GetHistoricalBrokerDetailsRequestMsg"/>
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
    "getHistoricalBrokerDetailsRequest"
})
@XmlRootElement(name = "GetHistoricalBrokerDetails")
public class GetHistoricalBrokerDetails
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "GetHistoricalBrokerDetailsRequest", required = true)
    protected GetHistoricalBrokerDetailsRequestMsg getHistoricalBrokerDetailsRequest;

    /**
     * Gets the value of the getHistoricalBrokerDetailsRequest property.
     * 
     * @return
     *     possible object is
     *     {@link GetHistoricalBrokerDetailsRequestMsg }
     *     
     */
    public GetHistoricalBrokerDetailsRequestMsg getGetHistoricalBrokerDetailsRequest() {
        return getHistoricalBrokerDetailsRequest;
    }

    /**
     * Sets the value of the getHistoricalBrokerDetailsRequest property.
     * 
     * @param value
     *     allowed object is
     *     {@link GetHistoricalBrokerDetailsRequestMsg }
     *     
     */
    public void setGetHistoricalBrokerDetailsRequest(GetHistoricalBrokerDetailsRequestMsg value) {
        this.getHistoricalBrokerDetailsRequest = value;
    }

    public boolean isSetGetHistoricalBrokerDetailsRequest() {
        return (this.getHistoricalBrokerDetailsRequest!= null);
    }

}
