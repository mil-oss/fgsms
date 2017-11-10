
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
 *         &lt;element name="GetBrokerListResponse" type="{urn:org:miloss:fgsms:services:interfaces:dataAccessService}GetBrokerListResponseMsg"/>
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
    "getBrokerListResponse"
})
@XmlRootElement(name = "GetBrokerListResponse")
public class GetBrokerListResponse
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "GetBrokerListResponse", required = true)
    protected GetBrokerListResponseMsg getBrokerListResponse;

    /**
     * Gets the value of the getBrokerListResponse property.
     * 
     * @return
     *     possible object is
     *     {@link GetBrokerListResponseMsg }
     *     
     */
    public GetBrokerListResponseMsg getGetBrokerListResponse() {
        return getBrokerListResponse;
    }

    /**
     * Sets the value of the getBrokerListResponse property.
     * 
     * @param value
     *     allowed object is
     *     {@link GetBrokerListResponseMsg }
     *     
     */
    public void setGetBrokerListResponse(GetBrokerListResponseMsg value) {
        this.getBrokerListResponse = value;
    }

    public boolean isSetGetBrokerListResponse() {
        return (this.getBrokerListResponse!= null);
    }

}
