
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
 *         &lt;element name="AddStatisticalDataRequestMsg" type="{urn:org:miloss:fgsms:services:interfaces:dataCollector}AddStatisticalDataRequestMsg"/>
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
    "addStatisticalDataRequestMsg"
})
@XmlRootElement(name = "AddStatisticalData")
public class AddStatisticalData
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "AddStatisticalDataRequestMsg", required = true)
    protected AddStatisticalDataRequestMsg addStatisticalDataRequestMsg;

    /**
     * Gets the value of the addStatisticalDataRequestMsg property.
     * 
     * @return
     *     possible object is
     *     {@link AddStatisticalDataRequestMsg }
     *     
     */
    public AddStatisticalDataRequestMsg getAddStatisticalDataRequestMsg() {
        return addStatisticalDataRequestMsg;
    }

    /**
     * Sets the value of the addStatisticalDataRequestMsg property.
     * 
     * @param value
     *     allowed object is
     *     {@link AddStatisticalDataRequestMsg }
     *     
     */
    public void setAddStatisticalDataRequestMsg(AddStatisticalDataRequestMsg value) {
        this.addStatisticalDataRequestMsg = value;
    }

    public boolean isSetAddStatisticalDataRequestMsg() {
        return (this.addStatisticalDataRequestMsg!= null);
    }

}
