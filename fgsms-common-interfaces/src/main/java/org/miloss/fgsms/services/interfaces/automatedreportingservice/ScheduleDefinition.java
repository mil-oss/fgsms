
package org.miloss.fgsms.services.interfaces.automatedreportingservice;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for scheduleDefinition complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="scheduleDefinition">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="triggers" type="{urn:org:miloss:fgsms:services:interfaces:automatedReportingService}abstractSchedule" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "scheduleDefinition", propOrder = {
    "triggers"
})
@XmlSeeAlso(value = {DailySchedule.class, WeeklySchedule.class, MonthlySchedule.class, OneTimeSchedule.class,ImmediateSchedule.class})
public class ScheduleDefinition
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(required = true)
    protected List<AbstractSchedule> triggers;

    /**
     * Gets the value of the triggers property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the triggers property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTriggers().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AbstractSchedule }
     * 
     * 
     */
    public List<AbstractSchedule> getTriggers() {
        if (triggers == null) {
            triggers = new ArrayList<AbstractSchedule>();
        }
        return this.triggers;
    }

    public boolean isSetTriggers() {
        return ((this.triggers!= null)&&(!this.triggers.isEmpty()));
    }

    public void unsetTriggers() {
        this.triggers = null;
    }

}
