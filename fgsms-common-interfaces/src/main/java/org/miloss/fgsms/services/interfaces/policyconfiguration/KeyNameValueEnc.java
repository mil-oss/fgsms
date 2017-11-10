
package org.miloss.fgsms.services.interfaces.policyconfiguration;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * provides a wrapper to include a shouldEncrypt field
 * 
 * <p>Java class for keyNameValueEnc complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="keyNameValueEnc">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="keyNameValue" type="{urn:org:miloss:fgsms:services:interfaces:policyConfiguration}keyNameValue"/>
 *         &lt;element name="shouldEncrypt" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "keyNameValueEnc", propOrder = {
    "keyNameValue",
    "shouldEncrypt"
})
public class KeyNameValueEnc
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(required = true)
    protected KeyNameValue keyNameValue;
    protected boolean shouldEncrypt;

    /**
     * Gets the value of the keyNameValue property.
     * 
     * @return
     *     possible object is
     *     {@link KeyNameValue }
     *     
     */
    public KeyNameValue getKeyNameValue() {
        return keyNameValue;
    }

    /**
     * Sets the value of the keyNameValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link KeyNameValue }
     *     
     */
    public void setKeyNameValue(KeyNameValue value) {
        this.keyNameValue = value;
    }

    public boolean isSetKeyNameValue() {
        return (this.keyNameValue!= null);
    }

    /**
     * Gets the value of the shouldEncrypt property.
     * 
     */
    public boolean isShouldEncrypt() {
        return shouldEncrypt;
    }

    /**
     * Sets the value of the shouldEncrypt property.
     * 
     */
    public void setShouldEncrypt(boolean value) {
        this.shouldEncrypt = value;
    }

    public boolean isSetShouldEncrypt() {
        return true;
    }

}
