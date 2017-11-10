
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
 *         &lt;element name="GetQuickStatsAllResponse" type="{urn:org:miloss:fgsms:services:interfaces:dataAccessService}GetQuickStatsAllResponseMsg"/>
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
    "getQuickStatsAllResponse"
})
@XmlRootElement(name = "GetQuickStatsAllResponse")
public class GetQuickStatsAllResponse
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "GetQuickStatsAllResponse", required = true)
    protected GetQuickStatsAllResponseMsg getQuickStatsAllResponse;

    /**
     * Gets the value of the getQuickStatsAllResponse property.
     * 
     * @return
     *     possible object is
     *     {@link GetQuickStatsAllResponseMsg }
     *     
     */
    public GetQuickStatsAllResponseMsg getGetQuickStatsAllResponse() {
        return getQuickStatsAllResponse;
    }

    /**
     * Sets the value of the getQuickStatsAllResponse property.
     * 
     * @param value
     *     allowed object is
     *     {@link GetQuickStatsAllResponseMsg }
     *     
     */
    public void setGetQuickStatsAllResponse(GetQuickStatsAllResponseMsg value) {
        this.getQuickStatsAllResponse = value;
    }

    public boolean isSetGetQuickStatsAllResponse() {
        return (this.getQuickStatsAllResponse!= null);
    }

}
