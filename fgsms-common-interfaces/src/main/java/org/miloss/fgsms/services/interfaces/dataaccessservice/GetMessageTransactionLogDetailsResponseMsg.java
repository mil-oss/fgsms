
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
import org.miloss.fgsms.services.interfaces.common.Header;
import org.miloss.fgsms.services.interfaces.common.SecurityWrapper;


/**
 * response message
 * 
 * <p>Java class for GetMessageTransactionLogDetailsResponseMsg complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetMessageTransactionLogDetailsResponseMsg">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="classification" type="{urn:org:miloss:fgsms:services:interfaces:common}SecurityWrapper"/>
 *         &lt;element name="Action" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Identity" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded"/>
 *         &lt;element name="IsFault" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="IsSLAFault" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="slaFaultMsg" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="MonitorHostname" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ResponseTime" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="ServiceHostname" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="XmlRequestMessage" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="XmlResponseMessage" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="requestSize" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="responseSize" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="timestamp" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="transactionId" type="{urn:org:miloss:fgsms:services:interfaces:dataAccessService}guid"/>
 *         &lt;element name="transactionthreadId" type="{urn:org:miloss:fgsms:services:interfaces:dataAccessService}guid"/>
 *         &lt;element name="relatedTransactionID" type="{urn:org:miloss:fgsms:services:interfaces:dataAccessService}guid"/>
 *         &lt;element name="headersRequest" type="{urn:org:miloss:fgsms:services:interfaces:common}header" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="headersResponse" type="{urn:org:miloss:fgsms:services:interfaces:common}header" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="agentType" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="agentMemo" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="OriginalRequestURL" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="CorrectedURL" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetMessageTransactionLogDetailsResponseMsg", propOrder = {
    "classification",
    "action",
    "identity",
    "isFault",
    "isSLAFault",
    "slaFaultMsg",
    "monitorHostname",
    "responseTime",
    "serviceHostname",
    "xmlRequestMessage",
    "xmlResponseMessage",
    "requestSize",
    "responseSize",
    "timestamp",
    "transactionId",
    "transactionthreadId",
    "relatedTransactionID",
    "headersRequest",
    "headersResponse",
    "agentType",
    "agentMemo",
    "originalRequestURL",
    "correctedURL"
})
public class GetMessageTransactionLogDetailsResponseMsg
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(required = true, nillable = true)
    protected SecurityWrapper classification;
    @XmlElement(name = "Action", required = true, nillable = true)
    protected String action;
    @XmlElement(name = "Identity", required = false, nillable = true)
    protected List<String> identity;
    @XmlElement(name = "IsFault")
    protected boolean isFault;
    @XmlElement(name = "IsSLAFault")
    protected boolean isSLAFault;
    @XmlElement(required = true, nillable = true)
    protected String slaFaultMsg;
    @XmlElement(name = "MonitorHostname", required = true, nillable = true)
    protected String monitorHostname;
    @XmlElement(name = "ResponseTime")
    protected long responseTime;
    @XmlElement(name = "ServiceHostname", required = true, nillable = true)
    protected String serviceHostname;
    @XmlElement(name = "XmlRequestMessage", required = false, nillable = true)
    protected String xmlRequestMessage;
    @XmlElement(name = "XmlResponseMessage", required = false, nillable = true)
    protected String xmlResponseMessage;
    protected long requestSize;
    protected long responseSize;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected Calendar timestamp;
    @XmlElement(required = true)
    protected String transactionId;
    @XmlElement(required = true, nillable = true)
    protected String transactionthreadId;
    @XmlElement(required = true, nillable = true)
    protected String relatedTransactionID;
    protected List<Header> headersRequest;
    protected List<Header> headersResponse;
    @XmlElement(required = true, nillable = true)
    protected String agentType;
    @XmlElement(required = true, nillable = true)
    protected String agentMemo;
    @XmlElement(name = "OriginalRequestURL", required = false, nillable = true)
    protected String originalRequestURL;
    @XmlElement(name = "CorrectedURL", required = false, nillable = true)
    protected String correctedURL;

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
     * Gets the value of the xmlRequestMessage property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getXmlRequestMessage() {
        return xmlRequestMessage;
    }

    /**
     * Sets the value of the xmlRequestMessage property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setXmlRequestMessage(String value) {
        this.xmlRequestMessage = value;
    }

    public boolean isSetXmlRequestMessage() {
        return (this.xmlRequestMessage!= null);
    }

    /**
     * Gets the value of the xmlResponseMessage property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getXmlResponseMessage() {
        return xmlResponseMessage;
    }

    /**
     * Sets the value of the xmlResponseMessage property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setXmlResponseMessage(String value) {
        this.xmlResponseMessage = value;
    }

    public boolean isSetXmlResponseMessage() {
        return (this.xmlResponseMessage!= null);
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

    /**
     * Gets the value of the transactionthreadId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTransactionthreadId() {
        return transactionthreadId;
    }

    /**
     * Sets the value of the transactionthreadId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTransactionthreadId(String value) {
        this.transactionthreadId = value;
    }

    public boolean isSetTransactionthreadId() {
        return (this.transactionthreadId!= null);
    }

    /**
     * Gets the value of the relatedTransactionID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRelatedTransactionID() {
        return relatedTransactionID;
    }

    /**
     * Sets the value of the relatedTransactionID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRelatedTransactionID(String value) {
        this.relatedTransactionID = value;
    }

    public boolean isSetRelatedTransactionID() {
        return (this.relatedTransactionID!= null);
    }

    /**
     * Gets the value of the headersRequest property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the headersRequest property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getHeadersRequest().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Header }
     * 
     * 
     */
    public List<Header> getHeadersRequest() {
        if (headersRequest == null) {
            headersRequest = new ArrayList<Header>();
        }
        return this.headersRequest;
    }

    public boolean isSetHeadersRequest() {
        return ((this.headersRequest!= null)&&(!this.headersRequest.isEmpty()));
    }

    public void unsetHeadersRequest() {
        this.headersRequest = null;
    }

    /**
     * Gets the value of the headersResponse property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the headersResponse property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getHeadersResponse().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Header }
     * 
     * 
     */
    public List<Header> getHeadersResponse() {
        if (headersResponse == null) {
            headersResponse = new ArrayList<Header>();
        }
        return this.headersResponse;
    }

    public boolean isSetHeadersResponse() {
        return ((this.headersResponse!= null)&&(!this.headersResponse.isEmpty()));
    }

    public void unsetHeadersResponse() {
        this.headersResponse = null;
    }

    /**
     * Gets the value of the agentType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAgentType() {
        return agentType;
    }

    /**
     * Sets the value of the agentType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAgentType(String value) {
        this.agentType = value;
    }

    public boolean isSetAgentType() {
        return (this.agentType!= null);
    }

    /**
     * Gets the value of the agentMemo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAgentMemo() {
        return agentMemo;
    }

    /**
     * Sets the value of the agentMemo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAgentMemo(String value) {
        this.agentMemo = value;
    }

    public boolean isSetAgentMemo() {
        return (this.agentMemo!= null);
    }

    /**
     * Gets the value of the originalRequestURL property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOriginalRequestURL() {
        return originalRequestURL;
    }

    /**
     * Sets the value of the originalRequestURL property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOriginalRequestURL(String value) {
        this.originalRequestURL = value;
    }

    public boolean isSetOriginalRequestURL() {
        return (this.originalRequestURL!= null);
    }

    /**
     * Gets the value of the correctedURL property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCorrectedURL() {
        return correctedURL;
    }

    /**
     * Sets the value of the correctedURL property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCorrectedURL(String value) {
        this.correctedURL = value;
    }

    public boolean isSetCorrectedURL() {
        return (this.correctedURL!= null);
    }

}
