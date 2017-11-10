
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
 *         &lt;element name="GetAlertsRequest" type="{urn:org:miloss:fgsms:services:interfaces:dataAccessService}GetAlertsRequestMsg"/>
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
    "getAlertsRequest"
})
@XmlRootElement(name = "GetAlerts")
public class GetAlerts
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "GetAlertsRequest", required = true)
    protected GetAlertsRequestMsg getAlertsRequest;

    /**
     * Gets the value of the getAlertsRequest property.
     * 
     * @return
     *     possible object is
     *     {@link GetAlertsRequestMsg }
     *     
     */
    public GetAlertsRequestMsg getGetAlertsRequest() {
        return getAlertsRequest;
    }

    /**
     * Sets the value of the getAlertsRequest property.
     * 
     * @param value
     *     allowed object is
     *     {@link GetAlertsRequestMsg }
     *     
     */
    public void setGetAlertsRequest(GetAlertsRequestMsg value) {
        this.getAlertsRequest = value;
    }

    public boolean isSetGetAlertsRequest() {
        return (this.getAlertsRequest!= null);
    }

}
