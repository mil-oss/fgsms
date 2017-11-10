
package org.miloss.fgsms.services.interfaces.policyconfiguration;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * collection of user information
 * 
 * <p>Java class for ArrayOfUserInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfUserInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="UserInfo" type="{urn:org:miloss:fgsms:services:interfaces:policyConfiguration}UserInfo" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfUserInfo", propOrder = {
    "userInfo"
})
public class ArrayOfUserInfo
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "UserInfo", required = true, nillable = true)
    protected List<UserInfo> userInfo;

    /**
     * Gets the value of the userInfo property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the userInfo property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getUserInfo().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link UserInfo }
     * 
     * 
     */
    public List<UserInfo> getUserInfo() {
        if (userInfo == null) {
            userInfo = new ArrayList<UserInfo>();
        }
        return this.userInfo;
    }

    public boolean isSetUserInfo() {
        return ((this.userInfo!= null)&&(!this.userInfo.isEmpty()));
    }

    public void unsetUserInfo() {
        this.userInfo = null;
    }

}
