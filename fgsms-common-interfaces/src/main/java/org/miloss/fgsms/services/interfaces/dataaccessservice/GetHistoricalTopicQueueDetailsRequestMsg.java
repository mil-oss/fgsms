
package org.miloss.fgsms.services.interfaces.dataaccessservice;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.miloss.fgsms.services.interfaces.common.SecurityWrapper;
import org.miloss.fgsms.services.interfaces.common.TimeRange;


/**
 * Must input either (URI AND topic/queue name) OR (URI AND cannoicalname)
 * 
 * <p>Java class for GetHistoricalTopicQueueDetailsRequestMsg complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetHistoricalTopicQueueDetailsRequestMsg">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="classification" type="{urn:org:miloss:fgsms:services:interfaces:common}SecurityWrapper"/>
 *         &lt;element name="range" type="{urn:org:miloss:fgsms:services:interfaces:common}TimeRange"/>
 *         &lt;element name="uri" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="queuetopicname" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="queuetopiccanonicalname" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetHistoricalTopicQueueDetailsRequestMsg", propOrder = {
    "classification",
    "range",
    "uri",
    "queuetopicname",
    "queuetopiccanonicalname"
})
public class GetHistoricalTopicQueueDetailsRequestMsg
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(required = true, nillable = true)
    protected SecurityWrapper classification;
    @XmlElement(required = true)
    protected TimeRange range;
    @XmlElement(required = true)
    protected String uri;
    @XmlElement(required = true)
    protected String queuetopicname;
    @XmlElement(required = true)
    protected String queuetopiccanonicalname;

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

    public boolean isSetRange() {
        return (this.range!= null);
    }

    /**
     * Gets the value of the uri property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUri() {
        return uri;
    }

    /**
     * Sets the value of the uri property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUri(String value) {
        this.uri = value;
    }

    public boolean isSetUri() {
        return (this.uri!= null);
    }

    /**
     * Gets the value of the queuetopicname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getQueuetopicname() {
        return queuetopicname;
    }

    /**
     * Sets the value of the queuetopicname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setQueuetopicname(String value) {
        this.queuetopicname = value;
    }

    public boolean isSetQueuetopicname() {
        return (this.queuetopicname!= null);
    }

    /**
     * Gets the value of the queuetopiccanonicalname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getQueuetopiccanonicalname() {
        return queuetopiccanonicalname;
    }

    /**
     * Sets the value of the queuetopiccanonicalname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setQueuetopiccanonicalname(String value) {
        this.queuetopiccanonicalname = value;
    }

    public boolean isSetQueuetopiccanonicalname() {
        return (this.queuetopiccanonicalname!= null);
    }

}
