
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
 *         &lt;element name="GetProcessPerformanceLogsByRangeRequest" type="{urn:org:miloss:fgsms:services:interfaces:dataAccessService}GetProcessPerformanceLogsByRangeRequestMsg"/>
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
    "getProcessPerformanceLogsByRangeRequest"
})
@XmlRootElement(name = "GetProcessPerformanceLogsByRange")
public class GetProcessPerformanceLogsByRange
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "GetProcessPerformanceLogsByRangeRequest", required = true)
    protected GetProcessPerformanceLogsByRangeRequestMsg getProcessPerformanceLogsByRangeRequest;

    /**
     * Gets the value of the getProcessPerformanceLogsByRangeRequest property.
     * 
     * @return
     *     possible object is
     *     {@link GetProcessPerformanceLogsByRangeRequestMsg }
     *     
     */
    public GetProcessPerformanceLogsByRangeRequestMsg getGetProcessPerformanceLogsByRangeRequest() {
        return getProcessPerformanceLogsByRangeRequest;
    }

    /**
     * Sets the value of the getProcessPerformanceLogsByRangeRequest property.
     * 
     * @param value
     *     allowed object is
     *     {@link GetProcessPerformanceLogsByRangeRequestMsg }
     *     
     */
    public void setGetProcessPerformanceLogsByRangeRequest(GetProcessPerformanceLogsByRangeRequestMsg value) {
        this.getProcessPerformanceLogsByRangeRequest = value;
    }

    public boolean isSetGetProcessPerformanceLogsByRangeRequest() {
        return (this.getProcessPerformanceLogsByRangeRequest!= null);
    }

}
