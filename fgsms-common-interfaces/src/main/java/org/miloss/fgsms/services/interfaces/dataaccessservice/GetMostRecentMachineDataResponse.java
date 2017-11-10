
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
 *         &lt;element name="GetMostRecentMachineDataResponse" type="{urn:org:miloss:fgsms:services:interfaces:dataAccessService}GetMostRecentMachineDataResponseMsg"/>
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
    "getMostRecentMachineDataResponse"
})
@XmlRootElement(name = "GetMostRecentMachineDataResponse")
public class GetMostRecentMachineDataResponse
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "GetMostRecentMachineDataResponse", required = true)
    protected GetMostRecentMachineDataResponseMsg getMostRecentMachineDataResponse;

    /**
     * Gets the value of the getMostRecentMachineDataResponse property.
     * 
     * @return
     *     possible object is
     *     {@link GetMostRecentMachineDataResponseMsg }
     *     
     */
    public GetMostRecentMachineDataResponseMsg getGetMostRecentMachineDataResponse() {
        return getMostRecentMachineDataResponse;
    }

    /**
     * Sets the value of the getMostRecentMachineDataResponse property.
     * 
     * @param value
     *     allowed object is
     *     {@link GetMostRecentMachineDataResponseMsg }
     *     
     */
    public void setGetMostRecentMachineDataResponse(GetMostRecentMachineDataResponseMsg value) {
        this.getMostRecentMachineDataResponse = value;
    }

    public boolean isSetGetMostRecentMachineDataResponse() {
        return (this.getMostRecentMachineDataResponse!= null);
    }

}
