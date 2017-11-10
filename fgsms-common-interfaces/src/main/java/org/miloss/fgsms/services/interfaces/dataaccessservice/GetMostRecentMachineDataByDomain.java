
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
 *         &lt;element name="GetMostRecentMachineDataByDomainRequest" type="{urn:org:miloss:fgsms:services:interfaces:dataAccessService}GetMostRecentMachineDataByDomainRequestMsg"/>
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
    "getMostRecentMachineDataByDomainRequest"
})
@XmlRootElement(name = "GetMostRecentMachineDataByDomain")
public class GetMostRecentMachineDataByDomain
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "GetMostRecentMachineDataByDomainRequest", required = true)
    protected GetMostRecentMachineDataByDomainRequestMsg getMostRecentMachineDataByDomainRequest;

    /**
     * Gets the value of the getMostRecentMachineDataByDomainRequest property.
     * 
     * @return
     *     possible object is
     *     {@link GetMostRecentMachineDataByDomainRequestMsg }
     *     
     */
    public GetMostRecentMachineDataByDomainRequestMsg getGetMostRecentMachineDataByDomainRequest() {
        return getMostRecentMachineDataByDomainRequest;
    }

    /**
     * Sets the value of the getMostRecentMachineDataByDomainRequest property.
     * 
     * @param value
     *     allowed object is
     *     {@link GetMostRecentMachineDataByDomainRequestMsg }
     *     
     */
    public void setGetMostRecentMachineDataByDomainRequest(GetMostRecentMachineDataByDomainRequestMsg value) {
        this.getMostRecentMachineDataByDomainRequest = value;
    }

    public boolean isSetGetMostRecentMachineDataByDomainRequest() {
        return (this.getMostRecentMachineDataByDomainRequest!= null);
    }

}
