
package org.miloss.fgsms.services.interfaces.status;

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
 *         &lt;element name="GetStatusResponseResult" type="{urn:org:miloss:fgsms:services:interfaces:status}GetStatusResponseMsg"/>
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
    "getStatusResponseResult"
})
@XmlRootElement(name = "GetStatusResponse")
public class GetStatusResponse {

    @XmlElement(name = "GetStatusResponseResult", required = true, nillable = true)
    protected GetStatusResponseMsg getStatusResponseResult;

    /**
     * Gets the value of the getStatusResponseResult property.
     * 
     * @return
     *     possible object is
     *     {@link GetStatusResponseMsg }
     *     
     */
    public GetStatusResponseMsg getGetStatusResponseResult() {
        return getStatusResponseResult;
    }

    /**
     * Sets the value of the getStatusResponseResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link GetStatusResponseMsg }
     *     
     */
    public void setGetStatusResponseResult(GetStatusResponseMsg value) {
        this.getStatusResponseResult = value;
    }

}
