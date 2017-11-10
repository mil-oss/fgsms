
package org.miloss.fgsms.services.interfaces.datacollector;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.miloss.fgsms.services.interfaces.common.SecurityWrapper;


/**
 * request message
 * 
 * <p>Java class for AddStatisticalDataRequestMsg complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AddStatisticalDataRequestMsg">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="data" type="{urn:org:miloss:fgsms:services:interfaces:dataCollector}BrokerData" maxOccurs="unbounded"/>
 *         &lt;element name="BrokerURI" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="BrokerHostname" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Domain" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="OperationalStatus" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="OperationalStatusMessage" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="AgentType" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
@XmlType(name = "AddStatisticalDataRequestMsg", propOrder = {
    "data",
    "brokerURI",
    "brokerHostname",
    "domain",
    "operationalStatus",
    "operationalStatusMessage",
    "agentType",
    "classification"
})
public class AddStatisticalDataRequestMsg
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(required = true)
    protected List<BrokerData> data;
    @XmlElement(name = "BrokerURI", required = true)
    protected String brokerURI;
    @XmlElement(name = "BrokerHostname", required = true)
    protected String brokerHostname;
    @XmlElement(name = "Domain", required = true)
    protected String domain;
    @XmlElement(name = "OperationalStatus")
    protected boolean operationalStatus;
    @XmlElement(name = "OperationalStatusMessage", required = true)
    protected String operationalStatusMessage;
    @XmlElement(name = "AgentType", required = true)
    protected String agentType;
    @XmlElement(required = true, nillable = true)
    protected SecurityWrapper classification;

    /**
     * Gets the value of the data property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the data property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getData().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link BrokerData }
     * 
     * 
     */
    public List<BrokerData> getData() {
        if (data == null) {
            data = new ArrayList<BrokerData>();
        }
        return this.data;
    }

    public boolean isSetData() {
        return ((this.data!= null)&&(!this.data.isEmpty()));
    }

    public void unsetData() {
        this.data = null;
    }

    /**
     * Gets the value of the brokerURI property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBrokerURI() {
        return brokerURI;
    }

    /**
     * Sets the value of the brokerURI property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBrokerURI(String value) {
        this.brokerURI = value;
    }

    public boolean isSetBrokerURI() {
        return (this.brokerURI!= null);
    }

    /**
     * Gets the value of the brokerHostname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBrokerHostname() {
        return brokerHostname;
    }

    /**
     * Sets the value of the brokerHostname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBrokerHostname(String value) {
        this.brokerHostname = value;
    }

    public boolean isSetBrokerHostname() {
        return (this.brokerHostname!= null);
    }

    /**
     * Gets the value of the domain property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDomain() {
        return domain;
    }

    /**
     * Sets the value of the domain property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDomain(String value) {
        this.domain = value;
    }

    public boolean isSetDomain() {
        return (this.domain!= null);
    }

    /**
     * Gets the value of the operationalStatus property.
     * 
     */
    public boolean isOperationalStatus() {
        return operationalStatus;
    }

    /**
     * Sets the value of the operationalStatus property.
     * 
     */
    public void setOperationalStatus(boolean value) {
        this.operationalStatus = value;
    }

    public boolean isSetOperationalStatus() {
        return true;
    }

    /**
     * Gets the value of the operationalStatusMessage property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOperationalStatusMessage() {
        return operationalStatusMessage;
    }

    /**
     * Sets the value of the operationalStatusMessage property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOperationalStatusMessage(String value) {
        this.operationalStatusMessage = value;
    }

    public boolean isSetOperationalStatusMessage() {
        return (this.operationalStatusMessage!= null);
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
