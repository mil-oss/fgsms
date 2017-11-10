
package org.miloss.fgsms.services.interfaces.automatedreportingservice;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for weeklySchedule complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="weeklySchedule">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:org:miloss:fgsms:services:interfaces:automatedReportingService}abstractSchedule">
 *       &lt;sequence>
 *         &lt;element name="reoccurs" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *         &lt;element name="DayOfTheWeekIs" type="{urn:org:miloss:fgsms:services:interfaces:automatedReportingService}daynames" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "weeklySchedule", propOrder = {
    "reoccurs",
    "dayOfTheWeekIs"
})
public class WeeklySchedule
    extends AbstractSchedule
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(required = true, defaultValue = "1")
    protected BigInteger reoccurs;
    @XmlElement(name = "DayOfTheWeekIs", required = true)
    protected List<Daynames> dayOfTheWeekIs;

    /**
     * Gets the value of the reoccurs property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getReoccurs() {
        return reoccurs;
    }

    /**
     * Sets the value of the reoccurs property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setReoccurs(BigInteger value) {
        this.reoccurs = value;
    }

    public boolean isSetReoccurs() {
        return (this.reoccurs!= null);
    }

    /**
     * Gets the value of the dayOfTheWeekIs property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the dayOfTheWeekIs property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDayOfTheWeekIs().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Daynames }
     * 
     * 
     */
    public List<Daynames> getDayOfTheWeekIs() {
        if (dayOfTheWeekIs == null) {
            dayOfTheWeekIs = new ArrayList<Daynames>();
        }
        return this.dayOfTheWeekIs;
    }

    public boolean isSetDayOfTheWeekIs() {
        return ((this.dayOfTheWeekIs!= null)&&(!this.dayOfTheWeekIs.isEmpty()));
    }

    public void unsetDayOfTheWeekIs() {
        this.dayOfTheWeekIs = null;
    }

}
