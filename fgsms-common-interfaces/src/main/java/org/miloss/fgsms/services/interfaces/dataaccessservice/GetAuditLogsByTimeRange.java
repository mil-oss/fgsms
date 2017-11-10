
package org.miloss.fgsms.services.interfaces.dataaccessservice;

import java.io.Serializable;
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
 *         &lt;element name="GetAuditLogsByTimeRangeRequest" type="{urn:org:miloss:fgsms:services:interfaces:dataAccessService}GetAuditLogsByTimeRangeRequestMsg"/>
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
    "getAuditLogsByTimeRangeRequest"
})
@XmlRootElement(name = "GetAuditLogsByTimeRange")
public class GetAuditLogsByTimeRange
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "GetAuditLogsByTimeRangeRequest", required = true)
    protected GetAuditLogsByTimeRangeRequestMsg getAuditLogsByTimeRangeRequest;

    /**
     * Gets the value of the getAuditLogsByTimeRangeRequest property.
     * 
     * @return
     *     possible object is
     *     {@link GetAuditLogsByTimeRangeRequestMsg }
     *     
     */
    public GetAuditLogsByTimeRangeRequestMsg getGetAuditLogsByTimeRangeRequest() {
        return getAuditLogsByTimeRangeRequest;
    }

    /**
     * Sets the value of the getAuditLogsByTimeRangeRequest property.
     * 
     * @param value
     *     allowed object is
     *     {@link GetAuditLogsByTimeRangeRequestMsg }
     *     
     */
    public void setGetAuditLogsByTimeRangeRequest(GetAuditLogsByTimeRangeRequestMsg value) {
        this.getAuditLogsByTimeRangeRequest = value;
    }

    public boolean isSetGetAuditLogsByTimeRangeRequest() {
        return (this.getAuditLogsByTimeRangeRequest!= null);
    }

}
