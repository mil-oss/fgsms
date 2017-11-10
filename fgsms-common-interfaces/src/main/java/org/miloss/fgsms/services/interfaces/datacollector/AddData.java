
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
 *         &lt;element name="req" type="{urn:org:miloss:fgsms:services:interfaces:dataCollector}AddDataRequestMsg"/>
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
    "req"
})
@XmlRootElement(name = "AddData")
public class AddData
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(required = true, nillable = true)
    protected AddDataRequestMsg req;

    /**
     * Gets the value of the req property.
     * 
     * @return
     *     possible object is
     *     {@link AddDataRequestMsg }
     *     
     */
    public AddDataRequestMsg getReq() {
        return req;
    }

    /**
     * Sets the value of the req property.
     * 
     * @param value
     *     allowed object is
     *     {@link AddDataRequestMsg }
     *     
     */
    public void setReq(AddDataRequestMsg value) {
        this.req = value;
    }

    public boolean isSetReq() {
        return (this.req!= null);
    }

}
