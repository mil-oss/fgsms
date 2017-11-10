
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
 *         &lt;element name="GetMessageLogsResult" type="{urn:org:miloss:fgsms:services:interfaces:dataAccessService}GetMessageLogsResponseMsg"/>
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
    "getMessageLogsResult"
})
@XmlRootElement(name = "GetMessageLogsByRangeResponse")
public class GetMessageLogsByRangeResponse
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "GetMessageLogsResult", required = true, nillable = true)
    protected GetMessageLogsResponseMsg getMessageLogsResult;

    /**
     * Gets the value of the getMessageLogsResult property.
     * 
     * @return
     *     possible object is
     *     {@link GetMessageLogsResponseMsg }
     *     
     */
    public GetMessageLogsResponseMsg getGetMessageLogsResult() {
        return getMessageLogsResult;
    }

    /**
     * Sets the value of the getMessageLogsResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link GetMessageLogsResponseMsg }
     *     
     */
    public void setGetMessageLogsResult(GetMessageLogsResponseMsg value) {
        this.getMessageLogsResult = value;
    }

    public boolean isSetGetMessageLogsResult() {
        return (this.getMessageLogsResult!= null);
    }

}
