
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
 *         &lt;element name="GetAuditLogsByTimeRangeResponse" type="{urn:org:miloss:fgsms:services:interfaces:dataAccessService}GetAuditLogsByTimeRangeResponseMsg"/>
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
    "getAuditLogsByTimeRangeResponse"
})
@XmlRootElement(name = "GetAuditLogsByTimeRangeResponse")
public class GetAuditLogsByTimeRangeResponse
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "GetAuditLogsByTimeRangeResponse", required = true)
    protected GetAuditLogsByTimeRangeResponseMsg getAuditLogsByTimeRangeResponse;

    /**
     * Gets the value of the getAuditLogsByTimeRangeResponse property.
     * 
     * @return
     *     possible object is
     *     {@link GetAuditLogsByTimeRangeResponseMsg }
     *     
     */
    public GetAuditLogsByTimeRangeResponseMsg getGetAuditLogsByTimeRangeResponse() {
        return getAuditLogsByTimeRangeResponse;
    }

    /**
     * Sets the value of the getAuditLogsByTimeRangeResponse property.
     * 
     * @param value
     *     allowed object is
     *     {@link GetAuditLogsByTimeRangeResponseMsg }
     *     
     */
    public void setGetAuditLogsByTimeRangeResponse(GetAuditLogsByTimeRangeResponseMsg value) {
        this.getAuditLogsByTimeRangeResponse = value;
    }

    public boolean isSetGetAuditLogsByTimeRangeResponse() {
        return (this.getAuditLogsByTimeRangeResponse!= null);
    }

}
