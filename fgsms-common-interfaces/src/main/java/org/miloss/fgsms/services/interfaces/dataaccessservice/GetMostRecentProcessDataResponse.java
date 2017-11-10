
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
 *         &lt;element name="GetMostRecentProcessDataResponse" type="{urn:org:miloss:fgsms:services:interfaces:dataAccessService}GetMostRecentProcessDataResponseMsg"/>
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
    "getMostRecentProcessDataResponse"
})
@XmlRootElement(name = "GetMostRecentProcessDataResponse")
public class GetMostRecentProcessDataResponse
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "GetMostRecentProcessDataResponse", required = true)
    protected GetMostRecentProcessDataResponseMsg getMostRecentProcessDataResponse;

    /**
     * Gets the value of the getMostRecentProcessDataResponse property.
     * 
     * @return
     *     possible object is
     *     {@link GetMostRecentProcessDataResponseMsg }
     *     
     */
    public GetMostRecentProcessDataResponseMsg getGetMostRecentProcessDataResponse() {
        return getMostRecentProcessDataResponse;
    }

    /**
     * Sets the value of the getMostRecentProcessDataResponse property.
     * 
     * @param value
     *     allowed object is
     *     {@link GetMostRecentProcessDataResponseMsg }
     *     
     */
    public void setGetMostRecentProcessDataResponse(GetMostRecentProcessDataResponseMsg value) {
        this.getMostRecentProcessDataResponse = value;
    }

    public boolean isSetGetMostRecentProcessDataResponse() {
        return (this.getMostRecentProcessDataResponse!= null);
    }

}
