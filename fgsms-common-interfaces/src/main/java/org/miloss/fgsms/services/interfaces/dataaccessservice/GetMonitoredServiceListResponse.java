
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
 *         &lt;element name="GetMonitoredServiceListResult" type="{urn:org:miloss:fgsms:services:interfaces:dataAccessService}GetMonitoredServiceListResponseMsg"/>
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
    "getMonitoredServiceListResult"
})
@XmlRootElement(name = "GetMonitoredServiceListResponse")
public class GetMonitoredServiceListResponse
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "GetMonitoredServiceListResult", required = true, nillable = true)
    protected GetMonitoredServiceListResponseMsg getMonitoredServiceListResult;

    /**
     * Gets the value of the getMonitoredServiceListResult property.
     * 
     * @return
     *     possible object is
     *     {@link GetMonitoredServiceListResponseMsg }
     *     
     */
    public GetMonitoredServiceListResponseMsg getGetMonitoredServiceListResult() {
        return getMonitoredServiceListResult;
    }

    /**
     * Sets the value of the getMonitoredServiceListResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link GetMonitoredServiceListResponseMsg }
     *     
     */
    public void setGetMonitoredServiceListResult(GetMonitoredServiceListResponseMsg value) {
        this.getMonitoredServiceListResult = value;
    }

    public boolean isSetGetMonitoredServiceListResult() {
        return (this.getMonitoredServiceListResult!= null);
    }

}
