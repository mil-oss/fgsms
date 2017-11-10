
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
 *         &lt;element name="response" type="{urn:org:miloss:fgsms:services:interfaces:policyConfiguration}RegisterPluginResponseMsg"/>
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
    "response"
})
@XmlRootElement(name = "registerPluginResponse")
public class RegisterPluginResponse
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(required = true, nillable = true)
    protected RegisterPluginResponseMsg response;

    /**
     * Gets the value of the response property.
     * 
     * @return
     *     possible object is
     *     {@link RegisterPluginResponseMsg }
     *     
     */
    public RegisterPluginResponseMsg getResponse() {
        return response;
    }

    /**
     * Sets the value of the response property.
     * 
     * @param value
     *     allowed object is
     *     {@link RegisterPluginResponseMsg }
     *     
     */
    public void setResponse(RegisterPluginResponseMsg value) {
        this.response = value;
    }

    public boolean isSetResponse() {
        return (this.response!= null);
    }

}
