
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
 *         &lt;element name="GetAllMostRecentMachineAndProcessDataResponse" type="{urn:org:miloss:fgsms:services:interfaces:dataAccessService}GetAllMostRecentMachineAndProcessDataResponseMsg"/>
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
    "getAllMostRecentMachineAndProcessDataResponse"
})
@XmlRootElement(name = "GetAllMostRecentMachineAndProcessDataResponse")
public class GetAllMostRecentMachineAndProcessDataResponse
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "GetAllMostRecentMachineAndProcessDataResponse", required = true)
    protected GetAllMostRecentMachineAndProcessDataResponseMsg getAllMostRecentMachineAndProcessDataResponse;

    /**
     * Gets the value of the getAllMostRecentMachineAndProcessDataResponse property.
     * 
     * @return
     *     possible object is
     *     {@link GetAllMostRecentMachineAndProcessDataResponseMsg }
     *     
     */
    public GetAllMostRecentMachineAndProcessDataResponseMsg getGetAllMostRecentMachineAndProcessDataResponse() {
        return getAllMostRecentMachineAndProcessDataResponse;
    }

    /**
     * Sets the value of the getAllMostRecentMachineAndProcessDataResponse property.
     * 
     * @param value
     *     allowed object is
     *     {@link GetAllMostRecentMachineAndProcessDataResponseMsg }
     *     
     */
    public void setGetAllMostRecentMachineAndProcessDataResponse(GetAllMostRecentMachineAndProcessDataResponseMsg value) {
        this.getAllMostRecentMachineAndProcessDataResponse = value;
    }

    public boolean isSetGetAllMostRecentMachineAndProcessDataResponse() {
        return (this.getAllMostRecentMachineAndProcessDataResponse!= null);
    }

}
