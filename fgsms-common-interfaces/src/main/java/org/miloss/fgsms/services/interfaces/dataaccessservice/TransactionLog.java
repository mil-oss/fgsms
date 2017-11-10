
package org.miloss.fgsms.services.interfaces.dataaccessservice;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import java.util.Calendar;


/**
 * 
 *             Message Transaction Log
 *             
 * 
 * <p>Java class for TransactionLog complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TransactionLog">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Action" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="URL" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Identity" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded"/>
 *         &lt;element name="IsFault" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="IsSLAFault" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="slaFaultMsg" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="MonitorHostname" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ResponseTime" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="ServiceHostname" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="hasRequestMessage" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="hasResponseMessage" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="requestSize" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="responseSize" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="timestamp" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="transactionId" type="{urn:org:miloss:fgsms:services:interfaces:dataAccessService}guid"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TransactionLog", propOrder = {
    "action",
    "url",
    "identity",
    "isFault",
    "isSLAFault",
    "slaFaultMsg",
    "monitorHostname",
    "responseTime",
    "serviceHostname",
    "hasRequestMessage",
    "hasResponseMessage",
    "requestSize",
    "responseSize",
    "timestamp",
    "transactionId"
})
public class TransactionLog
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "Action", required = true, nillable = true)
    protected String action;
    @XmlElement(name = "URL", required = false)
    protected String url;
    @XmlElement(name = "Identity", required = false)
    protected List<String> identity;
    @XmlElement(name = "IsFault")
    protected boolean isFault;
    @XmlElement(name = "IsSLAFault")
    protected boolean isSLAFault;
    @XmlElement(required = false, nillable = true)
    protected String slaFaultMsg;
    @XmlElement(name = "MonitorHostname", required = false, nillable = true)
    protected String monitorHostname;
    @XmlElement(name = "ResponseTime")
    protected long responseTime;
    @XmlElement(name = "ServiceHostname", required = false, nillable = true)
    protected String serviceHostname;
    protected boolean hasRequestMessage;
    protected boolean hasResponseMessage;
    protected long requestSize;
    protected long responseSize;
    @XmlElement(required = false)
    @XmlSchemaType(name = "dateTime")
    protected Calendar timestamp;
    @XmlElement(required = true)
    protected String transactionId;

    /**
     * Gets the value of the action property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAction() {
        return action;
    }

    /**
     * Sets the value of the action property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAction(String value) {
        this.action = value;
    }

    public boolean isSetAction() {
        return (this.action!= null);
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
     * Gets the value of the identity property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the identity property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getIdentity().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getIdentity() {
        if (identity == null) {
            identity = new ArrayList<String>();
        }
        return this.identity;
    }

    public boolean isSetIdentity() {
        return ((this.identity!= null)&&(!this.identity.isEmpty()));
    }

    public void unsetIdentity() {
        this.identity = null;
    }

    /**
     * Gets the value of the isFault property.
     * 
     */
    public boolean isIsFault() {
        return isFault;
    }

    /**
     * Sets the value of the isFault property.
     * 
     */
    public void setIsFault(boolean value) {
        this.isFault = value;
    }

    public boolean isSetIsFault() {
        return true;
    }

    /**
     * Gets the value of the isSLAFault property.
     * 
     */
    public boolean isIsSLAFault() {
        return isSLAFault;
    }

    /**
     * Sets the value of the isSLAFault property.
     * 
     */
    public void setIsSLAFault(boolean value) {
        this.isSLAFault = value;
    }

    public boolean isSetIsSLAFault() {
        return true;
    }

    /**
     * Gets the value of the slaFaultMsg property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSlaFaultMsg() {
        return slaFaultMsg;
    }

    /**
     * Sets the value of the slaFaultMsg property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSlaFaultMsg(String value) {
        this.slaFaultMsg = value;
    }

    public boolean isSetSlaFaultMsg() {
        return (this.slaFaultMsg!= null);
    }

    /**
     * Gets the value of the monitorHostname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMonitorHostname() {
        return monitorHostname;
    }

    /**
     * Sets the value of the monitorHostname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMonitorHostname(String value) {
        this.monitorHostname = value;
    }

    public boolean isSetMonitorHostname() {
        return (this.monitorHostname!= null);
    }

    /**
     * Gets the value of the responseTime property.
     * 
     */
    public long getResponseTime() {
        return responseTime;
    }

    /**
     * Sets the value of the responseTime property.
     * 
     */
    public void setResponseTime(long value) {
        this.responseTime = value;
    }

    public boolean isSetResponseTime() {
        return true;
    }

    /**
     * Gets the value of the serviceHostname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getServiceHostname() {
        return serviceHostname;
    }

    /**
     * Sets the value of the serviceHostname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setServiceHostname(String value) {
        this.serviceHostname = value;
    }

    public boolean isSetServiceHostname() {
        return (this.serviceHostname!= null);
    }

    /**
     * Gets the value of the hasRequestMessage property.
     * 
     */
    public boolean isHasRequestMessage() {
        return hasRequestMessage;
    }

    /**
     * Sets the value of the hasRequestMessage property.
     * 
     */
    public void setHasRequestMessage(boolean value) {
        this.hasRequestMessage = value;
    }

    public boolean isSetHasRequestMessage() {
        return true;
    }

    /**
     * Gets the value of the hasResponseMessage property.
     * 
     */
    public boolean isHasResponseMessage() {
        return hasResponseMessage;
    }

    /**
     * Sets the value of the hasResponseMessage property.
     * 
     */
    public void setHasResponseMessage(boolean value) {
        this.hasResponseMessage = value;
    }

    public boolean isSetHasResponseMessage() {
        return true;
    }

    /**
     * Gets the value of the requestSize property.
     * 
     */
    public long getRequestSize() {
        return requestSize;
    }

    /**
     * Sets the value of the requestSize property.
     * 
     */
    public void setRequestSize(long value) {
        this.requestSize = value;
    }

    public boolean isSetRequestSize() {
        return true;
    }

    /**
     * Gets the value of the responseSize property.
     * 
     */
    public long getResponseSize() {
        return responseSize;
    }

    /**
     * Sets the value of the responseSize property.
     * 
     */
    public void setResponseSize(long value) {
        this.responseSize = value;
    }

    public boolean isSetResponseSize() {
        return true;
    }

    /**
     * Gets the value of the timestamp property.
     * 
     * @return
     *     possible object is
     *     {@link Calendar }
     *     
     */
    public Calendar getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the value of the timestamp property.
     * 
     * @param value
     *     allowed object is
     *     {@link Calendar }
     *     
     */
    public void setTimestamp(Calendar value) {
        this.timestamp = value;
    }

    public boolean isSetTimestamp() {
        return (this.timestamp!= null);
    }

    /**
     * Gets the value of the transactionId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTransactionId() {
        return transactionId;
    }

    /**
     * Sets the value of the transactionId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTransactionId(String value) {
        this.transactionId = value;
    }

    public boolean isSetTransactionId() {
        return (this.transactionId!= null);
    }

}
