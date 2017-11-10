
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
 *         &lt;element name="GetAgentTypesRequest" type="{urn:org:miloss:fgsms:services:interfaces:dataAccessService}GetAgentTypesRequestMsg"/>
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
    "getAgentTypesRequest"
})
@XmlRootElement(name = "GetAgentTypes")
public class GetAgentTypes
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "GetAgentTypesRequest", required = true)
    protected GetAgentTypesRequestMsg getAgentTypesRequest;

    /**
     * Gets the value of the getAgentTypesRequest property.
     * 
     * @return
     *     possible object is
     *     {@link GetAgentTypesRequestMsg }
     *     
     */
    public GetAgentTypesRequestMsg getGetAgentTypesRequest() {
        return getAgentTypesRequest;
    }

    /**
     * Sets the value of the getAgentTypesRequest property.
     * 
     * @param value
     *     allowed object is
     *     {@link GetAgentTypesRequestMsg }
     *     
     */
    public void setGetAgentTypesRequest(GetAgentTypesRequestMsg value) {
        this.getAgentTypesRequest = value;
    }

    public boolean isSetGetAgentTypesRequest() {
        return (this.getAgentTypesRequest!= null);
    }

}
