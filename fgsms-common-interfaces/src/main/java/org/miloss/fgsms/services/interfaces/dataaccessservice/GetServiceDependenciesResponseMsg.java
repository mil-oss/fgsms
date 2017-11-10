
package org.miloss.fgsms.services.interfaces.dataaccessservice;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.miloss.fgsms.services.interfaces.common.SecurityWrapper;


/**
 * response message
 * 
 * <p>Java class for GetServiceDependenciesResponseMsg complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetServiceDependenciesResponseMsg">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="classification" type="{urn:org:miloss:fgsms:services:interfaces:common}SecurityWrapper"/>
 *         &lt;element name="idependon" type="{urn:org:miloss:fgsms:services:interfaces:dataAccessService}dependencies" maxOccurs="unbounded"/>
 *         &lt;element name="dependonme" type="{urn:org:miloss:fgsms:services:interfaces:dataAccessService}dependencies" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetServiceDependenciesResponseMsg", propOrder = {
    "classification",
    "idependon",
    "dependonme"
})
public class GetServiceDependenciesResponseMsg
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(required = true, nillable = true)
    protected SecurityWrapper classification;
    @XmlElement(required = false, nillable = true)
    protected List<Dependencies> idependon;
    @XmlElement(required = false, nillable = true)
    protected List<Dependencies> dependonme;

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
     * Gets the value of the idependon property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the idependon property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getIdependon().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Dependencies }
     * 
     * 
     */
    public List<Dependencies> getIdependon() {
        if (idependon == null) {
            idependon = new ArrayList<Dependencies>();
        }
        return this.idependon;
    }

    public boolean isSetIdependon() {
        return ((this.idependon!= null)&&(!this.idependon.isEmpty()));
    }

    public void unsetIdependon() {
        this.idependon = null;
    }

    /**
     * Gets the value of the dependonme property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the dependonme property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDependonme().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Dependencies }
     * 
     * 
     */
    public List<Dependencies> getDependonme() {
        if (dependonme == null) {
            dependonme = new ArrayList<Dependencies>();
        }
        return this.dependonme;
    }

    public boolean isSetDependonme() {
        return ((this.dependonme!= null)&&(!this.dependonme.isEmpty()));
    }

    public void unsetDependonme() {
        this.dependonme = null;
    }

}
