
package org.miloss.fgsms.services.interfaces.policyconfiguration;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.Duration;
import org.miloss.fgsms.services.interfaces.common.SecurityWrapper;


/**
 * 
 *                 Represents all available global policies for this realm
 *             
 * 
 * <p>Java class for GlobalPolicy complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GlobalPolicy">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="classification" type="{urn:org:miloss:fgsms:services:interfaces:common}SecurityWrapper"/>
 *         &lt;element name="PolicyRefreshRate" type="{http://www.w3.org/2001/XMLSchema}duration"/>
 *         &lt;element name="RecordedMessageCap" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="UDDIPublishRate" type="{http://www.w3.org/2001/XMLSchema}duration" minOccurs="0"/>
 *         &lt;element name="AgentsEnabled" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GlobalPolicy", propOrder = {
    "classification",
    "policyRefreshRate",
    "recordedMessageCap",
    "uddiPublishRate",
    "agentsEnabled"
})
public class GlobalPolicy
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(required = true, nillable = true)
    protected SecurityWrapper classification;
    @XmlElement(name = "PolicyRefreshRate", required = true)
    protected Duration policyRefreshRate;
    @XmlElement(name = "RecordedMessageCap")
    protected int recordedMessageCap;
    @XmlElement(name = "UDDIPublishRate")
    protected Duration uddiPublishRate;
    @XmlElement(name = "AgentsEnabled")
    protected Boolean agentsEnabled;

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
     * Gets the value of the policyRefreshRate property.
     * 
     * @return
     *     possible object is
     *     {@link Duration }
     *     
     */
    public Duration getPolicyRefreshRate() {
        return policyRefreshRate;
    }

    /**
     * Sets the value of the policyRefreshRate property.
     * 
     * @param value
     *     allowed object is
     *     {@link Duration }
     *     
     */
    public void setPolicyRefreshRate(Duration value) {
        this.policyRefreshRate = value;
    }

    public boolean isSetPolicyRefreshRate() {
        return (this.policyRefreshRate!= null);
    }

    /**
     * Gets the value of the recordedMessageCap property.
     * 
     */
    public int getRecordedMessageCap() {
        return recordedMessageCap;
    }

    /**
     * Sets the value of the recordedMessageCap property.
     * 
     */
    public void setRecordedMessageCap(int value) {
        this.recordedMessageCap = value;
    }

    public boolean isSetRecordedMessageCap() {
        return true;
    }

    /**
     * Gets the value of the uddiPublishRate property.
     * 
     * @return
     *     possible object is
     *     {@link Duration }
     *     
     */
    public Duration getUDDIPublishRate() {
        return uddiPublishRate;
    }

    /**
     * Sets the value of the uddiPublishRate property.
     * 
     * @param value
     *     allowed object is
     *     {@link Duration }
     *     
     */
    public void setUDDIPublishRate(Duration value) {
        this.uddiPublishRate = value;
    }

    public boolean isSetUDDIPublishRate() {
        return (this.uddiPublishRate!= null);
    }

    /**
     * Gets the value of the agentsEnabled property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isAgentsEnabled() {
        return agentsEnabled;
    }

    /**
     * Sets the value of the agentsEnabled property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAgentsEnabled(Boolean value) {
        this.agentsEnabled = value;
    }

    public boolean isSetAgentsEnabled() {
        return (this.agentsEnabled!= null);
    }

}
