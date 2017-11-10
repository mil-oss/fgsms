
package org.miloss.fgsms.services.interfaces.policyconfiguration;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * This represents a policy for a specifc process running on a machine
 * 
 * Extends the service policy base type
 * 
 * <p>Java class for ProcessPolicy complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ProcessPolicy">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:org:miloss:fgsms:services:interfaces:policyConfiguration}ServicePolicy">
 *       &lt;sequence>
 *         &lt;element name="AlsoKnownAs" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="RecordCPUusage" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="RecordMemoryUsage" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="RecordOpenFileHandles" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ProcessPolicy", propOrder = {
    "alsoKnownAs",
    "recordCPUusage",
    "recordMemoryUsage",
    "recordOpenFileHandles"
})
@XmlRootElement
public class ProcessPolicy
    extends ServicePolicy
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "AlsoKnownAs", required = false)
    protected String alsoKnownAs;
    @XmlElement(name = "RecordCPUusage")
    protected boolean recordCPUusage;
    @XmlElement(name = "RecordMemoryUsage")
    protected boolean recordMemoryUsage;
    @XmlElement(name = "RecordOpenFileHandles")
    protected boolean recordOpenFileHandles;

    /**
     * Gets the value of the alsoKnownAs property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAlsoKnownAs() {
        return alsoKnownAs;
    }

    /**
     * Sets the value of the alsoKnownAs property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAlsoKnownAs(String value) {
        this.alsoKnownAs = value;
    }

    public boolean isSetAlsoKnownAs() {
        return (this.alsoKnownAs!= null);
    }

    /**
     * Gets the value of the recordCPUusage property.
     * 
     */
    public boolean isRecordCPUusage() {
        return recordCPUusage;
    }

    /**
     * Sets the value of the recordCPUusage property.
     * 
     */
    public void setRecordCPUusage(boolean value) {
        this.recordCPUusage = value;
    }

    public boolean isSetRecordCPUusage() {
        return true;
    }

    /**
     * Gets the value of the recordMemoryUsage property.
     * 
     */
    public boolean isRecordMemoryUsage() {
        return recordMemoryUsage;
    }

    /**
     * Sets the value of the recordMemoryUsage property.
     * 
     */
    public void setRecordMemoryUsage(boolean value) {
        this.recordMemoryUsage = value;
    }

    public boolean isSetRecordMemoryUsage() {
        return true;
    }

    /**
     * Gets the value of the recordOpenFileHandles property.
     * 
     */
    public boolean isRecordOpenFileHandles() {
        return recordOpenFileHandles;
    }

    /**
     * Sets the value of the recordOpenFileHandles property.
     * 
     */
    public void setRecordOpenFileHandles(boolean value) {
        this.recordOpenFileHandles = value;
    }

    public boolean isSetRecordOpenFileHandles() {
        return true;
    }

}
