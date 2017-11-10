
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
 *         &lt;element name="GetMailSettingsResult" type="{urn:org:miloss:fgsms:services:interfaces:policyConfiguration}GetMailSettingsResponseMsg"/>
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
    "getMailSettingsResult"
})
@XmlRootElement(name = "GetMailSettingsResponse")
public class GetMailSettingsResponse
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "GetMailSettingsResult", required = true, nillable = true)
    protected GetMailSettingsResponseMsg getMailSettingsResult;

    /**
     * Gets the value of the getMailSettingsResult property.
     * 
     * @return
     *     possible object is
     *     {@link GetMailSettingsResponseMsg }
     *     
     */
    public GetMailSettingsResponseMsg getGetMailSettingsResult() {
        return getMailSettingsResult;
    }

    /**
     * Sets the value of the getMailSettingsResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link GetMailSettingsResponseMsg }
     *     
     */
    public void setGetMailSettingsResult(GetMailSettingsResponseMsg value) {
        this.getMailSettingsResult = value;
    }

    public boolean isSetGetMailSettingsResult() {
        return (this.getMailSettingsResult!= null);
    }

}
