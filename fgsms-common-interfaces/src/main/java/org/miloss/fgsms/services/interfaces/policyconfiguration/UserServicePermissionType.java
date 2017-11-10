
package org.miloss.fgsms.services.interfaces.policyconfiguration;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 *                 User/service permission type
 *             
 * 
 * <p>Java class for UserServicePermissionType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UserServicePermissionType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="URL" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="right" type="{urn:org:miloss:fgsms:services:interfaces:policyConfiguration}RightEnum"/>
 *         &lt;element name="user" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UserServicePermissionType", propOrder = {
    "url",
    "right",
    "user"
})
public class UserServicePermissionType
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "URL", required = true, nillable = true)
    protected String url;
    @XmlElement(required = true)
    protected RightEnum right;
    @XmlElement(required = true, nillable = true)
    protected String user;

    /**
     * Gets the value of the url property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getURL() {
        return url;
    }

    /**
     * Sets the value of the url property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setURL(String value) {
        this.url = value;
    }

    public boolean isSetURL() {
        return (this.url!= null);
    }

    /**
     * Gets the value of the right property.
     * 
     * @return
     *     possible object is
     *     {@link RightEnum }
     *     
     */
    public RightEnum getRight() {
        return right;
    }

    /**
     * Sets the value of the right property.
     * 
     * @param value
     *     allowed object is
     *     {@link RightEnum }
     *     
     */
    public void setRight(RightEnum value) {
        this.right = value;
    }

    public boolean isSetRight() {
        return (this.right!= null);
    }

    /**
     * Gets the value of the user property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUser() {
        return user;
    }

    /**
     * Sets the value of the user property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUser(String value) {
        this.user = value;
    }

    public boolean isSetUser() {
        return (this.user!= null);
    }

}
