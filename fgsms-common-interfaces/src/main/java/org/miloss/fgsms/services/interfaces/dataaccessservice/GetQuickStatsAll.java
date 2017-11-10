
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
 *         &lt;element name="GetQuickStatsAllRequest" type="{urn:org:miloss:fgsms:services:interfaces:dataAccessService}GetQuickStatsAllRequestMsg"/>
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
    "getQuickStatsAllRequest"
})
@XmlRootElement(name = "GetQuickStatsAll")
public class GetQuickStatsAll
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "GetQuickStatsAllRequest", required = true)
    protected GetQuickStatsAllRequestMsg getQuickStatsAllRequest;

    /**
     * Gets the value of the getQuickStatsAllRequest property.
     * 
     * @return
     *     possible object is
     *     {@link GetQuickStatsAllRequestMsg }
     *     
     */
    public GetQuickStatsAllRequestMsg getGetQuickStatsAllRequest() {
        return getQuickStatsAllRequest;
    }

    /**
     * Sets the value of the getQuickStatsAllRequest property.
     * 
     * @param value
     *     allowed object is
     *     {@link GetQuickStatsAllRequestMsg }
     *     
     */
    public void setGetQuickStatsAllRequest(GetQuickStatsAllRequestMsg value) {
        this.getQuickStatsAllRequest = value;
    }

    public boolean isSetGetQuickStatsAllRequest() {
        return (this.getQuickStatsAllRequest!= null);
    }

}
