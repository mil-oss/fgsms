
package org.miloss.fgsms.services.interfaces.policyconfiguration;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.miloss.fgsms.services.interfaces.common.SecurityWrapper;


/**
 * 
 *          request message, sets or replaces specific parameters, items not specified will not be affected  requires global admin privledges
 *            
 * 
 * <p>Java class for SetGeneralSettingsRequestMsg complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SetGeneralSettingsRequestMsg">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="classification" type="{urn:org:miloss:fgsms:services:interfaces:common}SecurityWrapper"/>
 *         &lt;element name="keyNameValueEnc" type="{urn:org:miloss:fgsms:services:interfaces:policyConfiguration}keyNameValueEnc" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SetGeneralSettingsRequestMsg", propOrder = {
    "classification",
    "keyNameValueEnc"
})
public class SetGeneralSettingsRequestMsg
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(required = true, nillable = true)
    protected SecurityWrapper classification;
    @XmlElement(required = true)
    protected List<KeyNameValueEnc> keyNameValueEnc;

    /**
     * Gets the value of the classification property.
     * 
     * @return
     *     possible object is
     *     {@link SecurityWrapper }
     *     
     */
    public SecurityWrapper getClassification() {
        return classification;
    }

    /**
     * Sets the value of the classification property.
     * 
     * @param value
     *     allowed object is
     *     {@link SecurityWrapper }
     *     
     */
    public void setClassification(SecurityWrapper value) {
        this.classification = value;
    }

    public boolean isSetClassification() {
        return (this.classification!= null);
    }

    /**
     * Gets the value of the keyNameValueEnc property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the keyNameValueEnc property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getKeyNameValueEnc().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link KeyNameValueEnc }
     * 
     * 
     */
    public List<KeyNameValueEnc> getKeyNameValueEnc() {
        if (keyNameValueEnc == null) {
            keyNameValueEnc = new ArrayList<KeyNameValueEnc>();
        }
        return this.keyNameValueEnc;
    }

    public boolean isSetKeyNameValueEnc() {
        return ((this.keyNameValueEnc!= null)&&(!this.keyNameValueEnc.isEmpty()));
    }

    public void unsetKeyNameValueEnc() {
        this.keyNameValueEnc = null;
    }

}
