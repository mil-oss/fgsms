
package org.miloss.fgsms.services.interfaces.policyconfiguration;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for TransactionalWebServicePolicy complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TransactionalWebServicePolicy">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:org:miloss:fgsms:services:interfaces:policyConfiguration}ServicePolicy">
 *       &lt;sequence>
 *         &lt;element name="RecordFaultsOnly" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="RecordRequestMessage" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="RecordResponseMessage" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="RecordedMessageCap" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="RecordHeaders" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="BuellerEnabled" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="HealthStatusEnabled" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="UserIdentification" type="{urn:org:miloss:fgsms:services:interfaces:policyConfiguration}ArrayOfUserIdentity"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TransactionalWebServicePolicy", propOrder = {
    "recordFaultsOnly",
    "recordRequestMessage",
    "recordResponseMessage",
    "recordedMessageCap",
    "recordHeaders",
    "buellerEnabled",
    "healthStatusEnabled",
    "userIdentification"
})
@XmlRootElement
public class TransactionalWebServicePolicy
    extends ServicePolicy
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "RecordFaultsOnly")
    protected boolean recordFaultsOnly;
    @XmlElement(name = "RecordRequestMessage")
    protected boolean recordRequestMessage;
    @XmlElement(name = "RecordResponseMessage")
    protected boolean recordResponseMessage;
    @XmlElement(name = "RecordedMessageCap")
    protected int recordedMessageCap;
    @XmlElement(name = "RecordHeaders", defaultValue = "true")
    protected boolean recordHeaders;
    @XmlElement(name = "BuellerEnabled")
    protected boolean buellerEnabled;
    @Deprecated
    @XmlElement(name = "HealthStatusEnabled")
    protected boolean healthStatusEnabled;
    @XmlElement(name = "UserIdentification", required = false, nillable = true)
    protected ArrayOfUserIdentity userIdentification;

    /**
     * Gets the value of the recordFaultsOnly property.
     * 
     */
    public boolean isRecordFaultsOnly() {
        return recordFaultsOnly;
    }

    /**
     * Sets the value of the recordFaultsOnly property.
     * 
     */
    public void setRecordFaultsOnly(boolean value) {
        this.recordFaultsOnly = value;
    }

    public boolean isSetRecordFaultsOnly() {
        return true;
    }

    /**
     * Gets the value of the recordRequestMessage property.
     * 
     */
    public boolean isRecordRequestMessage() {
        return recordRequestMessage;
    }

    /**
     * Sets the value of the recordRequestMessage property.
     * 
     */
    public void setRecordRequestMessage(boolean value) {
        this.recordRequestMessage = value;
    }

    public boolean isSetRecordRequestMessage() {
        return true;
    }

    /**
     * Gets the value of the recordResponseMessage property.
     * 
     */
    public boolean isRecordResponseMessage() {
        return recordResponseMessage;
    }

    /**
     * Sets the value of the recordResponseMessage property.
     * 
     */
    public void setRecordResponseMessage(boolean value) {
        this.recordResponseMessage = value;
    }

    public boolean isSetRecordResponseMessage() {
        return true;
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
     * Gets the value of the recordHeaders property.
     * 
     */
    public boolean isRecordHeaders() {
        return recordHeaders;
    }

    /**
     * Sets the value of the recordHeaders property.
     * 
     */
    public void setRecordHeaders(boolean value) {
        this.recordHeaders = value;
    }

    public boolean isSetRecordHeaders() {
        return true;
    }

    /**
     * Gets the value of the buellerEnabled property.
     * 
     */
    public boolean isBuellerEnabled() {
        return buellerEnabled;
    }

    /**
     * Sets the value of the buellerEnabled property.
     * 
     */
    public void setBuellerEnabled(boolean value) {
        this.buellerEnabled = value;
    }

    public boolean isSetBuellerEnabled() {
        return true;
    }

    /**
     * Gets the value of the healthStatusEnabled property.
     * 
     */
    @Deprecated
    public boolean isHealthStatusEnabled() {
        return healthStatusEnabled;
    }

    /**
     * Sets the value of the healthStatusEnabled property.
     * 
     */
    @Deprecated
    public void setHealthStatusEnabled(boolean value) {
        this.healthStatusEnabled = value;
    }

    public boolean isSetHealthStatusEnabled() {
        return true;
    }

    /**
     * Gets the value of the userIdentification property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfUserIdentity }
     *     
     */
    public ArrayOfUserIdentity getUserIdentification() {
        return userIdentification;
    }

    /**
     * Sets the value of the userIdentification property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfUserIdentity }
     *     
     */
    public void setUserIdentification(ArrayOfUserIdentity value) {
        this.userIdentification = value;
    }

    public boolean isSetUserIdentification() {
        return (this.userIdentification!= null);
    }

}
