
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
 *         &lt;element name="GetMessageTransactionLogResult" type="{urn:org:miloss:fgsms:services:interfaces:dataAccessService}GetMessageTransactionLogResponseMsg"/>
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
    "getMessageTransactionLogResult"
})
@XmlRootElement(name = "GetMessageTransactionLogResponse")
public class GetMessageTransactionLogResponse
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "GetMessageTransactionLogResult", required = true, nillable = true)
    protected GetMessageTransactionLogResponseMsg getMessageTransactionLogResult;

    /**
     * Gets the value of the getMessageTransactionLogResult property.
     * 
     * @return
     *     possible object is
     *     {@link GetMessageTransactionLogResponseMsg }
     *     
     */
    public GetMessageTransactionLogResponseMsg getGetMessageTransactionLogResult() {
        return getMessageTransactionLogResult;
    }

    /**
     * Sets the value of the getMessageTransactionLogResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link GetMessageTransactionLogResponseMsg }
     *     
     */
    public void setGetMessageTransactionLogResult(GetMessageTransactionLogResponseMsg value) {
        this.getMessageTransactionLogResult = value;
    }

    public boolean isSetGetMessageTransactionLogResult() {
        return (this.getMessageTransactionLogResult!= null);
    }

}
