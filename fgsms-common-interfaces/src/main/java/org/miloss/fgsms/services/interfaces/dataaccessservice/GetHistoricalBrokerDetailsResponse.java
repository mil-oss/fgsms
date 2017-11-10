
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
 *         &lt;element name="GetHistoricalBrokerDetailsResponse" type="{urn:org:miloss:fgsms:services:interfaces:dataAccessService}GetHistoricalBrokerDetailsResponseMsg"/>
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
    "getHistoricalBrokerDetailsResponse"
})
@XmlRootElement(name = "GetHistoricalBrokerDetailsResponse")
public class GetHistoricalBrokerDetailsResponse
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "GetHistoricalBrokerDetailsResponse", required = true)
    protected GetHistoricalBrokerDetailsResponseMsg getHistoricalBrokerDetailsResponse;

    /**
     * Gets the value of the getHistoricalBrokerDetailsResponse property.
     * 
     * @return
     *     possible object is
     *     {@link GetHistoricalBrokerDetailsResponseMsg }
     *     
     */
    public GetHistoricalBrokerDetailsResponseMsg getGetHistoricalBrokerDetailsResponse() {
        return getHistoricalBrokerDetailsResponse;
    }

    /**
     * Sets the value of the getHistoricalBrokerDetailsResponse property.
     * 
     * @param value
     *     allowed object is
     *     {@link GetHistoricalBrokerDetailsResponseMsg }
     *     
     */
    public void setGetHistoricalBrokerDetailsResponse(GetHistoricalBrokerDetailsResponseMsg value) {
        this.getHistoricalBrokerDetailsResponse = value;
    }

    public boolean isSetGetHistoricalBrokerDetailsResponse() {
        return (this.getHistoricalBrokerDetailsResponse!= null);
    }

}
