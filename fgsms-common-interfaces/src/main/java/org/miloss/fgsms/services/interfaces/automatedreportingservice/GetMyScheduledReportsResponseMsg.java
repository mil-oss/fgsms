
package org.miloss.fgsms.services.interfaces.automatedreportingservice;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.miloss.fgsms.services.interfaces.common.SecurityWrapper;


/**
 * <p>Java class for GetMyScheduledReportsResponseMsg complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetMyScheduledReportsResponseMsg">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="classification" type="{urn:org:miloss:fgsms:services:interfaces:common}SecurityWrapper"/>
 *         &lt;element name="CompletedJobs" type="{urn:org:miloss:fgsms:services:interfaces:automatedReportingService}ExistingReportDefitions" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetMyScheduledReportsResponseMsg", propOrder = {
    "classification",
    "completedJobs"
})
public class GetMyScheduledReportsResponseMsg
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(required = true, nillable = true)
    protected SecurityWrapper classification;
    @XmlElement(name = "CompletedJobs")
    protected List<ExistingReportDefitions> completedJobs;

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
     * Gets the value of the completedJobs property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the completedJobs property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCompletedJobs().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ExistingReportDefitions }
     * 
     * 
     */
    public List<ExistingReportDefitions> getCompletedJobs() {
        if (completedJobs == null) {
            completedJobs = new ArrayList<ExistingReportDefitions>();
        }
        return this.completedJobs;
    }

    public boolean isSetCompletedJobs() {
        return ((this.completedJobs!= null)&&(!this.completedJobs.isEmpty()));
    }

    public void unsetCompletedJobs() {
        this.completedJobs = null;
    }

     public ReportDefinition findJobWithIdOf(String jobId) {
        ReportDefinition rd = null;
        for (int i = 0; i < getCompletedJobs().size(); i++) {
            if (getCompletedJobs().get(i).getJob().getJobId().equals(jobId)) {
                rd = getCompletedJobs().get(i).getJob();
                return rd;
}
        }
        return rd;
    }

}
