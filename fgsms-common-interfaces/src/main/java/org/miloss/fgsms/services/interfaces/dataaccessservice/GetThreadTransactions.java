
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
 *         &lt;element name="GetThreadTransactionsRequest" type="{urn:org:miloss:fgsms:services:interfaces:dataAccessService}GetThreadTransactionsRequestMsg"/>
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
    "getThreadTransactionsRequest"
})
@XmlRootElement(name = "GetThreadTransactions")
public class GetThreadTransactions
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "GetThreadTransactionsRequest", required = true)
    protected GetThreadTransactionsRequestMsg getThreadTransactionsRequest;

    /**
     * Gets the value of the getThreadTransactionsRequest property.
     * 
     * @return
     *     possible object is
     *     {@link GetThreadTransactionsRequestMsg }
     *     
     */
    public GetThreadTransactionsRequestMsg getGetThreadTransactionsRequest() {
        return getThreadTransactionsRequest;
    }

    /**
     * Sets the value of the getThreadTransactionsRequest property.
     * 
     * @param value
     *     allowed object is
     *     {@link GetThreadTransactionsRequestMsg }
     *     
     */
    public void setGetThreadTransactionsRequest(GetThreadTransactionsRequestMsg value) {
        this.getThreadTransactionsRequest = value;
    }

    public boolean isSetGetThreadTransactionsRequest() {
        return (this.getThreadTransactionsRequest!= null);
    }

}
