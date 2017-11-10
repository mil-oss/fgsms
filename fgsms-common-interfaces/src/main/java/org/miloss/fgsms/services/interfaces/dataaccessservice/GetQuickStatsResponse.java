
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
 *         &lt;element name="GetQuickStatsResponse" type="{urn:org:miloss:fgsms:services:interfaces:dataAccessService}GetQuickStatsResponseMsg"/>
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
    "getQuickStatsResponse"
})
@XmlRootElement(name = "GetQuickStatsResponse")
public class GetQuickStatsResponse
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "GetQuickStatsResponse", required = true)
    protected GetQuickStatsResponseMsg getQuickStatsResponse;

    /**
     * Gets the value of the getQuickStatsResponse property.
     * 
     * @return
     *     possible object is
     *     {@link GetQuickStatsResponseMsg }
     *     
     */
    public GetQuickStatsResponseMsg getGetQuickStatsResponse() {
        return getQuickStatsResponse;
    }

    /**
     * Sets the value of the getQuickStatsResponse property.
     * 
     * @param value
     *     allowed object is
     *     {@link GetQuickStatsResponseMsg }
     *     
     */
    public void setGetQuickStatsResponse(GetQuickStatsResponseMsg value) {
        this.getQuickStatsResponse = value;
    }

    public boolean isSetGetQuickStatsResponse() {
        return (this.getQuickStatsResponse!= null);
    }

}
