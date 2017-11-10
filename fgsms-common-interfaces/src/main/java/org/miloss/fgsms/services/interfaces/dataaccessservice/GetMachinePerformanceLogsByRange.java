
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
 *         &lt;element name="GetMachinePerformanceLogsByRangeRequest" type="{urn:org:miloss:fgsms:services:interfaces:dataAccessService}GetMachinePerformanceLogsByRangeRequestMsg"/>
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
    "getMachinePerformanceLogsByRangeRequest"
})
@XmlRootElement(name = "GetMachinePerformanceLogsByRange")
public class GetMachinePerformanceLogsByRange
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "GetMachinePerformanceLogsByRangeRequest", required = true)
    protected GetMachinePerformanceLogsByRangeRequestMsg getMachinePerformanceLogsByRangeRequest;

    /**
     * Gets the value of the getMachinePerformanceLogsByRangeRequest property.
     * 
     * @return
     *     possible object is
     *     {@link GetMachinePerformanceLogsByRangeRequestMsg }
     *     
     */
    public GetMachinePerformanceLogsByRangeRequestMsg getGetMachinePerformanceLogsByRangeRequest() {
        return getMachinePerformanceLogsByRangeRequest;
    }

    /**
     * Sets the value of the getMachinePerformanceLogsByRangeRequest property.
     * 
     * @param value
     *     allowed object is
     *     {@link GetMachinePerformanceLogsByRangeRequestMsg }
     *     
     */
    public void setGetMachinePerformanceLogsByRangeRequest(GetMachinePerformanceLogsByRangeRequestMsg value) {
        this.getMachinePerformanceLogsByRangeRequest = value;
    }

    public boolean isSetGetMachinePerformanceLogsByRangeRequest() {
        return (this.getMachinePerformanceLogsByRangeRequest!= null);
    }

}
