
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
 *         &lt;element name="req" type="{urn:org:miloss:fgsms:services:interfaces:dataAccessService}GetMessageTransactionLogDetailsResponseMsg"/>
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
@XmlRootElement(name = "GetMessageTransactionLogDetailsResponse")
public class GetMessageTransactionLogDetailsResponse
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(required = true)
    protected GetMessageTransactionLogDetailsResponseMsg req;

    /**
     * Gets the value of the req property.
     * 
     * @return
     *     possible object is
     *     {@link GetMessageTransactionLogDetailsResponseMsg }
     *     
     */
    public GetMessageTransactionLogDetailsResponseMsg getReq() {
        return req;
    }

    /**
     * Sets the value of the req property.
     * 
     * @param value
     *     allowed object is
     *     {@link GetMessageTransactionLogDetailsResponseMsg }
     *     
     */
    public void setReq(GetMessageTransactionLogDetailsResponseMsg value) {
        this.req = value;
    }

    public boolean isSetReq() {
        return (this.req!= null);
    }

}
