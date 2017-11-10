
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
 *         &lt;element name="GetDataCollectorListResult" type="{urn:org:miloss:fgsms:services:interfaces:dataAccessService}GetDataCollectorListResponseMsg"/>
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
    "getDataCollectorListResult"
})
@XmlRootElement(name = "GetDataCollectorListResponse")
public class GetDataCollectorListResponse
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "GetDataCollectorListResult", required = true, nillable = true)
    protected GetDataCollectorListResponseMsg getDataCollectorListResult;

    /**
     * Gets the value of the getDataCollectorListResult property.
     * 
     * @return
     *     possible object is
     *     {@link GetDataCollectorListResponseMsg }
     *     
     */
    public GetDataCollectorListResponseMsg getGetDataCollectorListResult() {
        return getDataCollectorListResult;
    }

    /**
     * Sets the value of the getDataCollectorListResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link GetDataCollectorListResponseMsg }
     *     
     */
    public void setGetDataCollectorListResult(GetDataCollectorListResponseMsg value) {
        this.getDataCollectorListResult = value;
    }

    public boolean isSetGetDataCollectorListResult() {
        return (this.getDataCollectorListResult!= null);
    }

}
