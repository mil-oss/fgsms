
package org.miloss.fgsms.services.interfaces.dataaccessservice;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.Duration;
import org.miloss.fgsms.services.interfaces.common.SecurityWrapper;


/**
 * 
 *             GetPerformanceStats Response
 *             
 * 
 * <p>Java class for GetPerformanceAverageStatsResponseMsg complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetPerformanceAverageStatsResponseMsg">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="classification" type="{urn:org:miloss:fgsms:services:interfaces:common}SecurityWrapper"/>
 *         &lt;element name="AverageResponseTime" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="FailingInvocations" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="SuccessfulInvocations" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="ServiceLevelAgreementViolations" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="MTBF" type="{http://www.w3.org/2001/XMLSchema}duration"/>
 *         &lt;element name="URL" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="DisplayName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetPerformanceAverageStatsResponseMsg", propOrder = {
    "classification",
    "averageResponseTime",
    "failingInvocations",
    "successfulInvocations",
    "serviceLevelAgreementViolations",
    "mtbf",
    "url",
    "displayName"
})
public class GetPerformanceAverageStatsResponseMsg
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(required = true, nillable = true)
    protected SecurityWrapper classification;
    @XmlElement(name = "AverageResponseTime")
    protected long averageResponseTime;
    @XmlElement(name = "FailingInvocations")
    protected long failingInvocations;
    @XmlElement(name = "SuccessfulInvocations")
    protected long successfulInvocations;
    @XmlElement(name = "ServiceLevelAgreementViolations")
    protected long serviceLevelAgreementViolations;
    @XmlElement(name = "MTBF", required = false, nillable = true)
    protected Duration mtbf;
    @XmlElement(name = "URL", required = false)
    protected String url;
    @XmlElement(name = "DisplayName", required = false)
    protected String displayName;

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
     * Gets the value of the averageResponseTime property.
     * 
     */
    public long getAverageResponseTime() {
        return averageResponseTime;
    }

    /**
     * Sets the value of the averageResponseTime property.
     * 
     */
    public void setAverageResponseTime(long value) {
        this.averageResponseTime = value;
    }

    public boolean isSetAverageResponseTime() {
        return true;
    }

    /**
     * Gets the value of the failingInvocations property.
     * 
     */
    public long getFailingInvocations() {
        return failingInvocations;
    }

    /**
     * Sets the value of the failingInvocations property.
     * 
     */
    public void setFailingInvocations(long value) {
        this.failingInvocations = value;
    }

    public boolean isSetFailingInvocations() {
        return true;
    }

    /**
     * Gets the value of the successfulInvocations property.
     * 
     */
    public long getSuccessfulInvocations() {
        return successfulInvocations;
    }

    /**
     * Sets the value of the successfulInvocations property.
     * 
     */
    public void setSuccessfulInvocations(long value) {
        this.successfulInvocations = value;
    }

    public boolean isSetSuccessfulInvocations() {
        return true;
    }

    /**
     * Gets the value of the serviceLevelAgreementViolations property.
     * 
     */
    public long getServiceLevelAgreementViolations() {
        return serviceLevelAgreementViolations;
    }

    /**
     * Sets the value of the serviceLevelAgreementViolations property.
     * 
     */
    public void setServiceLevelAgreementViolations(long value) {
        this.serviceLevelAgreementViolations = value;
    }

    public boolean isSetServiceLevelAgreementViolations() {
        return true;
    }

    /**
     * Gets the value of the mtbf property.
     * 
     * @return
     *     possible object is
     *     {@link Duration }
     *     
     */
    public Duration getMTBF() {
        return mtbf;
    }

    /**
     * Sets the value of the mtbf property.
     * 
     * @param value
     *     allowed object is
     *     {@link Duration }
     *     
     */
    public void setMTBF(Duration value) {
        this.mtbf = value;
    }

    public boolean isSetMTBF() {
        return (this.mtbf!= null);
    }

    /**
     * Gets the value of the url property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getURL() {
        return url;
    }

    /**
     * Sets the value of the url property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setURL(String value) {
        this.url = value;
    }

    public boolean isSetURL() {
        return (this.url!= null);
    }

    /**
     * Gets the value of the displayName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Sets the value of the displayName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDisplayName(String value) {
        this.displayName = value;
    }

    public boolean isSetDisplayName() {
        return (this.displayName!= null);
    }

}
