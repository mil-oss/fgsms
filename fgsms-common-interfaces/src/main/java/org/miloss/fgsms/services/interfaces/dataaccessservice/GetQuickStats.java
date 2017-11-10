
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
 *         &lt;element name="GetQuickStatsRequest" type="{urn:org:miloss:fgsms:services:interfaces:dataAccessService}GetQuickStatsRequestMsg"/>
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
    "getQuickStatsRequest"
})
@XmlRootElement(name = "GetQuickStats")
public class GetQuickStats
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "GetQuickStatsRequest", required = true)
    protected GetQuickStatsRequestMsg getQuickStatsRequest;

    /**
     * Gets the value of the getQuickStatsRequest property.
     * 
     * @return
     *     possible object is
     *     {@link GetQuickStatsRequestMsg }
     *     
     */
    public GetQuickStatsRequestMsg getGetQuickStatsRequest() {
        return getQuickStatsRequest;
    }

    /**
     * Sets the value of the getQuickStatsRequest property.
     * 
     * @param value
     *     allowed object is
     *     {@link GetQuickStatsRequestMsg }
     *     
     */
    public void setGetQuickStatsRequest(GetQuickStatsRequestMsg value) {
        this.getQuickStatsRequest = value;
    }

    public boolean isSetGetQuickStatsRequest() {
        return (this.getQuickStatsRequest!= null);
    }

}
