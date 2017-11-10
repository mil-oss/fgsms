
package org.miloss.fgsms.services.interfaces.dataaccessservice;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * request message
 * 
 * <p>Java class for ArrayOfHostInstanceStats complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfHostInstanceStats">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="HostInstanceStats" type="{urn:org:miloss:fgsms:services:interfaces:dataAccessService}HostInstanceStats" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfHostInstanceStats", propOrder = {
    "hostInstanceStats"
})
public class ArrayOfHostInstanceStats
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "HostInstanceStats", required = true, nillable = true)
    protected List<HostInstanceStats> hostInstanceStats;

    /**
     * Gets the value of the hostInstanceStats property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the hostInstanceStats property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getHostInstanceStats().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link HostInstanceStats }
     * 
     * 
     */
    public List<HostInstanceStats> getHostInstanceStats() {
        if (hostInstanceStats == null) {
            hostInstanceStats = new ArrayList<HostInstanceStats>();
        }
        return this.hostInstanceStats;
    }

    public boolean isSetHostInstanceStats() {
        return ((this.hostInstanceStats!= null)&&(!this.hostInstanceStats.isEmpty()));
    }

    public void unsetHostInstanceStats() {
        this.hostInstanceStats = null;
    }

}
