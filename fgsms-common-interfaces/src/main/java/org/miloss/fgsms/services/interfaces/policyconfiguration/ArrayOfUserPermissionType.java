
package org.miloss.fgsms.services.interfaces.policyconfiguration;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * collection of user permissions
 * 
 * <p>Java class for ArrayOfUserPermissionType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfUserPermissionType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="UserPermissionType" type="{urn:org:miloss:fgsms:services:interfaces:policyConfiguration}UserPermissionType" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfUserPermissionType", propOrder = {
    "userPermissionType"
})
public class ArrayOfUserPermissionType
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "UserPermissionType", required = false, nillable = true)
    protected List<UserPermissionType> userPermissionType;

    /**
     * Gets the value of the userPermissionType property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the userPermissionType property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getUserPermissionType().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link UserPermissionType }
     * 
     * 
     */
    public List<UserPermissionType> getUserPermissionType() {
        if (userPermissionType == null) {
            userPermissionType = new ArrayList<UserPermissionType>();
        }
        return this.userPermissionType;
    }

    public boolean isSetUserPermissionType() {
        return ((this.userPermissionType!= null)&&(!this.userPermissionType.isEmpty()));
    }

    public void unsetUserPermissionType() {
        this.userPermissionType = null;
    }

}
