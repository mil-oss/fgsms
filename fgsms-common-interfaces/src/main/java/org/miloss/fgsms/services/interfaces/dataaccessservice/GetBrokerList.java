
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
 *         &lt;element name="GetBrokerListRequest" type="{urn:org:miloss:fgsms:services:interfaces:dataAccessService}GetBrokerListRequestMsg"/>
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
    "getBrokerListRequest"
})
@XmlRootElement(name = "GetBrokerList")
public class GetBrokerList
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "GetBrokerListRequest", required = true)
    protected GetBrokerListRequestMsg getBrokerListRequest;

    /**
     * Gets the value of the getBrokerListRequest property.
     * 
     * @return
     *     possible object is
     *     {@link GetBrokerListRequestMsg }
     *     
     */
    public GetBrokerListRequestMsg getGetBrokerListRequest() {
        return getBrokerListRequest;
    }

    /**
     * Sets the value of the getBrokerListRequest property.
     * 
     * @param value
     *     allowed object is
     *     {@link GetBrokerListRequestMsg }
     *     
     */
    public void setGetBrokerListRequest(GetBrokerListRequestMsg value) {
        this.getBrokerListRequest = value;
    }

    public boolean isSetGetBrokerListRequest() {
        return (this.getBrokerListRequest!= null);
    }

}
