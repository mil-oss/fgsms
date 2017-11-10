
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
 *         &lt;element name="GetServiceDependenciesRequest" type="{urn:org:miloss:fgsms:services:interfaces:dataAccessService}GetServiceDependenciesRequestMsg"/>
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
    "getServiceDependenciesRequest"
})
@XmlRootElement(name = "GetServiceDependencies")
public class GetServiceDependencies
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "GetServiceDependenciesRequest", required = true)
    protected GetServiceDependenciesRequestMsg getServiceDependenciesRequest;

    /**
     * Gets the value of the getServiceDependenciesRequest property.
     * 
     * @return
     *     possible object is
     *     {@link GetServiceDependenciesRequestMsg }
     *     
     */
    public GetServiceDependenciesRequestMsg getGetServiceDependenciesRequest() {
        return getServiceDependenciesRequest;
    }

    /**
     * Sets the value of the getServiceDependenciesRequest property.
     * 
     * @param value
     *     allowed object is
     *     {@link GetServiceDependenciesRequestMsg }
     *     
     */
    public void setGetServiceDependenciesRequest(GetServiceDependenciesRequestMsg value) {
        this.getServiceDependenciesRequest = value;
    }

    public boolean isSetGetServiceDependenciesRequest() {
        return (this.getServiceDependenciesRequest!= null);
    }

}
