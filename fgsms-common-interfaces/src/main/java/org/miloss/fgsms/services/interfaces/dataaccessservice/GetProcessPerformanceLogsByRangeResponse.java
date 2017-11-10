
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
 *         &lt;element name="GetProcessPerformanceLogsByRangeResponse" type="{urn:org:miloss:fgsms:services:interfaces:dataAccessService}GetProcessPerformanceLogsByRangeResponseMsg"/>
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
    "getProcessPerformanceLogsByRangeResponse"
})
@XmlRootElement(name = "GetProcessPerformanceLogsByRangeResponse")
public class GetProcessPerformanceLogsByRangeResponse
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "GetProcessPerformanceLogsByRangeResponse", required = true)
    protected GetProcessPerformanceLogsByRangeResponseMsg getProcessPerformanceLogsByRangeResponse;

    /**
     * Gets the value of the getProcessPerformanceLogsByRangeResponse property.
     * 
     * @return
     *     possible object is
     *     {@link GetProcessPerformanceLogsByRangeResponseMsg }
     *     
     */
    public GetProcessPerformanceLogsByRangeResponseMsg getGetProcessPerformanceLogsByRangeResponse() {
        return getProcessPerformanceLogsByRangeResponse;
    }

    /**
     * Sets the value of the getProcessPerformanceLogsByRangeResponse property.
     * 
     * @param value
     *     allowed object is
     *     {@link GetProcessPerformanceLogsByRangeResponseMsg }
     *     
     */
    public void setGetProcessPerformanceLogsByRangeResponse(GetProcessPerformanceLogsByRangeResponseMsg value) {
        this.getProcessPerformanceLogsByRangeResponse = value;
    }

    public boolean isSetGetProcessPerformanceLogsByRangeResponse() {
        return (this.getProcessPerformanceLogsByRangeResponse!= null);
    }

}
