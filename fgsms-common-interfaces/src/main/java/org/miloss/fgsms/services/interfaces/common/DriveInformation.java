
package org.miloss.fgsms.services.interfaces.common;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import java.util.Calendar;


/**
 * information about a specific drive or partition
 * 
 * <p>Java class for driveInformation complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="driveInformation">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="operational" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="partition" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="systemid" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="totalspace" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="freespace" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="type" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="timestamp" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="kilobytespersecondDiskRead" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="kilobytespersecondDiskWrite" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="operationalstatus" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "driveInformation", propOrder = {
    "operational",
    "partition",
    "systemid",
    "id",
    "totalspace",
    "freespace",
    "type",
    "timestamp",
    "kilobytespersecondDiskRead",
    "kilobytespersecondDiskWrite",
    "operationalstatus"
})
public class DriveInformation
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    protected boolean operational;
    @XmlElement(required = true)
    protected String partition;
    @XmlElement(required = false)
    protected String systemid;
    @XmlElement(required = false, nillable = true)
    protected String id;
    @XmlElement(required = false, type = Long.class, nillable = true)
    protected Long totalspace;
    @XmlElement(required = false, type = Long.class, nillable = true)
    protected Long freespace;
    @XmlElement(required = false)
    protected String type;
    @XmlElement(required = false, nillable = true)
    @XmlSchemaType(name = "dateTime")
    protected Calendar timestamp;
    @XmlElement(required = false, type = Long.class, nillable = true)
    protected Long kilobytespersecondDiskRead;
    @XmlElement(required = false, type = Long.class, nillable = true)
    protected Long kilobytespersecondDiskWrite;
    @XmlElement(required = false, nillable = true)
    protected String operationalstatus;

    /**
     * Gets the value of the operational property.
     * 
     */
    public boolean isOperational() {
        return operational;
    }

    /**
     * Sets the value of the operational property.
     * 
     */
    public void setOperational(boolean value) {
        this.operational = value;
    }

    /**
     * Gets the value of the partition property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPartition() {
        return partition;
    }

    /**
     * Sets the value of the partition property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPartition(String value) {
        this.partition = value;
    }

    /**
     * Gets the value of the systemid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSystemid() {
        return systemid;
    }

    /**
     * Sets the value of the systemid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSystemid(String value) {
        this.systemid = value;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Gets the value of the totalspace property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getTotalspace() {
        return totalspace;
    }

    /**
     * Sets the value of the totalspace property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setTotalspace(Long value) {
        this.totalspace = value;
    }

    /**
     * Gets the value of the freespace property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getFreespace() {
        return freespace;
    }

    /**
     * Sets the value of the freespace property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setFreespace(Long value) {
        this.freespace = value;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setType(String value) {
        this.type = value;
    }

    /**
     * Gets the value of the timestamp property.
     * 
     * @return
     *     possible object is
     *     {@link Calendar }
     *     
     */
    public Calendar getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the value of the timestamp property.
     * 
     * @param value
     *     allowed object is
     *     {@link Calendar }
     *     
     */
    public void setTimestamp(Calendar value) {
        this.timestamp = value;
    }

    /**
     * Gets the value of the kilobytespersecondDiskRead property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getKilobytespersecondDiskRead() {
        return kilobytespersecondDiskRead;
    }

    /**
     * Sets the value of the kilobytespersecondDiskRead property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setKilobytespersecondDiskRead(Long value) {
        this.kilobytespersecondDiskRead = value;
    }

    /**
     * Gets the value of the kilobytespersecondDiskWrite property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getKilobytespersecondDiskWrite() {
        return kilobytespersecondDiskWrite;
    }

    /**
     * Sets the value of the kilobytespersecondDiskWrite property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setKilobytespersecondDiskWrite(Long value) {
        this.kilobytespersecondDiskWrite = value;
    }

    /**
     * Gets the value of the operationalstatus property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOperationalstatus() {
        return operationalstatus;
    }

    /**
     * Sets the value of the operationalstatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOperationalstatus(String value) {
        this.operationalstatus = value;
    }

}
