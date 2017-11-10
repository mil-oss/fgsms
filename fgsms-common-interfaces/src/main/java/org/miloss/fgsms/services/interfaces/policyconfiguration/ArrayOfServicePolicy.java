
package org.miloss.fgsms.services.interfaces.policyconfiguration;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * collection of service policies
 * 
 * <p>Java class for ArrayOfServicePolicy complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfServicePolicy">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ServicePolicy" type="{urn:org:miloss:fgsms:services:interfaces:policyConfiguration}ServicePolicy" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfServicePolicy", propOrder = {
    "servicePolicy"
})
public class ArrayOfServicePolicy
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "ServicePolicy", required = true, nillable = true)
    protected List<ServicePolicy> servicePolicy;

    /**
     * Gets the value of the servicePolicy property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the servicePolicy property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getServicePolicy().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ServicePolicy }
     * 
     * 
     */
    public List<ServicePolicy> getServicePolicy() {
        if (servicePolicy == null) {
            servicePolicy = new ArrayList<ServicePolicy>();
        }
        return this.servicePolicy;
    }

    public boolean isSetServicePolicy() {
        return ((this.servicePolicy!= null)&&(!this.servicePolicy.isEmpty()));
    }

    public void unsetServicePolicy() {
        this.servicePolicy = null;
    }

}
