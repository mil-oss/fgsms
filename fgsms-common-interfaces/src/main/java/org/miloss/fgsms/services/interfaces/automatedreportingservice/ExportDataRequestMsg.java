
package org.miloss.fgsms.services.interfaces.automatedreportingservice;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.miloss.fgsms.services.interfaces.common.SecurityWrapper;
import org.miloss.fgsms.services.interfaces.reportingservice.ArrayOfReportTypeContainer;


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
 *         &lt;element name="ReportTypes" type="{urn:org:miloss:fgsms:services:interfaces:reportingService}ArrayOfReportTypeContainer"/>
 *         &lt;element name="URLs" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded"/>
 *         &lt;element name="range" type="{urn:org:miloss:fgsms:services:interfaces:automatedReportingService}TimeRangeDiff"/>
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
    "reportTypes",
    "urLs",
    "range"
})
public class ExportDataRequestMsg
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(required = true, nillable = true)
    protected SecurityWrapper classification;
    @XmlElement(name = "ReportTypes", required = true, nillable = true)
    protected ArrayOfReportTypeContainer reportTypes;
    @XmlElement(name = "URLs", required = true, nillable = true)
    protected List<String> urLs;
    @XmlElement(required = true, nillable = true)
    protected TimeRangeDiff range;

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

    public boolean isSetReportTypes() {
        return (this.reportTypes!= null);
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

    public boolean isSetURLs() {
        return ((this.urLs!= null)&&(!this.urLs.isEmpty()));
    }

    public void unsetURLs() {
        this.urLs = null;
    }

    /**
     * Gets the value of the range property.
     * 
     * @return
     *     possible object is
     *     {@link TimeRangeDiff }
     *     
     */
    public TimeRangeDiff getRange() {
        return range;
    }

    /**
     * Sets the value of the range property.
     * 
     * @param value
     *     allowed object is
     *     {@link TimeRangeDiff }
     *     
     */
    public void setRange(TimeRangeDiff value) {
        this.range = value;
    }

    public boolean isSetRange() {
        return (this.range!= null);
    }

}
