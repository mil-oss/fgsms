
package org.miloss.fgsms.services.interfaces.policyconfiguration;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 * sla container
 * 			
 * 
 * <p>Java class for ArrayOfSLA complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfSLA">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="SLA" type="{urn:org:miloss:fgsms:services:interfaces:policyConfiguration}SLA" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfSLA", propOrder = {
    "sla"
})
public class ArrayOfSLA
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "SLA", required = false, nillable = true)
    protected List<SLA> sla;

    /**
     * Gets the value of the sla property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the sla property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSLA().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SLA }
     * 
     * 
     */
    public List<SLA> getSLA() {
        if (sla == null) {
            sla = new ArrayList<SLA>();
        }
        return this.sla;
    }

    public boolean isSetSLA() {
        return ((this.sla!= null)&&(!this.sla.isEmpty()));
    }

    public void unsetSLA() {
        this.sla = null;
    }

}
