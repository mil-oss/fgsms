
package org.miloss.fgsms.services.interfaces.dataaccessservice;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.miloss.fgsms.services.interfaces.common.SecurityWrapper;


/**
 * response message
 * 
 * <p>Java class for GetMostRecentMachineDataResponseMsg complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetMostRecentMachineDataResponseMsg">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="classification" type="{urn:org:miloss:fgsms:services:interfaces:common}SecurityWrapper"/>
 *         &lt;element name="MachineData" type="{urn:org:miloss:fgsms:services:interfaces:dataAccessService}MachineData"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetMostRecentMachineDataResponseMsg", propOrder = {
    "classification",
    "machineData"
})
public class GetMostRecentMachineDataResponseMsg
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(required = true, nillable = true)
    protected SecurityWrapper classification;
    @XmlElement(name = "MachineData", required = true, nillable = true)
    protected MachineData machineData;

    /**
     * Gets the value of the classification property.
     * 
     * @return
     *     possible object is
     *     {@link SecurityWrapper }
     *     
     */
    public SecurityWrapper getClassification() {
        return classification;
    }

    /**
     * Sets the value of the classification property.
     * 
     * @param value
     *     allowed object is
     *     {@link SecurityWrapper }
     *     
     */
    public void setClassification(SecurityWrapper value) {
        this.classification = value;
    }

    public boolean isSetClassification() {
        return (this.classification!= null);
    }

    /**
     * Gets the value of the machineData property.
     * 
     * @return
     *     possible object is
     *     {@link MachineData }
     *     
     */
    public MachineData getMachineData() {
        return machineData;
    }

    /**
     * Sets the value of the machineData property.
     * 
     * @param value
     *     allowed object is
     *     {@link MachineData }
     *     
     */
    public void setMachineData(MachineData value) {
        this.machineData = value;
    }

    public boolean isSetMachineData() {
        return (this.machineData!= null);
    }

}
