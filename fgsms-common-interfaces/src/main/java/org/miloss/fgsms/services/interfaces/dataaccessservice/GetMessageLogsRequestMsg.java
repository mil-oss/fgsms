
package org.miloss.fgsms.services.interfaces.dataaccessservice;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.miloss.fgsms.services.interfaces.common.SecurityWrapper;
import org.miloss.fgsms.services.interfaces.common.TimeRange;


/**
 * 
 *             GetMessageLogsRequest
 *             Optional parameters:
 *             URL, shows only transactions from a matching modified url
 *             monitorhostname
 *             servicehostname
 *             agenttype
 *             
 *             Required:
 *             Time Range
 *             Records
 *             Offset
 *             faultsOnly
 *             SLAfaultsOnly
 *             
 *             
 * 
 * <p>Java class for GetMessageLogsRequestMsg complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetMessageLogsRequestMsg">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="classification" type="{urn:org:miloss:fgsms:services:interfaces:common}SecurityWrapper"/>
 *         &lt;element name="Range" type="{urn:org:miloss:fgsms:services:interfaces:common}TimeRange"/>
 *         &lt;element name="URL" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="monitorhostname" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="servicehostname" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="agentType" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="offset" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="records" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="faultsOnly" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="slaViolationsOnly" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetMessageLogsRequestMsg", propOrder = {
    "classification",
    "range",
    "url",
    "monitorhostname",
    "servicehostname",
    "agentType",
    "offset",
    "records",
    "faultsOnly",
    "slaViolationsOnly"
})
public class GetMessageLogsRequestMsg
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(required = true, nillable = true)
    protected SecurityWrapper classification;
    @XmlElement(name = "Range", required = true, nillable = true)
    protected TimeRange range;
    @XmlElement(name = "URL", required = true, nillable = true)
    protected String url;
    @XmlElement(required = true, nillable = true)
    protected String monitorhostname;
    @XmlElement(required = true, nillable = true)
    protected String servicehostname;
    @XmlElement(required = true, nillable = true)
    protected String agentType;
    protected Integer offset;
    protected int records;
    protected boolean faultsOnly;
    protected boolean slaViolationsOnly;

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
     * Gets the value of the url property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getURL() {
        return url;
    }

    /**
     * Sets the value of the url property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setURL(String value) {
        this.url = value;
    }

    public boolean isSetURL() {
        return (this.url!= null);
    }

    /**
     * Gets the value of the monitorhostname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMonitorhostname() {
        return monitorhostname;
    }

    /**
     * Sets the value of the monitorhostname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMonitorhostname(String value) {
        this.monitorhostname = value;
    }

    public boolean isSetMonitorhostname() {
        return (this.monitorhostname!= null);
    }

    /**
     * Gets the value of the servicehostname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getServicehostname() {
        return servicehostname;
    }

    /**
     * Sets the value of the servicehostname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setServicehostname(String value) {
        this.servicehostname = value;
    }

    public boolean isSetServicehostname() {
        return (this.servicehostname!= null);
    }

    /**
     * Gets the value of the agentType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAgentType() {
        return agentType;
    }

    /**
     * Sets the value of the agentType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAgentType(String value) {
        this.agentType = value;
    }

    public boolean isSetAgentType() {
        return (this.agentType!= null);
    }

    /**
     * Gets the value of the offset property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getOffset() {
        return offset;
    }

    /**
     * Sets the value of the offset property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setOffset(Integer value) {
        this.offset = value;
    }

    public boolean isSetOffset() {
        return (this.offset!= null);
    }

    /**
     * Gets the value of the records property.
     * 
     */
    public int getRecords() {
        return records;
    }

    /**
     * Sets the value of the records property.
     * 
     */
    public void setRecords(int value) {
        this.records = value;
    }

    public boolean isSetRecords() {
        return true;
    }

    /**
     * Gets the value of the faultsOnly property.
     * 
     */
    public boolean isFaultsOnly() {
        return faultsOnly;
    }

    /**
     * Sets the value of the faultsOnly property.
     * 
     */
    public void setFaultsOnly(boolean value) {
        this.faultsOnly = value;
    }

    public boolean isSetFaultsOnly() {
        return true;
    }

    /**
     * Gets the value of the slaViolationsOnly property.
     * 
     */
    public boolean isSlaViolationsOnly() {
        return slaViolationsOnly;
    }

    /**
     * Sets the value of the slaViolationsOnly property.
     * 
     */
    public void setSlaViolationsOnly(boolean value) {
        this.slaViolationsOnly = value;
    }

    public boolean isSetSlaViolationsOnly() {
        return true;
    }

}
