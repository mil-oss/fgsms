
package org.miloss.fgsms.services.interfaces.policyconfiguration;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * user service permissions
 * 
 * <p>Java class for ArrayOfUserServicePermissionType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfUserServicePermissionType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="UserServicePermissionType" type="{urn:org:miloss:fgsms:services:interfaces:policyConfiguration}UserServicePermissionType" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfUserServicePermissionType", propOrder = {
    "userServicePermissionType"
})
public class ArrayOfUserServicePermissionType
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "UserServicePermissionType", required = false, nillable = true)
    protected List<UserServicePermissionType> userServicePermissionType;

    /**
     * Gets the value of the userServicePermissionType property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the userServicePermissionType property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getUserServicePermissionType().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link UserServicePermissionType }
     * 
     * 
     */
    public List<UserServicePermissionType> getUserServicePermissionType() {
        if (userServicePermissionType == null) {
            userServicePermissionType = new ArrayList<UserServicePermissionType>();
        }
        return this.userServicePermissionType;
    }

    public boolean isSetUserServicePermissionType() {
        return ((this.userServicePermissionType!= null)&&(!this.userServicePermissionType.isEmpty()));
    }

    public void unsetUserServicePermissionType() {
        this.userServicePermissionType = null;
    }

}
