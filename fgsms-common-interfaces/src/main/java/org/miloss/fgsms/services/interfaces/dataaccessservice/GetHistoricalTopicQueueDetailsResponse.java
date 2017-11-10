
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
 *         &lt;element name="GetHistoricalTopicQueueDetailsResponse" type="{urn:org:miloss:fgsms:services:interfaces:dataAccessService}GetHistoricalTopicQueueDetailsResponseMsg"/>
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
    "getHistoricalTopicQueueDetailsResponse"
})
@XmlRootElement(name = "GetHistoricalTopicQueueDetailsResponse")
public class GetHistoricalTopicQueueDetailsResponse
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "GetHistoricalTopicQueueDetailsResponse", required = true)
    protected GetHistoricalTopicQueueDetailsResponseMsg getHistoricalTopicQueueDetailsResponse;

    /**
     * Gets the value of the getHistoricalTopicQueueDetailsResponse property.
     * 
     * @return
     *     possible object is
     *     {@link GetHistoricalTopicQueueDetailsResponseMsg }
     *     
     */
    public GetHistoricalTopicQueueDetailsResponseMsg getGetHistoricalTopicQueueDetailsResponse() {
        return getHistoricalTopicQueueDetailsResponse;
    }

    /**
     * Sets the value of the getHistoricalTopicQueueDetailsResponse property.
     * 
     * @param value
     *     allowed object is
     *     {@link GetHistoricalTopicQueueDetailsResponseMsg }
     *     
     */
    public void setGetHistoricalTopicQueueDetailsResponse(GetHistoricalTopicQueueDetailsResponseMsg value) {
        this.getHistoricalTopicQueueDetailsResponse = value;
    }

    public boolean isSetGetHistoricalTopicQueueDetailsResponse() {
        return (this.getHistoricalTopicQueueDetailsResponse!= null);
    }

}
