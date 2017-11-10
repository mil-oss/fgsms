
package org.miloss.fgsms.services.interfaces.dataaccessservice;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="GetSLAFaultRecordsResponseMsgResult" type="{urn:org:miloss:fgsms:services:interfaces:dataAccessService}GetSLAFaultRecordsResponseMsg" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "getSLAFaultRecordsResponseMsgResult"
})
@XmlRootElement(name = "GetSLAFaultRecordsResponse")
public class GetSLAFaultRecordsResponse
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "GetSLAFaultRecordsResponseMsgResult", required = false, nillable = true)
    protected List<GetSLAFaultRecordsResponseMsg> getSLAFaultRecordsResponseMsgResult;

    /**
     * Gets the value of the getSLAFaultRecordsResponseMsgResult property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the getSLAFaultRecordsResponseMsgResult property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getGetSLAFaultRecordsResponseMsgResult().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link GetSLAFaultRecordsResponseMsg }
     * 
     * 
     */
    public List<GetSLAFaultRecordsResponseMsg> getGetSLAFaultRecordsResponseMsgResult() {
        if (getSLAFaultRecordsResponseMsgResult == null) {
            getSLAFaultRecordsResponseMsgResult = new ArrayList<GetSLAFaultRecordsResponseMsg>();
        }
        return this.getSLAFaultRecordsResponseMsgResult;
    }

    public boolean isSetGetSLAFaultRecordsResponseMsgResult() {
        return ((this.getSLAFaultRecordsResponseMsgResult!= null)&&(!this.getSLAFaultRecordsResponseMsgResult.isEmpty()));
    }

    public void unsetGetSLAFaultRecordsResponseMsgResult() {
        this.getSLAFaultRecordsResponseMsgResult = null;
    }

}
