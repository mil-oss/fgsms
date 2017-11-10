
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
 *         &lt;element name="GetAllServiceDependenciesRequest" type="{urn:org:miloss:fgsms:services:interfaces:dataAccessService}GetAllServiceDependenciesRequestMsg"/>
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
    "getAllServiceDependenciesRequest"
})
@XmlRootElement(name = "GetAllServiceDependencies")
public class GetAllServiceDependencies
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "GetAllServiceDependenciesRequest", required = true)
    protected GetAllServiceDependenciesRequestMsg getAllServiceDependenciesRequest;

    /**
     * Gets the value of the getAllServiceDependenciesRequest property.
     * 
     * @return
     *     possible object is
     *     {@link GetAllServiceDependenciesRequestMsg }
     *     
     */
    public GetAllServiceDependenciesRequestMsg getGetAllServiceDependenciesRequest() {
        return getAllServiceDependenciesRequest;
    }

    /**
     * Sets the value of the getAllServiceDependenciesRequest property.
     * 
     * @param value
     *     allowed object is
     *     {@link GetAllServiceDependenciesRequestMsg }
     *     
     */
    public void setGetAllServiceDependenciesRequest(GetAllServiceDependenciesRequestMsg value) {
        this.getAllServiceDependenciesRequest = value;
    }

    public boolean isSetGetAllServiceDependenciesRequest() {
        return (this.getAllServiceDependenciesRequest!= null);
    }

}
