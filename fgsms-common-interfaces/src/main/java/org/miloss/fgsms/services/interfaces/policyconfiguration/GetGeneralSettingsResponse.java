
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
 *         &lt;element name="GetGeneralSettingsResult" type="{urn:org:miloss:fgsms:services:interfaces:policyConfiguration}GetGeneralSettingsResponseMsg"/>
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
    "getGeneralSettingsResult"
})
@XmlRootElement(name = "GetGeneralSettingsResponse")
public class GetGeneralSettingsResponse
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "GetGeneralSettingsResult", required = true, nillable = true)
    protected GetGeneralSettingsResponseMsg getGeneralSettingsResult;

    /**
     * Gets the value of the getGeneralSettingsResult property.
     * 
     * @return
     *     possible object is
     *     {@link GetGeneralSettingsResponseMsg }
     *     
     */
    public GetGeneralSettingsResponseMsg getGetGeneralSettingsResult() {
        return getGeneralSettingsResult;
    }

    /**
     * Sets the value of the getGeneralSettingsResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link GetGeneralSettingsResponseMsg }
     *     
     */
    public void setGetGeneralSettingsResult(GetGeneralSettingsResponseMsg value) {
        this.getGeneralSettingsResult = value;
    }

    public boolean isSetGetGeneralSettingsResult() {
        return (this.getGeneralSettingsResult!= null);
    }

}
