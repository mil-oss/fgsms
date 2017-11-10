
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
 *         &lt;element name="PurgePerformanceDataResult" type="{urn:org:miloss:fgsms:services:interfaces:dataAccessService}PurgePerformanceDataResponseMsg"/>
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
    "purgePerformanceDataResult"
})
@XmlRootElement(name = "PurgePerformanceDataResponse")
public class PurgePerformanceDataResponse
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "PurgePerformanceDataResult", required = true, nillable = true)
    protected PurgePerformanceDataResponseMsg purgePerformanceDataResult;

    /**
     * Gets the value of the purgePerformanceDataResult property.
     * 
     * @return
     *     possible object is
     *     {@link PurgePerformanceDataResponseMsg }
     *     
     */
    public PurgePerformanceDataResponseMsg getPurgePerformanceDataResult() {
        return purgePerformanceDataResult;
    }

    /**
     * Sets the value of the purgePerformanceDataResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link PurgePerformanceDataResponseMsg }
     *     
     */
    public void setPurgePerformanceDataResult(PurgePerformanceDataResponseMsg value) {
        this.purgePerformanceDataResult = value;
    }

    public boolean isSetPurgePerformanceDataResult() {
        return (this.purgePerformanceDataResult!= null);
    }

}
