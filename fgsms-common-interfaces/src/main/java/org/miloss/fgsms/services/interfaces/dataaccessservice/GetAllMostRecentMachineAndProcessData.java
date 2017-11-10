
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
 *         &lt;element name="GetAllMostRecentMachineAndProcessDataRequest" type="{urn:org:miloss:fgsms:services:interfaces:dataAccessService}GetAllMostRecentMachineAndProcessDataRequestMsg"/>
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
    "getAllMostRecentMachineAndProcessDataRequest"
})
@XmlRootElement(name = "GetAllMostRecentMachineAndProcessData")
public class GetAllMostRecentMachineAndProcessData
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "GetAllMostRecentMachineAndProcessDataRequest", required = true)
    protected GetAllMostRecentMachineAndProcessDataRequestMsg getAllMostRecentMachineAndProcessDataRequest;

    /**
     * Gets the value of the getAllMostRecentMachineAndProcessDataRequest property.
     * 
     * @return
     *     possible object is
     *     {@link GetAllMostRecentMachineAndProcessDataRequestMsg }
     *     
     */
    public GetAllMostRecentMachineAndProcessDataRequestMsg getGetAllMostRecentMachineAndProcessDataRequest() {
        return getAllMostRecentMachineAndProcessDataRequest;
    }

    /**
     * Sets the value of the getAllMostRecentMachineAndProcessDataRequest property.
     * 
     * @param value
     *     allowed object is
     *     {@link GetAllMostRecentMachineAndProcessDataRequestMsg }
     *     
     */
    public void setGetAllMostRecentMachineAndProcessDataRequest(GetAllMostRecentMachineAndProcessDataRequestMsg value) {
        this.getAllMostRecentMachineAndProcessDataRequest = value;
    }

    public boolean isSetGetAllMostRecentMachineAndProcessDataRequest() {
        return (this.getAllMostRecentMachineAndProcessDataRequest!= null);
    }

}
