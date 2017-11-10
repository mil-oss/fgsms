
package org.miloss.fgsms.services.interfaces.datacollector;

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
 *         &lt;element name="AddMachineAndProcessDataRequestMsg" type="{urn:org:miloss:fgsms:services:interfaces:dataCollector}AddMachineAndProcessDataRequestMsg"/>
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
    "addMachineAndProcessDataRequestMsg"
})
@XmlRootElement(name = "AddMachineAndProcessData")
public class AddMachineAndProcessData
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "AddMachineAndProcessDataRequestMsg", required = true)
    protected AddMachineAndProcessDataRequestMsg addMachineAndProcessDataRequestMsg;

    /**
     * Gets the value of the addMachineAndProcessDataRequestMsg property.
     * 
     * @return
     *     possible object is
     *     {@link AddMachineAndProcessDataRequestMsg }
     *     
     */
    public AddMachineAndProcessDataRequestMsg getAddMachineAndProcessDataRequestMsg() {
        return addMachineAndProcessDataRequestMsg;
    }

    /**
     * Sets the value of the addMachineAndProcessDataRequestMsg property.
     * 
     * @param value
     *     allowed object is
     *     {@link AddMachineAndProcessDataRequestMsg }
     *     
     */
    public void setAddMachineAndProcessDataRequestMsg(AddMachineAndProcessDataRequestMsg value) {
        this.addMachineAndProcessDataRequestMsg = value;
    }

    public boolean isSetAddMachineAndProcessDataRequestMsg() {
        return (this.addMachineAndProcessDataRequestMsg!= null);
    }

}
