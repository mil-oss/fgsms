
package org.miloss.fgsms.services.interfaces.common;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * All fields must be limited to 128 chars or less
 * 
 * <p>Java class for NameValuePair complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="NameValuePair">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Value" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Encrypted" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="EncryptOnSave" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "NameValuePair", propOrder = {
    "name",
    "value",
    "encrypted",
    "encryptOnSave"
})
public class NameValuePair implements Serializable{
     
     public NameValuePair(){}
     public NameValuePair(String n, String v, boolean isEncrypted, boolean encryptOnsave){
	  name=n;
	  value=v;
	  encrypted=isEncrypted;
	  encryptOnSave=encryptOnsave;
     }

    @XmlElement(name = "Name", required = true)
    protected String name;
    @XmlElement(name = "Value", required = true, nillable = true)
    protected String value;
    @XmlElement(name = "Encrypted", required = true, type = Boolean.class, nillable = true)
    protected Boolean encrypted;
    @XmlElement(name = "EncryptOnSave", required = true, type = Boolean.class, nillable = true)
    protected Boolean encryptOnSave;

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the value property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Gets the value of the encrypted property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isEncrypted() {
        return encrypted;
    }

    /**
     * Sets the value of the encrypted property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setEncrypted(Boolean value) {
        this.encrypted = value;
    }

    /**
     * Gets the value of the encryptOnSave property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isEncryptOnSave() {
        return encryptOnSave;
    }

    /**
     * Sets the value of the encryptOnSave property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setEncryptOnSave(Boolean value) {
        this.encryptOnSave = value;
    }

}
