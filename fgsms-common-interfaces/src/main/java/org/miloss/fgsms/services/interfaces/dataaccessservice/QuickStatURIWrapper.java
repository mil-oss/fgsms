
package org.miloss.fgsms.services.interfaces.dataaccessservice;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * wrapper
 * 
 * <p>Java class for QuickStatURIWrapper complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="QuickStatURIWrapper">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="uri" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="QuickStatWrapper" type="{urn:org:miloss:fgsms:services:interfaces:dataAccessService}QuickStatWrapper" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "QuickStatURIWrapper", propOrder = {
    "uri",
    "quickStatWrapper"
})
public class QuickStatURIWrapper
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(required = true)
    protected String uri;
    @XmlElement(name = "QuickStatWrapper")
    protected List<QuickStatWrapper> quickStatWrapper;

    /**
     * Gets the value of the uri property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUri() {
        return uri;
    }

    /**
     * Sets the value of the uri property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUri(String value) {
        this.uri = value;
    }

    public boolean isSetUri() {
        return (this.uri!= null);
    }

    /**
     * Gets the value of the quickStatWrapper property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the quickStatWrapper property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getQuickStatWrapper().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link QuickStatWrapper }
     * 
     * 
     */
    public List<QuickStatWrapper> getQuickStatWrapper() {
        if (quickStatWrapper == null) {
            quickStatWrapper = new ArrayList<QuickStatWrapper>();
        }
        return this.quickStatWrapper;
    }

    public boolean isSetQuickStatWrapper() {
        return ((this.quickStatWrapper!= null)&&(!this.quickStatWrapper.isEmpty()));
    }

    public void unsetQuickStatWrapper() {
        this.quickStatWrapper = null;
    }

}
