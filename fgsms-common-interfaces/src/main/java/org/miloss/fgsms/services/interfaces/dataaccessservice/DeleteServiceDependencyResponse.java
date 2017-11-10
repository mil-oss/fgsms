
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
 *         &lt;element name="DeleteServiceDependencyResponse" type="{urn:org:miloss:fgsms:services:interfaces:dataAccessService}DeleteServiceDependencyResponseMsg"/>
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
    "deleteServiceDependencyResponse"
})
@XmlRootElement(name = "DeleteServiceDependencyResponse")
public class DeleteServiceDependencyResponse
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "DeleteServiceDependencyResponse", required = true)
    protected DeleteServiceDependencyResponseMsg deleteServiceDependencyResponse;

    /**
     * Gets the value of the deleteServiceDependencyResponse property.
     * 
     * @return
     *     possible object is
     *     {@link DeleteServiceDependencyResponseMsg }
     *     
     */
    public DeleteServiceDependencyResponseMsg getDeleteServiceDependencyResponse() {
        return deleteServiceDependencyResponse;
    }

    /**
     * Sets the value of the deleteServiceDependencyResponse property.
     * 
     * @param value
     *     allowed object is
     *     {@link DeleteServiceDependencyResponseMsg }
     *     
     */
    public void setDeleteServiceDependencyResponse(DeleteServiceDependencyResponseMsg value) {
        this.deleteServiceDependencyResponse = value;
    }

    public boolean isSetDeleteServiceDependencyResponse() {
        return (this.deleteServiceDependencyResponse!= null);
    }

}
