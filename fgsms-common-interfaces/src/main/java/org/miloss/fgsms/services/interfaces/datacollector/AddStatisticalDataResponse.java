
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
 *         &lt;element name="AddStatisticalDataResponseMsg" type="{urn:org:miloss:fgsms:services:interfaces:dataCollector}AddStatisticalDataResponseMsg"/>
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
    "addStatisticalDataResponseMsg"
})
@XmlRootElement(name = "AddStatisticalDataResponse")
public class AddStatisticalDataResponse
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "AddStatisticalDataResponseMsg", required = true)
    protected AddStatisticalDataResponseMsg addStatisticalDataResponseMsg;

    /**
     * Gets the value of the addStatisticalDataResponseMsg property.
     * 
     * @return
     *     possible object is
     *     {@link AddStatisticalDataResponseMsg }
     *     
     */
    public AddStatisticalDataResponseMsg getAddStatisticalDataResponseMsg() {
        return addStatisticalDataResponseMsg;
    }

    /**
     * Sets the value of the addStatisticalDataResponseMsg property.
     * 
     * @param value
     *     allowed object is
     *     {@link AddStatisticalDataResponseMsg }
     *     
     */
    public void setAddStatisticalDataResponseMsg(AddStatisticalDataResponseMsg value) {
        this.addStatisticalDataResponseMsg = value;
    }

    public boolean isSetAddStatisticalDataResponseMsg() {
        return (this.addStatisticalDataResponseMsg!= null);
    }

}
