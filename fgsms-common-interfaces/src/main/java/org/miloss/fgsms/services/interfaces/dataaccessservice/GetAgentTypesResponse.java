
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
 *         &lt;element name="GetAgentTypesResponse" type="{urn:org:miloss:fgsms:services:interfaces:dataAccessService}GetAgentTypesResponseMsg"/>
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
    "getAgentTypesResponse"
})
@XmlRootElement(name = "GetAgentTypesResponse")
public class GetAgentTypesResponse
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "GetAgentTypesResponse", required = true)
    protected GetAgentTypesResponseMsg getAgentTypesResponse;

    /**
     * Gets the value of the getAgentTypesResponse property.
     * 
     * @return
     *     possible object is
     *     {@link GetAgentTypesResponseMsg }
     *     
     */
    public GetAgentTypesResponseMsg getGetAgentTypesResponse() {
        return getAgentTypesResponse;
    }

    /**
     * Sets the value of the getAgentTypesResponse property.
     * 
     * @param value
     *     allowed object is
     *     {@link GetAgentTypesResponseMsg }
     *     
     */
    public void setGetAgentTypesResponse(GetAgentTypesResponseMsg value) {
        this.getAgentTypesResponse = value;
    }

    public boolean isSetGetAgentTypesResponse() {
        return (this.getAgentTypesResponse!= null);
    }

}
