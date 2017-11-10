
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
 *         &lt;element name="GetServiceDependenciesResponse" type="{urn:org:miloss:fgsms:services:interfaces:dataAccessService}GetServiceDependenciesResponseMsg"/>
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
    "getServiceDependenciesResponse"
})
@XmlRootElement(name = "GetServiceDependenciesResponse")
public class GetServiceDependenciesResponse
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "GetServiceDependenciesResponse", required = true)
    protected GetServiceDependenciesResponseMsg getServiceDependenciesResponse;

    /**
     * Gets the value of the getServiceDependenciesResponse property.
     * 
     * @return
     *     possible object is
     *     {@link GetServiceDependenciesResponseMsg }
     *     
     */
    public GetServiceDependenciesResponseMsg getGetServiceDependenciesResponse() {
        return getServiceDependenciesResponse;
    }

    /**
     * Sets the value of the getServiceDependenciesResponse property.
     * 
     * @param value
     *     allowed object is
     *     {@link GetServiceDependenciesResponseMsg }
     *     
     */
    public void setGetServiceDependenciesResponse(GetServiceDependenciesResponseMsg value) {
        this.getServiceDependenciesResponse = value;
    }

    public boolean isSetGetServiceDependenciesResponse() {
        return (this.getServiceDependenciesResponse!= null);
    }

}
