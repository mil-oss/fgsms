
package org.miloss.fgsms.services.interfaces.automatedreportingservice;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import java.util.Calendar;
import org.miloss.fgsms.services.interfaces.policyconfiguration.SLAAction;


/**
 * <p>Java class for ReportDefinition complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ReportDefinition">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="friendlyName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="enabled" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="jobId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="owner" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="lastRanAt" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="schedule" type="{urn:org:miloss:fgsms:services:interfaces:automatedReportingService}scheduleDefinition"/>
 *         &lt;element name="notifications" type="{urn:org:miloss:fgsms:services:interfaces:policyConfiguration}SLAAction" maxOccurs="unbounded"/>
 *         &lt;choice>
 *           &lt;element ref="{urn:org:miloss:fgsms:services:interfaces:automatedReportingService}ExportCSVDataRequestMsg"/>
 *           &lt;element ref="{urn:org:miloss:fgsms:services:interfaces:automatedReportingService}ExportDataRequestMsg"/>
 *         &lt;/choice>
 *         &lt;element name="additionalReaders" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ReportDefinition", propOrder = {
    "friendlyName",
    "enabled",
    "jobId",
    "owner",
    "lastRanAt",
    "schedule",
    "notifications",
    "exportCSVDataRequestMsg",
    "exportDataRequestMsg",
    "additionalReaders"
})
@XmlRootElement
public class ReportDefinition
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(required = true, nillable = true)
    protected String friendlyName;
    protected boolean enabled;
    @XmlElement(required = false, nillable = true)
    protected String jobId;
    @XmlElement(required = false, nillable = true)
    protected String owner;
    @XmlElement(required = true, nillable = true)
    @XmlSchemaType(name = "dateTime")
    protected Calendar lastRanAt;
    @XmlElement(required = true)
    protected ScheduleDefinition schedule;
    @XmlElement(required = false, nillable = true)
    protected List<SLAAction> notifications;
    @XmlElement(name = "ExportCSVDataRequestMsg", nillable = true)
    protected ExportCSVDataRequestMsg exportCSVDataRequestMsg;
    @XmlElement(name = "ExportDataRequestMsg", nillable = true)
    protected ExportDataRequestMsg exportDataRequestMsg;
    @XmlElement(required = false, nillable = true)
    protected List<String> additionalReaders;

    /**
     * Gets the value of the friendlyName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFriendlyName() {
        return friendlyName;
    }

    /**
     * Sets the value of the friendlyName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFriendlyName(String value) {
        this.friendlyName = value;
    }

    public boolean isSetFriendlyName() {
        return (this.friendlyName!= null);
    }

    /**
     * Gets the value of the enabled property.
     * 
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Sets the value of the enabled property.
     * 
     */
    public void setEnabled(boolean value) {
        this.enabled = value;
    }

    public boolean isSetEnabled() {
        return true;
    }

    /**
     * Gets the value of the jobId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getJobId() {
        return jobId;
    }

    /**
     * Sets the value of the jobId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setJobId(String value) {
        this.jobId = value;
    }

    public boolean isSetJobId() {
        return (this.jobId!= null);
    }

    /**
     * Gets the value of the owner property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOwner() {
        return owner;
    }

    /**
     * Sets the value of the owner property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOwner(String value) {
        this.owner = value;
    }

    public boolean isSetOwner() {
        return (this.owner!= null);
    }

    /**
     * Gets the value of the lastRanAt property.
     * 
     * @return
     *     possible object is
     *     {@link Calendar }
     *     
     */
    public Calendar getLastRanAt() {
        return lastRanAt;
    }

    /**
     * Sets the value of the lastRanAt property.
     * 
     * @param value
     *     allowed object is
     *     {@link Calendar }
     *     
     */
    public void setLastRanAt(Calendar value) {
        this.lastRanAt = value;
    }

    public boolean isSetLastRanAt() {
        return (this.lastRanAt!= null);
    }

    /**
     * Gets the value of the schedule property.
     * 
     * @return
     *     possible object is
     *     {@link ScheduleDefinition }
     *     
     */
    public ScheduleDefinition getSchedule() {
        return schedule;
    }

    /**
     * Sets the value of the schedule property.
     * 
     * @param value
     *     allowed object is
     *     {@link ScheduleDefinition }
     *     
     */
    public void setSchedule(ScheduleDefinition value) {
        this.schedule = value;
    }

    public boolean isSetSchedule() {
        return (this.schedule!= null);
    }

    /**
     * Gets the value of the notifications property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the notifications property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getNotifications().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SLAAction }
     * 
     * 
     */
    public List<SLAAction> getNotifications() {
        if (notifications == null) {
            notifications = new ArrayList<SLAAction>();
        }
        return this.notifications;
    }

    public boolean isSetNotifications() {
        return ((this.notifications!= null)&&(!this.notifications.isEmpty()));
    }

    public void unsetNotifications() {
        this.notifications = null;
    }

    /**
     * Gets the value of the exportCSVDataRequestMsg property.
     * 
     * @return
     *     possible object is
     *     {@link ExportCSVDataRequestMsg }
     *     
     */
    public ExportCSVDataRequestMsg getExportCSVDataRequestMsg() {
        return exportCSVDataRequestMsg;
    }

    /**
     * Sets the value of the exportCSVDataRequestMsg property.
     * 
     * @param value
     *     allowed object is
     *     {@link ExportCSVDataRequestMsg }
     *     
     */
    public void setExportCSVDataRequestMsg(ExportCSVDataRequestMsg value) {
        this.exportCSVDataRequestMsg = value;
    }

    public boolean isSetExportCSVDataRequestMsg() {
        return (this.exportCSVDataRequestMsg!= null);
    }

    /**
     * Gets the value of the exportDataRequestMsg property.
     * 
     * @return
     *     possible object is
     *     {@link ExportDataRequestMsg }
     *     
     */
    public ExportDataRequestMsg getExportDataRequestMsg() {
        return exportDataRequestMsg;
    }

    /**
     * Sets the value of the exportDataRequestMsg property.
     * 
     * @param value
     *     allowed object is
     *     {@link ExportDataRequestMsg }
     *     
     */
    public void setExportDataRequestMsg(ExportDataRequestMsg value) {
        this.exportDataRequestMsg = value;
    }

    public boolean isSetExportDataRequestMsg() {
        return (this.exportDataRequestMsg!= null);
    }

    /**
     * Gets the value of the additionalReaders property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the additionalReaders property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAdditionalReaders().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getAdditionalReaders() {
        if (additionalReaders == null) {
            additionalReaders = new ArrayList<String>();
        }
        return this.additionalReaders;
    }

    public boolean isSetAdditionalReaders() {
        return ((this.additionalReaders!= null)&&(!this.additionalReaders.isEmpty()));
    }

    public void unsetAdditionalReaders() {
        this.additionalReaders = null;
    }

}
