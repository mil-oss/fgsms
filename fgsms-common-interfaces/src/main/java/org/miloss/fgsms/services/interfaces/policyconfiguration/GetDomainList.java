
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
 *         &lt;element name="request" type="{urn:org:miloss:fgsms:services:interfaces:policyConfiguration}GetDomainListRequestMsg"/>
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
    "request"
})
@XmlRootElement(name = "GetDomainList")
public class GetDomainList
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(required = true, nillable = true)
    protected GetDomainListRequestMsg request;

    /**
     * Gets the value of the request property.
     * 
     * @return
     *     possible object is
     *     {@link GetDomainListRequestMsg }
     *     
     */
    public GetDomainListRequestMsg getRequest() {
        return request;
    }

    /**
     * Sets the value of the request property.
     * 
     * @param value
     *     allowed object is
     *     {@link GetDomainListRequestMsg }
     *     
     */
    public void setRequest(GetDomainListRequestMsg value) {
        this.request = value;
    }

    public boolean isSetRequest() {
        return (this.request!= null);
    }

}
