
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
 *         &lt;element name="GetAgentPrinicplesResult" type="{urn:org:miloss:fgsms:services:interfaces:policyConfiguration}GetAgentPrinicplesResponseMsg"/>
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
    "getAgentPrinicplesResult"
})
@XmlRootElement(name = "GetAgentPrinicplesResponse")
public class GetAgentPrinicplesResponse
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "GetAgentPrinicplesResult", required = true, nillable = true)
    protected GetAgentPrinicplesResponseMsg getAgentPrinicplesResult;

    /**
     * Gets the value of the getAgentPrinicplesResult property.
     * 
     * @return
     *     possible object is
     *     {@link GetAgentPrinicplesResponseMsg }
     *     
     */
    public GetAgentPrinicplesResponseMsg getGetAgentPrinicplesResult() {
        return getAgentPrinicplesResult;
    }

    /**
     * Sets the value of the getAgentPrinicplesResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link GetAgentPrinicplesResponseMsg }
     *     
     */
    public void setGetAgentPrinicplesResult(GetAgentPrinicplesResponseMsg value) {
        this.getAgentPrinicplesResult = value;
    }

    public boolean isSetGetAgentPrinicplesResult() {
        return (this.getAgentPrinicplesResult!= null);
    }

}
