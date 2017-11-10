
package org.miloss.fgsms.services.interfaces.datacollector;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import java.util.Calendar;
import org.miloss.fgsms.services.interfaces.common.Header;
import org.miloss.fgsms.services.interfaces.common.SecurityWrapper;


/**
 * request
 * 
 * <p>Java class for AddDataRequestMsg complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AddDataRequestMsg">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="classification" type="{urn:org:miloss:fgsms:services:interfaces:common}SecurityWrapper"/>
 *         &lt;element name="Action" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Message" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="TransactionID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="RelatedTransactionID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="TransactionThreadID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Identity" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded"/>
 *         &lt;element name="ServiceHost" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Success" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="URI" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="RequestURI" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="XmlRequest" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="XmlResponse" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="requestSize" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="responseSize" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="responseTime" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="agentType" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="recordedat" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="headersRequest" type="{urn:org:miloss:fgsms:services:interfaces:common}header" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="headersResponse" type="{urn:org:miloss:fgsms:services:interfaces:common}header" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AddDataRequestMsg", propOrder = {
    "classification",
    "action",
    "message",
    "transactionID",
    "relatedTransactionID",
    "transactionThreadID",
    "identity",
    "serviceHost",
    "success",
    "uri",
    "requestURI",
    "xmlRequest",
    "xmlResponse",
    "requestSize",
    "responseSize",
    "responseTime",
    "agentType",
    "recordedat",
    "headersRequest",
    "headersResponse"
})
@XmlRootElement
public class AddDataRequestMsg
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(required = true, nillable = true)
    protected SecurityWrapper classification;
    @XmlElement(name = "Action", required = true, nillable = true)
    protected String action;
    @XmlElement(name = "Message", required = true, nillable = true)
    protected String message;
    @XmlElement(name = "TransactionID", required = true, nillable = true)
    protected String transactionID;
    @XmlElement(name = "RelatedTransactionID", required = true, nillable = true)
    protected String relatedTransactionID;
    @XmlElement(name = "TransactionThreadID", required = true, nillable = true)
    protected String transactionThreadID;
    @XmlElement(name = "Identity", required = false, nillable = true)
    protected List<String> identity;
    @XmlElement(name = "ServiceHost", required = true, nillable = true)
    protected String serviceHost;
    @XmlElement(name = "Success")
    protected boolean success;
    @XmlElement(name = "URI", required = true, nillable = true)
    protected String uri;
    @XmlElement(name = "RequestURI", required = true, nillable = true)
    protected String requestURI;
    @XmlElement(name = "XmlRequest", required = false, nillable = true)
    protected String xmlRequest;
    @XmlElement(name = "XmlResponse", required = false, nillable = true)
    protected String xmlResponse;
    protected int requestSize;
    protected int responseSize;
    protected int responseTime;
    @XmlElement(required = true, nillable = true)
    protected String agentType;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected Calendar recordedat;
    protected List<Header> headersRequest;
    protected List<Header> headersResponse;

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
     * Gets the value of the message property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the value of the message property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMessage(String value) {
        this.message = value;
    }

    public boolean isSetMessage() {
        return (this.message!= null);
    }

    /**
     * Gets the value of the transactionID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTransactionID() {
        return transactionID;
    }

    /**
     * Sets the value of the transactionID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTransactionID(String value) {
        this.transactionID = value;
    }

    public boolean isSetTransactionID() {
        return (this.transactionID!= null);
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
     * Gets the value of the transactionThreadID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTransactionThreadID() {
        return transactionThreadID;
    }

    /**
     * Sets the value of the transactionThreadID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTransactionThreadID(String value) {
        this.transactionThreadID = value;
    }

    public boolean isSetTransactionThreadID() {
        return (this.transactionThreadID!= null);
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
     * Gets the value of the serviceHost property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getServiceHost() {
        return serviceHost;
    }

    /**
     * Sets the value of the serviceHost property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setServiceHost(String value) {
        this.serviceHost = value;
    }

    public boolean isSetServiceHost() {
        return (this.serviceHost!= null);
    }

    /**
     * Gets the value of the success property.
     * 
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Sets the value of the success property.
     * 
     */
    public void setSuccess(boolean value) {
        this.success = value;
    }

    public boolean isSetSuccess() {
        return true;
    }

    /**
     * Gets the value of the uri property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getURI() {
        return uri;
    }

    /**
     * Sets the value of the uri property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setURI(String value) {
        this.uri = value;
    }

    public boolean isSetURI() {
        return (this.uri!= null);
    }

    /**
     * Gets the value of the requestURI property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRequestURI() {
        return requestURI;
    }

    /**
     * Sets the value of the requestURI property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRequestURI(String value) {
        this.requestURI = value;
    }

    public boolean isSetRequestURI() {
        return (this.requestURI!= null);
    }

    /**
     * Gets the value of the xmlRequest property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getXmlRequest() {
        return xmlRequest;
    }

    /**
     * Sets the value of the xmlRequest property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setXmlRequest(String value) {
        this.xmlRequest = value;
    }

    public boolean isSetXmlRequest() {
        return (this.xmlRequest!= null);
    }

    /**
     * Gets the value of the xmlResponse property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getXmlResponse() {
        return xmlResponse;
    }

    /**
     * Sets the value of the xmlResponse property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setXmlResponse(String value) {
        this.xmlResponse = value;
    }

    public boolean isSetXmlResponse() {
        return (this.xmlResponse!= null);
    }

    /**
     * Gets the value of the requestSize property.
     * 
     */
    public int getRequestSize() {
        return requestSize;
    }

    /**
     * Sets the value of the requestSize property.
     * 
     */
    public void setRequestSize(int value) {
        this.requestSize = value;
    }

    public boolean isSetRequestSize() {
        return true;
    }

    /**
     * Gets the value of the responseSize property.
     * 
     */
    public int getResponseSize() {
        return responseSize;
    }

    /**
     * Sets the value of the responseSize property.
     * 
     */
    public void setResponseSize(int value) {
        this.responseSize = value;
    }

    public boolean isSetResponseSize() {
        return true;
    }

    /**
     * Gets the value of the responseTime property.
     * 
     */
    public int getResponseTime() {
        return responseTime;
    }

    /**
     * Sets the value of the responseTime property.
     * 
     */
    public void setResponseTime(int value) {
        this.responseTime = value;
    }

    public boolean isSetResponseTime() {
        return true;
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
     * Gets the value of the recordedat property.
     * 
     * @return
     *     possible object is
     *     {@link Calendar }
     *     
     */
    public Calendar getRecordedat() {
        return recordedat;
    }

    /**
     * Sets the value of the recordedat property.
     * 
     * @param value
     *     allowed object is
     *     {@link Calendar }
     *     
     */
    public void setRecordedat(Calendar value) {
        this.recordedat = value;
    }

    public boolean isSetRecordedat() {
        return (this.recordedat!= null);
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

}
