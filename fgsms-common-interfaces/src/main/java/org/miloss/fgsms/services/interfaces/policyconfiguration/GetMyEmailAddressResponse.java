
package org.miloss.fgsms.services.interfaces.policyconfiguration;

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
 *         &lt;element name="GetMyEmailAddressResponseMsg" type="{urn:org:miloss:fgsms:services:interfaces:policyConfiguration}GetMyEmailAddressResponseMsg"/>
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
    "getMyEmailAddressResponseMsg"
})
@XmlRootElement(name = "GetMyEmailAddressResponse")
public class GetMyEmailAddressResponse
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "GetMyEmailAddressResponseMsg", required = false, nillable = true)
    protected GetMyEmailAddressResponseMsg getMyEmailAddressResponseMsg;

    /**
     * Gets the value of the getMyEmailAddressResponseMsg property.
     * 
     * @return
     *     possible object is
     *     {@link GetMyEmailAddressResponseMsg }
     *     
     */
    public GetMyEmailAddressResponseMsg getGetMyEmailAddressResponseMsg() {
        return getMyEmailAddressResponseMsg;
    }

    /**
     * Sets the value of the getMyEmailAddressResponseMsg property.
     * 
     * @param value
     *     allowed object is
     *     {@link GetMyEmailAddressResponseMsg }
     *     
     */
    public void setGetMyEmailAddressResponseMsg(GetMyEmailAddressResponseMsg value) {
        this.getMyEmailAddressResponseMsg = value;
    }

    public boolean isSetGetMyEmailAddressResponseMsg() {
        return (this.getMyEmailAddressResponseMsg!= null);
    }

}
