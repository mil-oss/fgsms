
package org.miloss.fgsms.services.interfaces.policyconfiguration;

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
 *         &lt;element name="GetfgsmsAdministratorsResult" type="{urn:org:miloss:fgsms:services:interfaces:policyConfiguration}GetfgsmsAdministratorsResponseMsg"/>
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
    "getAdministratorsResult"
})
@XmlRootElement(name = "GetAdministratorsResponse")
public class GetAdministratorsResponse
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "GetAdministratorsResult", required = true, nillable = true)
    protected GetAdministratorsResponseMsg getAdministratorsResult;

    /**
     * Gets the value of the getfgsmsAdministratorsResult property.
     * 
     * @return
     *     possible object is
     *     {@link GetfgsmsAdministratorsResponseMsg }
     *     
     */
    public GetAdministratorsResponseMsg getGetAdministratorsResult() {
        return getAdministratorsResult;
    }

    /**
     * Sets the value of the getfgsmsAdministratorsResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link GetfgsmsAdministratorsResponseMsg }
     *     
     */
    public void setGetAdministratorsResult(GetAdministratorsResponseMsg value) {
        this.getAdministratorsResult = value;
    }

    public boolean isSetGetAdministratorsResult() {
        return (this.getAdministratorsResult!= null);
    }

}
