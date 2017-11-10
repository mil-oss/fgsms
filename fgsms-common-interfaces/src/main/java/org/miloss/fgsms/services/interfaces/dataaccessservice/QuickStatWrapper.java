
package org.miloss.fgsms.services.interfaces.dataaccessservice;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.Duration;


/**
 * provides a simple wrapper providing action, duration and the statistics data. done to minimize network usage
 * 
 * <p>Java class for QuickStatWrapper complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="QuickStatWrapper">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="action" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="uptime" type="{http://www.w3.org/2001/XMLSchema}duration"/>
 *         &lt;element name="QuickStatData" type="{urn:org:miloss:fgsms:services:interfaces:dataAccessService}QuickStatData" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "QuickStatWrapper", propOrder = {
    "action",
    "uptime",
    "quickStatData"
})
public class QuickStatWrapper
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(required = true, nillable = true)
    protected String action;
    @XmlElement(required = true, nillable = true)
    protected Duration uptime;
    @XmlElement(name = "QuickStatData", required = false, nillable = true)
    protected List<QuickStatData> quickStatData;

    /**
     * Gets the value of the action property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAction() {
        return action;
    }

    /**
     * Sets the value of the action property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAction(String value) {
        this.action = value;
    }

    public boolean isSetAction() {
        return (this.action!= null);
    }

    /**
     * Gets the value of the uptime property.
     * 
     * @return
     *     possible object is
     *     {@link Duration }
     *     
     */
    public Duration getUptime() {
        return uptime;
    }

    /**
     * Sets the value of the uptime property.
     * 
     * @param value
     *     allowed object is
     *     {@link Duration }
     *     
     */
    public void setUptime(Duration value) {
        this.uptime = value;
    }

    public boolean isSetUptime() {
        return (this.uptime!= null);
    }

    /**
     * Gets the value of the quickStatData property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the quickStatData property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getQuickStatData().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link QuickStatData }
     * 
     * 
     */
    public List<QuickStatData> getQuickStatData() {
        if (quickStatData == null) {
            quickStatData = new ArrayList<QuickStatData>();
        }
        return this.quickStatData;
    }

    public boolean isSetQuickStatData() {
        return ((this.quickStatData!= null)&&(!this.quickStatData.isEmpty()));
    }

    public void unsetQuickStatData() {
        this.quickStatData = null;
    }

}
