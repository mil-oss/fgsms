
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
 * <p>Java class for GetThreadTransactionsResponseMsg complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetThreadTransactionsResponseMsg">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="classification" type="{urn:org:miloss:fgsms:services:interfaces:common}SecurityWrapper"/>
 *         &lt;element name="threads" type="{urn:org:miloss:fgsms:services:interfaces:dataAccessService}ThreadTime" maxOccurs="unbounded" minOccurs="0" />
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetThreadTransactionsResponseMsg", propOrder = {
    "classification",
    "threads"
})
public class GetThreadTransactionsResponseMsg
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(required = true, nillable = true)
    protected SecurityWrapper classification;
    @XmlElement(required = false)
    protected List<ThreadTime> threads;

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
     * Gets the value of the threads property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the threads property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getThreads().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ThreadTime }
     * 
     * 
     */
    public List<ThreadTime> getThreads() {
        if (threads == null) {
            threads = new ArrayList<ThreadTime>();
        }
        return this.threads;
    }

    public boolean isSetThreads() {
        return ((this.threads!= null)&&(!this.threads.isEmpty()));
    }

    public void unsetThreads() {
        this.threads = null;
    }

}
