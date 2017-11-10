
package org.miloss.fgsms.services.interfaces.datacollector;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * broker data
 * 
 * <p>Java class for BrokerData complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="BrokerData">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="QueueOrTopicName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="QueueOrTopicCanonicalName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="MessagesIn" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="MessagesOut" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="MessagesDropped" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="ActiveConsumers" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="TotalConsumers" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="BytesIn" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="BytesOut" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="BytesDropped" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="Depth" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="ItemType" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BrokerData", propOrder = {
    "queueOrTopicName",
    "queueOrTopicCanonicalName",
    "messagesIn",
    "messagesOut",
    "messagesDropped",
    "activeConsumers",
    "totalConsumers",
    "bytesIn",
    "bytesOut",
    "bytesDropped",
    "depth",
    "itemType"
})
public class BrokerData
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "QueueOrTopicName", required = true)
    protected String queueOrTopicName;
    @XmlElement(name = "QueueOrTopicCanonicalName", required = true)
    protected String queueOrTopicCanonicalName;
    @XmlElement(name = "MessagesIn", defaultValue = "0")
    protected long messagesIn;
    @XmlElement(name = "MessagesOut", defaultValue = "0")
    protected long messagesOut;
    @XmlElement(name = "MessagesDropped", defaultValue = "0")
    protected long messagesDropped;
    @XmlElement(name = "ActiveConsumers", defaultValue = "0")
    protected long activeConsumers;
    @XmlElement(name = "TotalConsumers", defaultValue = "0")
    protected long totalConsumers;
    @XmlElement(name = "BytesIn", defaultValue = "0")
    protected long bytesIn;
    @XmlElement(name = "BytesOut", defaultValue = "0")
    protected long bytesOut;
    @XmlElement(name = "BytesDropped", defaultValue = "0")
    protected long bytesDropped;
    @XmlElement(name = "Depth", defaultValue = "0")
    protected long depth;
    @XmlElement(name = "ItemType", required = true)
    protected String itemType;

    /**
     * Gets the value of the queueOrTopicName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getQueueOrTopicName() {
        return queueOrTopicName;
    }

    /**
     * Sets the value of the queueOrTopicName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setQueueOrTopicName(String value) {
        this.queueOrTopicName = value;
    }

    public boolean isSetQueueOrTopicName() {
        return (this.queueOrTopicName!= null);
    }

    /**
     * Gets the value of the queueOrTopicCanonicalName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getQueueOrTopicCanonicalName() {
        return queueOrTopicCanonicalName;
    }

    /**
     * Sets the value of the queueOrTopicCanonicalName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setQueueOrTopicCanonicalName(String value) {
        this.queueOrTopicCanonicalName = value;
    }

    public boolean isSetQueueOrTopicCanonicalName() {
        return (this.queueOrTopicCanonicalName!= null);
    }

    /**
     * Gets the value of the messagesIn property.
     * 
     */
    public long getMessagesIn() {
        return messagesIn;
    }

    /**
     * Sets the value of the messagesIn property.
     * 
     */
    public void setMessagesIn(long value) {
        this.messagesIn = value;
    }

    public boolean isSetMessagesIn() {
        return true;
    }

    /**
     * Gets the value of the messagesOut property.
     * 
     */
    public long getMessagesOut() {
        return messagesOut;
    }

    /**
     * Sets the value of the messagesOut property.
     * 
     */
    public void setMessagesOut(long value) {
        this.messagesOut = value;
    }

    public boolean isSetMessagesOut() {
        return true;
    }

    /**
     * Gets the value of the messagesDropped property.
     * 
     */
    public long getMessagesDropped() {
        return messagesDropped;
    }

    /**
     * Sets the value of the messagesDropped property.
     * 
     */
    public void setMessagesDropped(long value) {
        this.messagesDropped = value;
    }

    public boolean isSetMessagesDropped() {
        return true;
    }

    /**
     * Gets the value of the activeConsumers property.
     * 
     */
    public long getActiveConsumers() {
        return activeConsumers;
    }

    /**
     * Sets the value of the activeConsumers property.
     * 
     */
    public void setActiveConsumers(long value) {
        this.activeConsumers = value;
    }

    public boolean isSetActiveConsumers() {
        return true;
    }

    /**
     * Gets the value of the totalConsumers property.
     * 
     */
    public long getTotalConsumers() {
        return totalConsumers;
    }

    /**
     * Sets the value of the totalConsumers property.
     * 
     */
    public void setTotalConsumers(long value) {
        this.totalConsumers = value;
    }

    public boolean isSetTotalConsumers() {
        return true;
    }

    /**
     * Gets the value of the bytesIn property.
     * 
     */
    public long getBytesIn() {
        return bytesIn;
    }

    /**
     * Sets the value of the bytesIn property.
     * 
     */
    public void setBytesIn(long value) {
        this.bytesIn = value;
    }

    public boolean isSetBytesIn() {
        return true;
    }

    /**
     * Gets the value of the bytesOut property.
     * 
     */
    public long getBytesOut() {
        return bytesOut;
    }

    /**
     * Sets the value of the bytesOut property.
     * 
     */
    public void setBytesOut(long value) {
        this.bytesOut = value;
    }

    public boolean isSetBytesOut() {
        return true;
    }

    /**
     * Gets the value of the bytesDropped property.
     * 
     */
    public long getBytesDropped() {
        return bytesDropped;
    }

    /**
     * Sets the value of the bytesDropped property.
     * 
     */
    public void setBytesDropped(long value) {
        this.bytesDropped = value;
    }

    public boolean isSetBytesDropped() {
        return true;
    }

    /**
     * Gets the value of the depth property.
     * 
     */
    public long getDepth() {
        return depth;
    }

    /**
     * Sets the value of the depth property.
     * 
     */
    public void setDepth(long value) {
        this.depth = value;
    }

    public boolean isSetDepth() {
        return true;
    }

    /**
     * Gets the value of the itemType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getItemType() {
        return itemType;
    }

    /**
     * Sets the value of the itemType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setItemType(String value) {
        this.itemType = value;
    }

    public boolean isSetItemType() {
        return (this.itemType!= null);
    }

}
