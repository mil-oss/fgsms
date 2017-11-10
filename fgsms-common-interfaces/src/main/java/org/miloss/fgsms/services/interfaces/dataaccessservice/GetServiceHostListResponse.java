
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
 *         &lt;element name="GetServiceHostListResult" type="{urn:org:miloss:fgsms:services:interfaces:dataAccessService}GetServiceHostListResponseMsg"/>
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
    "getServiceHostListResult"
})
@XmlRootElement(name = "GetServiceHostListResponse")
public class GetServiceHostListResponse
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "GetServiceHostListResult", required = true, nillable = true)
    protected GetServiceHostListResponseMsg getServiceHostListResult;

    /**
     * Gets the value of the getServiceHostListResult property.
     * 
     * @return
     *     possible object is
     *     {@link GetServiceHostListResponseMsg }
     *     
     */
    public GetServiceHostListResponseMsg getGetServiceHostListResult() {
        return getServiceHostListResult;
    }

    /**
     * Sets the value of the getServiceHostListResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link GetServiceHostListResponseMsg }
     *     
     */
    public void setGetServiceHostListResult(GetServiceHostListResponseMsg value) {
        this.getServiceHostListResult = value;
    }

    public boolean isSetGetServiceHostListResult() {
        return (this.getServiceHostListResult!= null);
    }

}
