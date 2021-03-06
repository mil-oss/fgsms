
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
 *         &lt;element name="GetAuditLog" type="{urn:org:miloss:fgsms:services:interfaces:dataAccessService}GetAuditLogRequestMsg"/>
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
    "getAuditLog"
})
@XmlRootElement(name = "GetAuditLog")
public class GetAuditLog
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "GetAuditLog", required = true)
    protected GetAuditLogRequestMsg getAuditLog;

    /**
     * Gets the value of the getAuditLog property.
     * 
     * @return
     *     possible object is
     *     {@link GetAuditLogRequestMsg }
     *     
     */
    public GetAuditLogRequestMsg getGetAuditLog() {
        return getAuditLog;
    }

    /**
     * Sets the value of the getAuditLog property.
     * 
     * @param value
     *     allowed object is
     *     {@link GetAuditLogRequestMsg }
     *     
     */
    public void setGetAuditLog(GetAuditLogRequestMsg value) {
        this.getAuditLog = value;
    }

    public boolean isSetGetAuditLog() {
        return (this.getAuditLog!= null);
    }

}
