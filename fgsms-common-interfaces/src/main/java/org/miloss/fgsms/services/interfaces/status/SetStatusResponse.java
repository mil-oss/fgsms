
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
 *         &lt;element name="SetStatusResponseResult" type="{urn:org:miloss:fgsms:services:interfaces:status}SetStatusResponseMsg"/>
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
    "setStatusResponseResult"
})
@XmlRootElement(name = "SetStatusResponse")
public class SetStatusResponse {

    @XmlElement(name = "SetStatusResponseResult", required = true, nillable = true)
    protected SetStatusResponseMsg setStatusResponseResult;

    /**
     * Gets the value of the setStatusResponseResult property.
     * 
     * @return
     *     possible object is
     *     {@link SetStatusResponseMsg }
     *     
     */
    public SetStatusResponseMsg getSetStatusResponseResult() {
        return setStatusResponseResult;
    }

    /**
     * Sets the value of the setStatusResponseResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link SetStatusResponseMsg }
     *     
     */
    public void setSetStatusResponseResult(SetStatusResponseMsg value) {
        this.setStatusResponseResult = value;
    }

}
