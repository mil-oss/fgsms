
package org.miloss.fgsms.services.interfaces.dataaccessservice;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import java.util.Calendar;


/**
 * details for a specific broker
 * 
 * <p>Java class for brokerDetails complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="brokerDetails">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="uri" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="displayName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="operational" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="numberOfQueuesTopics" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="totalmessagesent" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="totalmessagesrecieved" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="totalmessagesdropped" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="totalqueuedepth" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="totalconsumers" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="totalactiveconsumers" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="totalbytesin" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="totalbytesout" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="totalbytesdropped" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="lastCheckIn" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "brokerDetails", propOrder = {
    "uri",
    "displayName",
    "operational",
    "numberOfQueuesTopics",
    "totalmessagesent",
    "totalmessagesrecieved",
    "totalmessagesdropped",
    "totalqueuedepth",
    "totalconsumers",
    "totalactiveconsumers",
    "totalbytesin",
    "totalbytesout",
    "totalbytesdropped",
    "lastCheckIn"
})
public class BrokerDetails
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(required = true)
    protected String uri;
    @XmlElement(required = true)
    protected String displayName;
    protected boolean operational;
    protected long numberOfQueuesTopics;
    protected long totalmessagesent;
    protected long totalmessagesrecieved;
    protected long totalmessagesdropped;
    protected long totalqueuedepth;
    protected long totalconsumers;
    protected long totalactiveconsumers;
    protected long totalbytesin;
    protected long totalbytesout;
    protected long totalbytesdropped;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected Calendar lastCheckIn;

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
     * Gets the value of the displayName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Sets the value of the displayName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDisplayName(String value) {
        this.displayName = value;
    }

    public boolean isSetDisplayName() {
        return (this.displayName!= null);
    }

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

    public boolean isSetOperational() {
        return true;
    }

    /**
     * Gets the value of the numberOfQueuesTopics property.
     * 
     */
    public long getNumberOfQueuesTopics() {
        return numberOfQueuesTopics;
    }

    /**
     * Sets the value of the numberOfQueuesTopics property.
     * 
     */
    public void setNumberOfQueuesTopics(long value) {
        this.numberOfQueuesTopics = value;
    }

    public boolean isSetNumberOfQueuesTopics() {
        return true;
    }

    /**
     * Gets the value of the totalmessagesent property.
     * 
     */
    public long getTotalmessagesent() {
        return totalmessagesent;
    }

    /**
     * Sets the value of the totalmessagesent property.
     * 
     */
    public void setTotalmessagesent(long value) {
        this.totalmessagesent = value;
    }

    public boolean isSetTotalmessagesent() {
        return true;
    }

    /**
     * Gets the value of the totalmessagesrecieved property.
     * 
     */
    public long getTotalmessagesrecieved() {
        return totalmessagesrecieved;
    }

    /**
     * Sets the value of the totalmessagesrecieved property.
     * 
     */
    public void setTotalmessagesrecieved(long value) {
        this.totalmessagesrecieved = value;
    }

    public boolean isSetTotalmessagesrecieved() {
        return true;
    }

    /**
     * Gets the value of the totalmessagesdropped property.
     * 
     */
    public long getTotalmessagesdropped() {
        return totalmessagesdropped;
    }

    /**
     * Sets the value of the totalmessagesdropped property.
     * 
     */
    public void setTotalmessagesdropped(long value) {
        this.totalmessagesdropped = value;
    }

    public boolean isSetTotalmessagesdropped() {
        return true;
    }

    /**
     * Gets the value of the totalqueuedepth property.
     * 
     */
    public long getTotalqueuedepth() {
        return totalqueuedepth;
    }

    /**
     * Sets the value of the totalqueuedepth property.
     * 
     */
    public void setTotalqueuedepth(long value) {
        this.totalqueuedepth = value;
    }

    public boolean isSetTotalqueuedepth() {
        return true;
    }

    /**
     * Gets the value of the totalconsumers property.
     * 
     */
    public long getTotalconsumers() {
        return totalconsumers;
    }

    /**
     * Sets the value of the totalconsumers property.
     * 
     */
    public void setTotalconsumers(long value) {
        this.totalconsumers = value;
    }

    public boolean isSetTotalconsumers() {
        return true;
    }

    /**
     * Gets the value of the totalactiveconsumers property.
     * 
     */
    public long getTotalactiveconsumers() {
        return totalactiveconsumers;
    }

    /**
     * Sets the value of the totalactiveconsumers property.
     * 
     */
    public void setTotalactiveconsumers(long value) {
        this.totalactiveconsumers = value;
    }

    public boolean isSetTotalactiveconsumers() {
        return true;
    }

    /**
     * Gets the value of the totalbytesin property.
     * 
     */
    public long getTotalbytesin() {
        return totalbytesin;
    }

    /**
     * Sets the value of the totalbytesin property.
     * 
     */
    public void setTotalbytesin(long value) {
        this.totalbytesin = value;
    }

    public boolean isSetTotalbytesin() {
        return true;
    }

    /**
     * Gets the value of the totalbytesout property.
     * 
     */
    public long getTotalbytesout() {
        return totalbytesout;
    }

    /**
     * Sets the value of the totalbytesout property.
     * 
     */
    public void setTotalbytesout(long value) {
        this.totalbytesout = value;
    }

    public boolean isSetTotalbytesout() {
        return true;
    }

    /**
     * Gets the value of the totalbytesdropped property.
     * 
     */
    public long getTotalbytesdropped() {
        return totalbytesdropped;
    }

    /**
     * Sets the value of the totalbytesdropped property.
     * 
     */
    public void setTotalbytesdropped(long value) {
        this.totalbytesdropped = value;
    }

    public boolean isSetTotalbytesdropped() {
        return true;
    }

    /**
     * Gets the value of the lastCheckIn property.
     * 
     * @return
     *     possible object is
     *     {@link Calendar }
     *     
     */
    public Calendar getLastCheckIn() {
        return lastCheckIn;
    }

    /**
     * Sets the value of the lastCheckIn property.
     * 
     * @param value
     *     allowed object is
     *     {@link Calendar }
     *     
     */
    public void setLastCheckIn(Calendar value) {
        this.lastCheckIn = value;
    }

    public boolean isSetLastCheckIn() {
        return (this.lastCheckIn!= null);
    }

}
