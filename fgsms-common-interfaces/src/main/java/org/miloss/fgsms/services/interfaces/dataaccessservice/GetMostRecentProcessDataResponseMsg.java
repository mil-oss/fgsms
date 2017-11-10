
package org.miloss.fgsms.services.interfaces.dataaccessservice;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.miloss.fgsms.services.interfaces.common.ProcessPerformanceData;
import org.miloss.fgsms.services.interfaces.common.SecurityWrapper;


/**
 * response message
 * 
 * <p>Java class for GetMostRecentProcessDataResponseMsg complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetMostRecentProcessDataResponseMsg">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="classification" type="{urn:org:miloss:fgsms:services:interfaces:common}SecurityWrapper"/>
 *         &lt;element name="PerformanceData" type="{urn:org:miloss:fgsms:services:interfaces:common}ProcessPerformanceData"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetMostRecentProcessDataResponseMsg", propOrder = {
    "classification",
    "performanceData"
})
public class GetMostRecentProcessDataResponseMsg
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(required = true, nillable = true)
    protected SecurityWrapper classification;
    @XmlElement(name = "PerformanceData", required = true, nillable = true)
    protected ProcessPerformanceData performanceData;

    /**
     * Gets the value of the classification property.
     * 
     * @return
     *     possible object is
     *     {@link SecurityWrapper }
     *     
     */
    public SecurityWrapper getClassification() {
        return classification;
    }

    /**
     * Sets the value of the classification property.
     * 
     * @param value
     *     allowed object is
     *     {@link SecurityWrapper }
     *     
     */
    public void setClassification(SecurityWrapper value) {
        this.classification = value;
    }

    public boolean isSetClassification() {
        return (this.classification!= null);
    }

    /**
     * Gets the value of the performanceData property.
     * 
     * @return
     *     possible object is
     *     {@link ProcessPerformanceData }
     *     
     */
    public ProcessPerformanceData getPerformanceData() {
        return performanceData;
    }

    /**
     * Sets the value of the performanceData property.
     * 
     * @param value
     *     allowed object is
     *     {@link ProcessPerformanceData }
     *     
     */
    public void setPerformanceData(ProcessPerformanceData value) {
        this.performanceData = value;
    }

    public boolean isSetPerformanceData() {
        return (this.performanceData!= null);
    }

}
