
package org.miloss.fgsms.services.interfaces.automatedreportingservice;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for monthlySchedule complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="monthlySchedule">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:org:miloss:fgsms:services:interfaces:automatedReportingService}abstractSchedule">
 *       &lt;sequence>
 *         &lt;element name="MonthNameIs" type="{urn:org:miloss:fgsms:services:interfaces:automatedReportingService}monthnames" maxOccurs="12"/>
 *         &lt;element name="DayOfTheMonthIs" type="{http://www.w3.org/2001/XMLSchema}int" maxOccurs="31"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "monthlySchedule", propOrder = {
    "monthNameIs",
    "dayOfTheMonthIs"
})
public class MonthlySchedule
    extends AbstractSchedule
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "MonthNameIs", required = true)
    protected List<Monthnames> monthNameIs;
    @XmlElement(name = "DayOfTheMonthIs", type = Integer.class)
    protected List<Integer> dayOfTheMonthIs;

    /**
     * Gets the value of the monthNameIs property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the monthNameIs property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMonthNameIs().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Monthnames }
     * 
     * 
     */
    public List<Monthnames> getMonthNameIs() {
        if (monthNameIs == null) {
            monthNameIs = new ArrayList<Monthnames>();
        }
        return this.monthNameIs;
    }

    public boolean isSetMonthNameIs() {
        return ((this.monthNameIs!= null)&&(!this.monthNameIs.isEmpty()));
    }

    public void unsetMonthNameIs() {
        this.monthNameIs = null;
    }

    /**
     * Gets the value of the dayOfTheMonthIs property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the dayOfTheMonthIs property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDayOfTheMonthIs().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Integer }
     * 
     * 
     */
    public List<Integer> getDayOfTheMonthIs() {
        if (dayOfTheMonthIs == null) {
            dayOfTheMonthIs = new ArrayList<Integer>();
        }
        return this.dayOfTheMonthIs;
    }

    public boolean isSetDayOfTheMonthIs() {
        return ((this.dayOfTheMonthIs!= null)&&(!this.dayOfTheMonthIs.isEmpty()));
    }

    public void unsetDayOfTheMonthIs() {
        this.dayOfTheMonthIs = null;
    }

}
