
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
 *                 collection of user identities
 *             
 * 
 * <p>Java class for ArrayOfUserIdentity complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfUserIdentity">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="UserIdentity" type="{urn:org:miloss:fgsms:services:interfaces:policyConfiguration}UserIdentity" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfUserIdentity", propOrder = {
    "userIdentity"
})
public class ArrayOfUserIdentity
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "UserIdentity", required = true, nillable = true)
    protected List<UserIdentity> userIdentity;

    /**
     * Gets the value of the userIdentity property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the userIdentity property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getUserIdentity().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link UserIdentity }
     * 
     * 
     */
    public List<UserIdentity> getUserIdentity() {
        if (userIdentity == null) {
            userIdentity = new ArrayList<UserIdentity>();
        }
        return this.userIdentity;
    }

    public boolean isSetUserIdentity() {
        return ((this.userIdentity!= null)&&(!this.userIdentity.isEmpty()));
    }

    public void unsetUserIdentity() {
        this.userIdentity = null;
    }

}
