
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
 *         &lt;element name="GetOperationalStatusLogResult" type="{urn:org:miloss:fgsms:services:interfaces:dataAccessService}GetOperationalStatusLogResponseMsg"/>
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
    "getOperationalStatusLogResult"
})
@XmlRootElement(name = "GetOperationalStatusLogResponse")
public class GetOperationalStatusLogResponse
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "GetOperationalStatusLogResult", required = false, nillable = true)
    protected GetOperationalStatusLogResponseMsg getOperationalStatusLogResult;

    /**
     * Gets the value of the getOperationalStatusLogResult property.
     * 
     * @return
     *     possible object is
     *     {@link GetOperationalStatusLogResponseMsg }
     *     
     */
    public GetOperationalStatusLogResponseMsg getGetOperationalStatusLogResult() {
        return getOperationalStatusLogResult;
    }

    /**
     * Sets the value of the getOperationalStatusLogResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link GetOperationalStatusLogResponseMsg }
     *     
     */
    public void setGetOperationalStatusLogResult(GetOperationalStatusLogResponseMsg value) {
        this.getOperationalStatusLogResult = value;
    }

    public boolean isSetGetOperationalStatusLogResult() {
        return (this.getOperationalStatusLogResult!= null);
    }

}
