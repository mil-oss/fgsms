
package org.miloss.fgsms.services.interfaces.dataaccessservice;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.miloss.fgsms.services.interfaces.common.SecurityWrapper;


/**
 * response message
 * 
 * <p>Java class for GetHistoricalTopicQueueDetailsResponseMsg complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetHistoricalTopicQueueDetailsResponseMsg">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="HistoricalTopicQueueDetails" type="{urn:org:miloss:fgsms:services:interfaces:dataAccessService}queueORtopicDetails" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="classification" type="{urn:org:miloss:fgsms:services:interfaces:common}SecurityWrapper"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetHistoricalTopicQueueDetailsResponseMsg", propOrder = {
    "historicalTopicQueueDetails",
    "classification"
})
public class GetHistoricalTopicQueueDetailsResponseMsg
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "HistoricalTopicQueueDetails")
    protected List<QueueORtopicDetails> historicalTopicQueueDetails;
    @XmlElement(required = true, nillable = true)
    protected SecurityWrapper classification;

    /**
     * Gets the value of the historicalTopicQueueDetails property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the historicalTopicQueueDetails property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getHistoricalTopicQueueDetails().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link QueueORtopicDetails }
     * 
     * 
     */
    public List<QueueORtopicDetails> getHistoricalTopicQueueDetails() {
        if (historicalTopicQueueDetails == null) {
            historicalTopicQueueDetails = new ArrayList<QueueORtopicDetails>();
        }
        return this.historicalTopicQueueDetails;
    }

    public boolean isSetHistoricalTopicQueueDetails() {
        return ((this.historicalTopicQueueDetails!= null)&&(!this.historicalTopicQueueDetails.isEmpty()));
    }

    public void unsetHistoricalTopicQueueDetails() {
        this.historicalTopicQueueDetails = null;
    }

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

}
