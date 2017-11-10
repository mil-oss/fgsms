
package org.miloss.fgsms.services.interfaces.status;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import java.util.Calendar;
import org.miloss.fgsms.services.interfaces.common.NameValuePairSet;
import org.miloss.fgsms.services.interfaces.common.PolicyType;
import org.miloss.fgsms.services.interfaces.common.SecurityWrapper;


/**
 * request
 * 
 * <p>Java class for SetExtendedStatusRequestMsg complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SetExtendedStatusRequestMsg">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="classification" type="{urn:org:miloss:fgsms:services:interfaces:common}SecurityWrapper"/>
 *         &lt;element name="Operational" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="URI" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Message" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Hostname" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="DomainName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="TimeStamp" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="PolicyType" type="{urn:org:miloss:fgsms:services:interfaces:common}policyType"/>
 *         &lt;element name="ParentObject" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Data" type="{urn:org:miloss:fgsms:services:interfaces:common}NameValuePairSet"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SetExtendedStatusRequestMsg", propOrder = {
    "classification",
    "operational",
    "uri",
    "message",
    "hostname",
    "domainName",
    "timeStamp",
    "policyType",
    "parentObject",
    "data"
})
public class SetExtendedStatusRequestMsg {

    @XmlElement(required = true, nillable = true)
    protected SecurityWrapper classification;
    @XmlElement(name = "Operational")
    protected boolean operational;
    @XmlElement(name = "URI", required = true, nillable = true)
    protected String uri;
    @XmlElement(name = "Message", required = true, nillable = true)
    protected String message;
    @XmlElement(name = "Hostname", required = true, nillable = true)
    protected String hostname;
    @XmlElement(name = "DomainName", required = true, nillable = true)
    protected String domainName;
    @XmlElement(name = "TimeStamp", required = true)
    @XmlSchemaType(name = "dateTime")
    protected Calendar timeStamp;
    @XmlElement(name = "PolicyType", required = true, defaultValue = "Status", nillable = true)
    protected PolicyType policyType;
    @XmlElement(name = "ParentObject", required = false)
    protected String parentObject;
    @XmlElement(name = "Data", required = false)
    protected NameValuePairSet data;

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

    /**
     * Gets the value of the operational property.
     * 
     */
    public boolean isOperational() {
        return operational;
    }

    /**
     * Sets the value of the operational property.
     * 
     */
    public void setOperational(boolean value) {
        this.operational = value;
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

    /**
     * Gets the value of the hostname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHostname() {
        return hostname;
    }

    /**
     * Sets the value of the hostname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHostname(String value) {
        this.hostname = value;
    }

    /**
     * Gets the value of the domainName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDomainName() {
        return domainName;
    }

    /**
     * Sets the value of the domainName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDomainName(String value) {
        this.domainName = value;
    }

    /**
     * Gets the value of the timeStamp property.
     * 
     * @return
     *     possible object is
     *     {@link Calendar }
     *     
     */
    public Calendar getTimeStamp() {
        return timeStamp;
    }

    /**
     * Sets the value of the timeStamp property.
     * 
     * @param value
     *     allowed object is
     *     {@link Calendar }
     *     
     */
    public void setTimeStamp(Calendar value) {
        this.timeStamp = value;
    }

    /**
     * Gets the value of the policyType property.
     * 
     * @return
     *     possible object is
     *     {@link PolicyType }
     *     
     */
    public PolicyType getPolicyType() {
        return policyType;
    }

    /**
     * Sets the value of the policyType property.
     * 
     * @param value
     *     allowed object is
     *     {@link PolicyType }
     *     
     */
    public void setPolicyType(PolicyType value) {
        this.policyType = value;
    }

    /**
     * Gets the value of the parentObject property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getParentObject() {
        return parentObject;
    }

    /**
     * Sets the value of the parentObject property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setParentObject(String value) {
        this.parentObject = value;
    }

    /**
     * Gets the value of the data property.
     * 
     * @return
     *     possible object is
     *     {@link NameValuePairSet }
     *     
     */
    public NameValuePairSet getData() {
        return data;
    }

    /**
     * Sets the value of the data property.
     * 
     * @param value
     *     allowed object is
     *     {@link NameValuePairSet }
     *     
     */
    public void setData(NameValuePairSet value) {
        this.data = value;
    }

}
