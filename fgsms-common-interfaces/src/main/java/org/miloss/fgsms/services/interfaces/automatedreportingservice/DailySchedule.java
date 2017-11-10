
package org.miloss.fgsms.services.interfaces.automatedreportingservice;

import java.io.Serializable;
import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for dailySchedule complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="dailySchedule">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:org:miloss:fgsms:services:interfaces:automatedReportingService}abstractSchedule">
 *       &lt;sequence>
 *         &lt;element name="reoccurs" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "dailySchedule", propOrder = {
    "reoccurs"
})
public class DailySchedule
    extends AbstractSchedule
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(required = true, defaultValue = "1")
    protected BigInteger reoccurs;

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

}
