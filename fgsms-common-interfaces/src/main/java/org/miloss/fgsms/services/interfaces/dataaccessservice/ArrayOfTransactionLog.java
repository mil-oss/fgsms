
package org.miloss.fgsms.services.interfaces.dataaccessservice;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * request message
 * 
 * <p>Java class for ArrayOfTransactionLog complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfTransactionLog">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="TransactionLog" type="{urn:org:miloss:fgsms:services:interfaces:dataAccessService}TransactionLog" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfTransactionLog", propOrder = {
    "transactionLog"
})
public class ArrayOfTransactionLog
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "TransactionLog", required = false, nillable = true)
    protected List<TransactionLog> transactionLog;

    /**
     * Gets the value of the transactionLog property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the transactionLog property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTransactionLog().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TransactionLog }
     * 
     * 
     */
    public List<TransactionLog> getTransactionLog() {
        if (transactionLog == null) {
            transactionLog = new ArrayList<TransactionLog>();
        }
        return this.transactionLog;
    }

    public boolean isSetTransactionLog() {
        return ((this.transactionLog!= null)&&(!this.transactionLog.isEmpty()));
    }

    public void unsetTransactionLog() {
        this.transactionLog = null;
    }

}
