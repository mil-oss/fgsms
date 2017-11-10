
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
 *         &lt;element name="GetAuditLogResponse" type="{urn:org:miloss:fgsms:services:interfaces:dataAccessService}GetAuditLogResponseMsg"/>
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
    "getAuditLogResponse"
})
@XmlRootElement(name = "GetAuditLogResponse")
public class GetAuditLogResponse
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "GetAuditLogResponse", required = true)
    protected GetAuditLogResponseMsg getAuditLogResponse;

    /**
     * Gets the value of the getAuditLogResponse property.
     * 
     * @return
     *     possible object is
     *     {@link GetAuditLogResponseMsg }
     *     
     */
    public GetAuditLogResponseMsg getGetAuditLogResponse() {
        return getAuditLogResponse;
    }

    /**
     * Sets the value of the getAuditLogResponse property.
     * 
     * @param value
     *     allowed object is
     *     {@link GetAuditLogResponseMsg }
     *     
     */
    public void setGetAuditLogResponse(GetAuditLogResponseMsg value) {
        this.getAuditLogResponse = value;
    }

    public boolean isSetGetAuditLogResponse() {
        return (this.getAuditLogResponse!= null);
    }

}
