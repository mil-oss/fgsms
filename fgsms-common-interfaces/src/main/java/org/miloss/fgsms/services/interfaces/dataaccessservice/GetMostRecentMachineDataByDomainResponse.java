
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
 *         &lt;element name="GetMostRecentMachineDataByDomainResponse" type="{urn:org:miloss:fgsms:services:interfaces:dataAccessService}GetMostRecentMachineDataByDomainResponseMsg"/>
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
    "getMostRecentMachineDataByDomainResponse"
})
@XmlRootElement(name = "GetMostRecentMachineDataByDomainResponse")
public class GetMostRecentMachineDataByDomainResponse
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "GetMostRecentMachineDataByDomainResponse", required = true)
    protected GetMostRecentMachineDataByDomainResponseMsg getMostRecentMachineDataByDomainResponse;

    /**
     * Gets the value of the getMostRecentMachineDataByDomainResponse property.
     * 
     * @return
     *     possible object is
     *     {@link GetMostRecentMachineDataByDomainResponseMsg }
     *     
     */
    public GetMostRecentMachineDataByDomainResponseMsg getGetMostRecentMachineDataByDomainResponse() {
        return getMostRecentMachineDataByDomainResponse;
    }

    /**
     * Sets the value of the getMostRecentMachineDataByDomainResponse property.
     * 
     * @param value
     *     allowed object is
     *     {@link GetMostRecentMachineDataByDomainResponseMsg }
     *     
     */
    public void setGetMostRecentMachineDataByDomainResponse(GetMostRecentMachineDataByDomainResponseMsg value) {
        this.getMostRecentMachineDataByDomainResponse = value;
    }

    public boolean isSetGetMostRecentMachineDataByDomainResponse() {
        return (this.getMostRecentMachineDataByDomainResponse!= null);
    }

}
