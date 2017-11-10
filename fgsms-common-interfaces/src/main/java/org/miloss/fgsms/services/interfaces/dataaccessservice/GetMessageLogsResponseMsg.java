
package org.miloss.fgsms.services.interfaces.dataaccessservice;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.miloss.fgsms.services.interfaces.common.SecurityWrapper;


/**
 * 
 *             GetMessageLogsResponse
 *             
 * 
 * <p>Java class for GetMessageLogsResponseMsg complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetMessageLogsResponseMsg">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="classification" type="{urn:org:miloss:fgsms:services:interfaces:common}SecurityWrapper"/>
 *         &lt;element name="TotalRecords" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="logs" type="{urn:org:miloss:fgsms:services:interfaces:dataAccessService}ArrayOfTransactionLog"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetMessageLogsResponseMsg", propOrder = {
    "classification",
    "totalRecords",
    "logs"
})
public class GetMessageLogsResponseMsg
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(required = true, nillable = true)
    protected SecurityWrapper classification;
    @XmlElement(name = "TotalRecords")
    protected int totalRecords;
    @XmlElement(required = false, nillable = true)
    protected ArrayOfTransactionLog logs;

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
     * Gets the value of the totalRecords property.
     * 
     */
    public int getTotalRecords() {
        return totalRecords;
    }

    /**
     * Sets the value of the totalRecords property.
     * 
     */
    public void setTotalRecords(int value) {
        this.totalRecords = value;
    }

    public boolean isSetTotalRecords() {
        return true;
    }

    /**
     * Gets the value of the logs property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfTransactionLog }
     *     
     */
    public ArrayOfTransactionLog getLogs() {
        return logs;
    }

    /**
     * Sets the value of the logs property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfTransactionLog }
     *     
     */
    public void setLogs(ArrayOfTransactionLog value) {
        this.logs = value;
    }

    public boolean isSetLogs() {
        return (this.logs!= null);
    }

}
