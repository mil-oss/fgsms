
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
 *         &lt;element name="GetMachinePerformanceLogsByRangeResponse" type="{urn:org:miloss:fgsms:services:interfaces:dataAccessService}GetMachinePerformanceLogsByRangeResponseMsg"/>
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
    "getMachinePerformanceLogsByRangeResponse"
})
@XmlRootElement(name = "GetMachinePerformanceLogsByRangeResponse")
public class GetMachinePerformanceLogsByRangeResponse
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "GetMachinePerformanceLogsByRangeResponse", required = true)
    protected GetMachinePerformanceLogsByRangeResponseMsg getMachinePerformanceLogsByRangeResponse;

    /**
     * Gets the value of the getMachinePerformanceLogsByRangeResponse property.
     * 
     * @return
     *     possible object is
     *     {@link GetMachinePerformanceLogsByRangeResponseMsg }
     *     
     */
    public GetMachinePerformanceLogsByRangeResponseMsg getGetMachinePerformanceLogsByRangeResponse() {
        return getMachinePerformanceLogsByRangeResponse;
    }

    /**
     * Sets the value of the getMachinePerformanceLogsByRangeResponse property.
     * 
     * @param value
     *     allowed object is
     *     {@link GetMachinePerformanceLogsByRangeResponseMsg }
     *     
     */
    public void setGetMachinePerformanceLogsByRangeResponse(GetMachinePerformanceLogsByRangeResponseMsg value) {
        this.getMachinePerformanceLogsByRangeResponse = value;
    }

    public boolean isSetGetMachinePerformanceLogsByRangeResponse() {
        return (this.getMachinePerformanceLogsByRangeResponse!= null);
    }

}
