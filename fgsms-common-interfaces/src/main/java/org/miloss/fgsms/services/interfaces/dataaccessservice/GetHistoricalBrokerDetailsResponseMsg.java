
package org.miloss.fgsms.services.interfaces.dataaccessservice;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.miloss.fgsms.services.interfaces.common.SecurityWrapper;


/**
 * response message
 * 
 * <p>Java class for GetHistoricalBrokerDetailsResponseMsg complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetHistoricalBrokerDetailsResponseMsg">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="classification" type="{urn:org:miloss:fgsms:services:interfaces:common}SecurityWrapper"/>
 *         &lt;element name="uri" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="averagemessagesent" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="averagemessagesrecieved" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="averagemessagesdropped" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="averagequeuedepth" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="averageconsumers" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="averageactiveconsumers" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="averagebytesin" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="averagebytesout" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="averagebytesdropped" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetHistoricalBrokerDetailsResponseMsg", propOrder = {
    "classification",
    "uri",
    "averagemessagesent",
    "averagemessagesrecieved",
    "averagemessagesdropped",
    "averagequeuedepth",
    "averageconsumers",
    "averageactiveconsumers",
    "averagebytesin",
    "averagebytesout",
    "averagebytesdropped"
})
public class GetHistoricalBrokerDetailsResponseMsg
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(required = true, nillable = true)
    protected SecurityWrapper classification;
    @XmlElement(required = true)
    protected String uri;
    protected double averagemessagesent;
    protected double averagemessagesrecieved;
    protected double averagemessagesdropped;
    protected double averagequeuedepth;
    protected double averageconsumers;
    protected double averageactiveconsumers;
    protected double averagebytesin;
    protected double averagebytesout;
    protected double averagebytesdropped;

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
     * Gets the value of the averagemessagesent property.
     * 
     */
    public double getAveragemessagesent() {
        return averagemessagesent;
    }

    /**
     * Sets the value of the averagemessagesent property.
     * 
     */
    public void setAveragemessagesent(double value) {
        this.averagemessagesent = value;
    }

    public boolean isSetAveragemessagesent() {
        return true;
    }

    /**
     * Gets the value of the averagemessagesrecieved property.
     * 
     */
    public double getAveragemessagesrecieved() {
        return averagemessagesrecieved;
    }

    /**
     * Sets the value of the averagemessagesrecieved property.
     * 
     */
    public void setAveragemessagesrecieved(double value) {
        this.averagemessagesrecieved = value;
    }

    public boolean isSetAveragemessagesrecieved() {
        return true;
    }

    /**
     * Gets the value of the averagemessagesdropped property.
     * 
     */
    public double getAveragemessagesdropped() {
        return averagemessagesdropped;
    }

    /**
     * Sets the value of the averagemessagesdropped property.
     * 
     */
    public void setAveragemessagesdropped(double value) {
        this.averagemessagesdropped = value;
    }

    public boolean isSetAveragemessagesdropped() {
        return true;
    }

    /**
     * Gets the value of the averagequeuedepth property.
     * 
     */
    public double getAveragequeuedepth() {
        return averagequeuedepth;
    }

    /**
     * Sets the value of the averagequeuedepth property.
     * 
     */
    public void setAveragequeuedepth(double value) {
        this.averagequeuedepth = value;
    }

    public boolean isSetAveragequeuedepth() {
        return true;
    }

    /**
     * Gets the value of the averageconsumers property.
     * 
     */
    public double getAverageconsumers() {
        return averageconsumers;
    }

    /**
     * Sets the value of the averageconsumers property.
     * 
     */
    public void setAverageconsumers(double value) {
        this.averageconsumers = value;
    }

    public boolean isSetAverageconsumers() {
        return true;
    }

    /**
     * Gets the value of the averageactiveconsumers property.
     * 
     */
    public double getAverageactiveconsumers() {
        return averageactiveconsumers;
    }

    /**
     * Sets the value of the averageactiveconsumers property.
     * 
     */
    public void setAverageactiveconsumers(double value) {
        this.averageactiveconsumers = value;
    }

    public boolean isSetAverageactiveconsumers() {
        return true;
    }

    /**
     * Gets the value of the averagebytesin property.
     * 
     */
    public double getAveragebytesin() {
        return averagebytesin;
    }

    /**
     * Sets the value of the averagebytesin property.
     * 
     */
    public void setAveragebytesin(double value) {
        this.averagebytesin = value;
    }

    public boolean isSetAveragebytesin() {
        return true;
    }

    /**
     * Gets the value of the averagebytesout property.
     * 
     */
    public double getAveragebytesout() {
        return averagebytesout;
    }

    /**
     * Sets the value of the averagebytesout property.
     * 
     */
    public void setAveragebytesout(double value) {
        this.averagebytesout = value;
    }

    public boolean isSetAveragebytesout() {
        return true;
    }

    /**
     * Gets the value of the averagebytesdropped property.
     * 
     */
    public double getAveragebytesdropped() {
        return averagebytesdropped;
    }

    /**
     * Sets the value of the averagebytesdropped property.
     * 
     */
    public void setAveragebytesdropped(double value) {
        this.averagebytesdropped = value;
    }

    public boolean isSetAveragebytesdropped() {
        return true;
    }

}
