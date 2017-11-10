
package org.miloss.fgsms.services.interfaces.dataaccessservice;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import java.util.Calendar;


/**
 * details for a specfic queue or topic
 * 
 * <p>Java class for queueORtopicDetails complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="queueORtopicDetails">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="itemtype" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="messagecount" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="consumercount" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="recievedmessagecount" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="activeconsumercount" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="bytesin" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="bytesout" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="bytesdropped" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="messagesdropped" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="queue_depth" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="agenttype" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="canonicalname" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="timestamp" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "queueORtopicDetails", propOrder = {
    "name",
    "itemtype",
    "messagecount",
    "consumercount",
    "recievedmessagecount",
    "activeconsumercount",
    "bytesin",
    "bytesout",
    "bytesdropped",
    "messagesdropped",
    "queueDepth",
    "agenttype",
    "canonicalname",
    "timestamp"
})
public class QueueORtopicDetails
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(required = true)
    protected String name;
    @XmlElement(required = true, nillable = true)
    protected String itemtype;
    @XmlElement(required = true, type = Long.class, nillable = true)
    protected Long messagecount;
    @XmlElement(required = true, type = Long.class, nillable = true)
    protected Long consumercount;
    @XmlElement(required = true, type = Long.class, nillable = true)
    protected Long recievedmessagecount;
    @XmlElement(required = true, type = Long.class, nillable = true)
    protected Long activeconsumercount;
    @XmlElement(required = true, type = Long.class, nillable = true)
    protected Long bytesin;
    @XmlElement(required = true, type = Long.class, nillable = true)
    protected Long bytesout;
    @XmlElement(required = true, type = Long.class, nillable = true)
    protected Long bytesdropped;
    @XmlElement(required = true, type = Long.class, nillable = true)
    protected Long messagesdropped;
    @XmlElement(name = "queue_depth", required = true, type = Long.class, nillable = true)
    protected Long queueDepth;
    @XmlElement(required = true, nillable = true)
    protected String agenttype;
    @XmlElement(required = true, nillable = true)
    protected String canonicalname;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected Calendar timestamp;

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    public boolean isSetName() {
        return (this.name!= null);
    }

    /**
     * Gets the value of the itemtype property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getItemtype() {
        return itemtype;
    }

    /**
     * Sets the value of the itemtype property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setItemtype(String value) {
        this.itemtype = value;
    }

    public boolean isSetItemtype() {
        return (this.itemtype!= null);
    }

    /**
     * Gets the value of the messagecount property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getMessagecount() {
        return messagecount;
    }

    /**
     * Sets the value of the messagecount property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setMessagecount(Long value) {
        this.messagecount = value;
    }

    public boolean isSetMessagecount() {
        return (this.messagecount!= null);
    }

    /**
     * Gets the value of the consumercount property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getConsumercount() {
        return consumercount;
    }

    /**
     * Sets the value of the consumercount property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setConsumercount(Long value) {
        this.consumercount = value;
    }

    public boolean isSetConsumercount() {
        return (this.consumercount!= null);
    }

    /**
     * Gets the value of the recievedmessagecount property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getRecievedmessagecount() {
        return recievedmessagecount;
    }

    /**
     * Sets the value of the recievedmessagecount property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setRecievedmessagecount(Long value) {
        this.recievedmessagecount = value;
    }

    public boolean isSetRecievedmessagecount() {
        return (this.recievedmessagecount!= null);
    }

    /**
     * Gets the value of the activeconsumercount property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getActiveconsumercount() {
        return activeconsumercount;
    }

    /**
     * Sets the value of the activeconsumercount property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setActiveconsumercount(Long value) {
        this.activeconsumercount = value;
    }

    public boolean isSetActiveconsumercount() {
        return (this.activeconsumercount!= null);
    }

    /**
     * Gets the value of the bytesin property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getBytesin() {
        return bytesin;
    }

    /**
     * Sets the value of the bytesin property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setBytesin(Long value) {
        this.bytesin = value;
    }

    public boolean isSetBytesin() {
        return (this.bytesin!= null);
    }

    /**
     * Gets the value of the bytesout property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getBytesout() {
        return bytesout;
    }

    /**
     * Sets the value of the bytesout property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setBytesout(Long value) {
        this.bytesout = value;
    }

    public boolean isSetBytesout() {
        return (this.bytesout!= null);
    }

    /**
     * Gets the value of the bytesdropped property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getBytesdropped() {
        return bytesdropped;
    }

    /**
     * Sets the value of the bytesdropped property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setBytesdropped(Long value) {
        this.bytesdropped = value;
    }

    public boolean isSetBytesdropped() {
        return (this.bytesdropped!= null);
    }

    /**
     * Gets the value of the messagesdropped property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getMessagesdropped() {
        return messagesdropped;
    }

    /**
     * Sets the value of the messagesdropped property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setMessagesdropped(Long value) {
        this.messagesdropped = value;
    }

    public boolean isSetMessagesdropped() {
        return (this.messagesdropped!= null);
    }

    /**
     * Gets the value of the queueDepth property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getQueueDepth() {
        return queueDepth;
    }

    /**
     * Sets the value of the queueDepth property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setQueueDepth(Long value) {
        this.queueDepth = value;
    }

    public boolean isSetQueueDepth() {
        return (this.queueDepth!= null);
    }

    /**
     * Gets the value of the agenttype property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAgenttype() {
        return agenttype;
    }

    /**
     * Sets the value of the agenttype property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAgenttype(String value) {
        this.agenttype = value;
    }

    public boolean isSetAgenttype() {
        return (this.agenttype!= null);
    }

    /**
     * Gets the value of the canonicalname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCanonicalname() {
        return canonicalname;
    }

    /**
     * Sets the value of the canonicalname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCanonicalname(String value) {
        this.canonicalname = value;
    }

    public boolean isSetCanonicalname() {
        return (this.canonicalname!= null);
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

    public boolean isSetTimestamp() {
        return (this.timestamp!= null);
    }

}
