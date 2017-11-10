
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
 *         &lt;element name="req" type="{urn:org:miloss:fgsms:services:interfaces:dataAccessService}GetMessageTransactionLogDetailsMsg"/>
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
    "req"
})
@XmlRootElement(name = "GetMessageTransactionLogDetails")
public class GetMessageTransactionLogDetails
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(required = true)
    protected GetMessageTransactionLogDetailsMsg req;

    /**
     * Gets the value of the req property.
     * 
     * @return
     *     possible object is
     *     {@link GetMessageTransactionLogDetailsMsg }
     *     
     */
    public GetMessageTransactionLogDetailsMsg getReq() {
        return req;
    }

    /**
     * Sets the value of the req property.
     * 
     * @param value
     *     allowed object is
     *     {@link GetMessageTransactionLogDetailsMsg }
     *     
     */
    public void setReq(GetMessageTransactionLogDetailsMsg value) {
        this.req = value;
    }

    public boolean isSetReq() {
        return (this.req!= null);
    }

}
