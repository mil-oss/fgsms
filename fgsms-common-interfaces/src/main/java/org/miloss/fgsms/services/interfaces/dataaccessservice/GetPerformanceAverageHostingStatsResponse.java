
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
 *         &lt;element name="GetPerformanceAverageHostingStatsResult" type="{urn:org:miloss:fgsms:services:interfaces:dataAccessService}GetPerformanceAverageStatsResponseMsg"/>
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
    "getPerformanceAverageHostingStatsResult"
})
@XmlRootElement(name = "GetPerformanceAverageHostingStatsResponse")
public class GetPerformanceAverageHostingStatsResponse
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "GetPerformanceAverageHostingStatsResult", required = true, nillable = true)
    protected GetPerformanceAverageStatsResponseMsg getPerformanceAverageHostingStatsResult;

    /**
     * Gets the value of the getPerformanceAverageHostingStatsResult property.
     * 
     * @return
     *     possible object is
     *     {@link GetPerformanceAverageStatsResponseMsg }
     *     
     */
    public GetPerformanceAverageStatsResponseMsg getGetPerformanceAverageHostingStatsResult() {
        return getPerformanceAverageHostingStatsResult;
    }

    /**
     * Sets the value of the getPerformanceAverageHostingStatsResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link GetPerformanceAverageStatsResponseMsg }
     *     
     */
    public void setGetPerformanceAverageHostingStatsResult(GetPerformanceAverageStatsResponseMsg value) {
        this.getPerformanceAverageHostingStatsResult = value;
    }

    public boolean isSetGetPerformanceAverageHostingStatsResult() {
        return (this.getPerformanceAverageHostingStatsResult!= null);
    }

}
