
package org.miloss.fgsms.services.interfaces.reportingservice;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.miloss.fgsms.services.interfaces.common.SecurityWrapper;
import org.miloss.fgsms.services.interfaces.common.TimeRange;


/**
 * request
 * 
 * <p>Java class for ExportDataRequestMsg complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ExportDataRequestMsg">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="classification" type="{urn:org:miloss:fgsms:services:interfaces:common}SecurityWrapper"/>
 *         &lt;element name="AllServices" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="ReportTypes" type="{urn:org:miloss:fgsms:services:interfaces:reportingService}ArrayOfReportTypeContainer"/>
 *         &lt;element name="URLs" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded"/>
 *         &lt;element name="range" type="{urn:org:miloss:fgsms:services:interfaces:common}TimeRange"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ExportDataRequestMsg", propOrder = {
    "classification",
    "allServices",
    "reportTypes",
    "urLs",
    "range"
})
public class ExportDataRequestMsg {

    @XmlElement(required = true, nillable = true)
    protected SecurityWrapper classification;
    @XmlElement(name = "AllServices")
    protected boolean allServices=false;
    @XmlElement(name = "ReportTypes", required = true, nillable = true)
    protected ArrayOfReportTypeContainer reportTypes;
    @XmlElement(name = "URLs", required = false, nillable = true)
    protected List<String> urLs;
    @XmlElement(required = true, nillable = true)
    protected TimeRange range;

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

    /**
     * Gets the value of the allServices property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isAllServices() {
        return allServices;
    }

    /**
     * Sets the value of the allServices property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAllServices(boolean value) {
        this.allServices = value;
    }

    /**
     * Gets the value of the reportTypes property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfReportTypeContainer }
     *     
     */
    public ArrayOfReportTypeContainer getReportTypes() {
        return reportTypes;
    }

    /**
     * Sets the value of the reportTypes property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfReportTypeContainer }
     *     
     */
    public void setReportTypes(ArrayOfReportTypeContainer value) {
        this.reportTypes = value;
    }

    /**
     * Gets the value of the urLs property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the urLs property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getURLs().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getURLs() {
        if (urLs == null) {
            urLs = new ArrayList<String>();
        }
        return this.urLs;
    }

    /**
     * Gets the value of the range property.
     * 
     * @return
     *     possible object is
     *     {@link TimeRange }
     *     
     */
    public TimeRange getRange() {
        return range;
    }

    /**
     * Sets the value of the range property.
     * 
     * @param value
     *     allowed object is
     *     {@link TimeRange }
     *     
     */
    public void setRange(TimeRange value) {
        this.range = value;
    }

}
