
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
 *         &lt;element name="GetMostRecentMachineDataRequest" type="{urn:org:miloss:fgsms:services:interfaces:dataAccessService}GetMostRecentMachineDataRequestMsg"/>
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
    "getMostRecentMachineDataRequest"
})
@XmlRootElement(name = "GetMostRecentMachineData")
public class GetMostRecentMachineData
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "GetMostRecentMachineDataRequest", required = true)
    protected GetMostRecentMachineDataRequestMsg getMostRecentMachineDataRequest;

    /**
     * Gets the value of the getMostRecentMachineDataRequest property.
     * 
     * @return
     *     possible object is
     *     {@link GetMostRecentMachineDataRequestMsg }
     *     
     */
    public GetMostRecentMachineDataRequestMsg getGetMostRecentMachineDataRequest() {
        return getMostRecentMachineDataRequest;
    }

    /**
     * Sets the value of the getMostRecentMachineDataRequest property.
     * 
     * @param value
     *     allowed object is
     *     {@link GetMostRecentMachineDataRequestMsg }
     *     
     */
    public void setGetMostRecentMachineDataRequest(GetMostRecentMachineDataRequestMsg value) {
        this.getMostRecentMachineDataRequest = value;
    }

    public boolean isSetGetMostRecentMachineDataRequest() {
        return (this.getMostRecentMachineDataRequest!= null);
    }

}
