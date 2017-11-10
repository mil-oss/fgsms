
package org.miloss.fgsms.services.interfaces.status;

import java.util.ArrayList;
import java.util.List;
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
 *         &lt;element name="req" type="{urn:org:miloss:fgsms:services:interfaces:status}SetStatusRequestMsg" maxOccurs="unbounded"/>
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
@XmlRootElement(name = "SetMoreStatus")
public class SetMoreStatus {

    @XmlElement(required = true, nillable = true)
    protected List<SetStatusRequestMsg> req;

    /**
     * Gets the value of the req property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the req property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getReq().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SetStatusRequestMsg }
     * 
     * 
     */
    public List<SetStatusRequestMsg> getReq() {
        if (req == null) {
            req = new ArrayList<SetStatusRequestMsg>();
        }
        return this.req;
    }

}
