
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
 * array of SLA actions to be performed
 * 			
 * 
 * <p>Java class for ArrayOfSLAActionBaseType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfSLAActionBaseType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="SLAAction" type="{urn:org:miloss:fgsms:services:interfaces:policyConfiguration}SLAAction" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfSLAActionBaseType", propOrder = {
    "slaAction"
})
public class ArrayOfSLAActionBaseType
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "SLAAction", required = true, nillable = true)
    protected List<SLAAction> slaAction;

    /**
     * Gets the value of the slaAction property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the slaAction property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSLAAction().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SLAAction }
     * 
     * 
     */
    public List<SLAAction> getSLAAction() {
        if (slaAction == null) {
            slaAction = new ArrayList<SLAAction>();
        }
        return this.slaAction;
    }

    public boolean isSetSLAAction() {
        return ((this.slaAction!= null)&&(!this.slaAction.isEmpty()));
    }

    public void unsetSLAAction() {
        this.slaAction = null;
    }

}
