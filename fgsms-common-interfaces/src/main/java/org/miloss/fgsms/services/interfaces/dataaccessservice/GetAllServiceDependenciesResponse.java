
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
 *         &lt;element name="GetAllServiceDependenciesResponse" type="{urn:org:miloss:fgsms:services:interfaces:dataAccessService}GetAllServiceDependenciesResponseMsg"/>
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
    "getAllServiceDependenciesResponse"
})
@XmlRootElement(name = "GetAllServiceDependenciesResponse")
public class GetAllServiceDependenciesResponse
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "GetAllServiceDependenciesResponse", required = true)
    protected GetAllServiceDependenciesResponseMsg getAllServiceDependenciesResponse;

    /**
     * Gets the value of the getAllServiceDependenciesResponse property.
     * 
     * @return
     *     possible object is
     *     {@link GetAllServiceDependenciesResponseMsg }
     *     
     */
    public GetAllServiceDependenciesResponseMsg getGetAllServiceDependenciesResponse() {
        return getAllServiceDependenciesResponse;
    }

    /**
     * Sets the value of the getAllServiceDependenciesResponse property.
     * 
     * @param value
     *     allowed object is
     *     {@link GetAllServiceDependenciesResponseMsg }
     *     
     */
    public void setGetAllServiceDependenciesResponse(GetAllServiceDependenciesResponseMsg value) {
        this.getAllServiceDependenciesResponse = value;
    }

    public boolean isSetGetAllServiceDependenciesResponse() {
        return (this.getAllServiceDependenciesResponse!= null);
    }

}
