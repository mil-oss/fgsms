
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
 * <p>Java class for GetQuickStatsAllResponseMsg complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetQuickStatsAllResponseMsg">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="classification" type="{urn:org:miloss:fgsms:services:interfaces:common}SecurityWrapper"/>
 *         &lt;element name="QuickStatURIWrapper" type="{urn:org:miloss:fgsms:services:interfaces:dataAccessService}QuickStatURIWrapper" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetQuickStatsAllResponseMsg", propOrder = {
    "classification",
    "quickStatURIWrapper"
})
public class GetQuickStatsAllResponseMsg
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(required = true, nillable = true)
    protected SecurityWrapper classification;
    @XmlElement(name = "QuickStatURIWrapper")
    protected List<QuickStatURIWrapper> quickStatURIWrapper;

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
     * Gets the value of the quickStatURIWrapper property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the quickStatURIWrapper property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getQuickStatURIWrapper().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link QuickStatURIWrapper }
     * 
     * 
     */
    public List<QuickStatURIWrapper> getQuickStatURIWrapper() {
        if (quickStatURIWrapper == null) {
            quickStatURIWrapper = new ArrayList<QuickStatURIWrapper>();
        }
        return this.quickStatURIWrapper;
    }

    public boolean isSetQuickStatURIWrapper() {
        return ((this.quickStatURIWrapper!= null)&&(!this.quickStatURIWrapper.isEmpty()));
    }

    public void unsetQuickStatURIWrapper() {
        this.quickStatURIWrapper = null;
    }

}
