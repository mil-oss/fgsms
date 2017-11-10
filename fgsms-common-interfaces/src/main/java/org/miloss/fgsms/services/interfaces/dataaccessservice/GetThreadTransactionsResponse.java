
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
 *         &lt;element name="GetThreadTransactionsResponse" type="{urn:org:miloss:fgsms:services:interfaces:dataAccessService}GetThreadTransactionsResponseMsg"/>
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
    "getThreadTransactionsResponse"
})
@XmlRootElement(name = "GetThreadTransactionsResponse")
public class GetThreadTransactionsResponse
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "GetThreadTransactionsResponse", required = true)
    protected GetThreadTransactionsResponseMsg getThreadTransactionsResponse;

    /**
     * Gets the value of the getThreadTransactionsResponse property.
     * 
     * @return
     *     possible object is
     *     {@link GetThreadTransactionsResponseMsg }
     *     
     */
    public GetThreadTransactionsResponseMsg getGetThreadTransactionsResponse() {
        return getThreadTransactionsResponse;
    }

    /**
     * Sets the value of the getThreadTransactionsResponse property.
     * 
     * @param value
     *     allowed object is
     *     {@link GetThreadTransactionsResponseMsg }
     *     
     */
    public void setGetThreadTransactionsResponse(GetThreadTransactionsResponseMsg value) {
        this.getThreadTransactionsResponse = value;
    }

    public boolean isSetGetThreadTransactionsResponse() {
        return (this.getThreadTransactionsResponse!= null);
    }

}
