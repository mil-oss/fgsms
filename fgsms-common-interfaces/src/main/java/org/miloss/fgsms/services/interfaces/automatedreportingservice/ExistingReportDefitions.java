
package org.miloss.fgsms.services.interfaces.automatedreportingservice;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ExistingReportDefitions complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ExistingReportDefitions">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="job" type="{urn:org:miloss:fgsms:services:interfaces:automatedReportingService}ReportDefinition"/>
 *         &lt;element name="reports" type="{urn:org:miloss:fgsms:services:interfaces:automatedReportingService}CompletedJobs" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ExistingReportDefitions", propOrder = {
    "job",
    "reports"
})
public class ExistingReportDefitions
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(required = true)
    protected ReportDefinition job;
    protected List<CompletedJobs> reports;

    /**
     * Gets the value of the job property.
     * 
     * @return
     *     possible object is
     *     {@link ReportDefinition }
     *     
     */
    public ReportDefinition getJob() {
        return job;
    }

    /**
     * Sets the value of the job property.
     * 
     * @param value
     *     allowed object is
     *     {@link ReportDefinition }
     *     
     */
    public void setJob(ReportDefinition value) {
        this.job = value;
    }

    public boolean isSetJob() {
        return (this.job!= null);
    }

    /**
     * Gets the value of the reports property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the reports property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getReports().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CompletedJobs }
     * 
     * 
     */
    public List<CompletedJobs> getReports() {
        if (reports == null) {
            reports = new ArrayList<CompletedJobs>();
        }
        return this.reports;
    }

    public boolean isSetReports() {
        return ((this.reports!= null)&&(!this.reports.isEmpty()));
    }

    public void unsetReports() {
        this.reports = null;
    }

}
