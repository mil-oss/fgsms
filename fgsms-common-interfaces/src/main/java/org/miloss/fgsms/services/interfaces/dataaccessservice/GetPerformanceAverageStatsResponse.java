
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
 *         &lt;element name="GetPerformanceAverageStatsResult" type="{urn:org:miloss:fgsms:services:interfaces:dataAccessService}GetPerformanceAverageStatsResponseMsg"/>
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
    "getPerformanceAverageStatsResult"
})
@XmlRootElement(name = "GetPerformanceAverageStatsResponse")
public class GetPerformanceAverageStatsResponse
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "GetPerformanceAverageStatsResult", required = true, nillable = true)
    protected GetPerformanceAverageStatsResponseMsg getPerformanceAverageStatsResult;

    /**
     * Gets the value of the getPerformanceAverageStatsResult property.
     * 
     * @return
     *     possible object is
     *     {@link GetPerformanceAverageStatsResponseMsg }
     *     
     */
    public GetPerformanceAverageStatsResponseMsg getGetPerformanceAverageStatsResult() {
        return getPerformanceAverageStatsResult;
    }

    /**
     * Sets the value of the getPerformanceAverageStatsResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link GetPerformanceAverageStatsResponseMsg }
     *     
     */
    public void setGetPerformanceAverageStatsResult(GetPerformanceAverageStatsResponseMsg value) {
        this.getPerformanceAverageStatsResult = value;
    }

    public boolean isSetGetPerformanceAverageStatsResult() {
        return (this.getPerformanceAverageStatsResult!= null);
    }

}
