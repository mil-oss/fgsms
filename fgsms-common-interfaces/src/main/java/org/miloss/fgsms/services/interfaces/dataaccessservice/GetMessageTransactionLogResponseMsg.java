
package org.miloss.fgsms.services.interfaces.dataaccessservice;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.miloss.fgsms.services.interfaces.common.SecurityWrapper;


/**
 * 
 *             GetMessageTransactionLog Response
 *             
 * 
 * <p>Java class for GetMessageTransactionLogResponseMsg complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetMessageTransactionLogResponseMsg">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="classification" type="{urn:org:miloss:fgsms:services:interfaces:common}SecurityWrapper"/>
 *         &lt;element name="IsFault" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="RequestMessage" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ResponseMessage" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ResponseTime" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetMessageTransactionLogResponseMsg", propOrder = {
    "classification",
    "isFault",
    "requestMessage",
    "responseMessage",
    "responseTime"
})
public class GetMessageTransactionLogResponseMsg
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(required = true, nillable = true)
    protected SecurityWrapper classification;
    @XmlElement(name = "IsFault")
    protected boolean isFault;
    @XmlElement(name = "RequestMessage", required = true, nillable = true)
    protected String requestMessage;
    @XmlElement(name = "ResponseMessage", required = true, nillable = true)
    protected String responseMessage;
    @XmlElement(name = "ResponseTime")
    protected long responseTime;

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
     * Gets the value of the requestMessage property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRequestMessage() {
        return requestMessage;
    }

    /**
     * Sets the value of the requestMessage property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRequestMessage(String value) {
        this.requestMessage = value;
    }

    public boolean isSetRequestMessage() {
        return (this.requestMessage!= null);
    }

    /**
     * Gets the value of the responseMessage property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getResponseMessage() {
        return responseMessage;
    }

    /**
     * Sets the value of the responseMessage property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setResponseMessage(String value) {
        this.responseMessage = value;
    }

    public boolean isSetResponseMessage() {
        return (this.responseMessage!= null);
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

}
