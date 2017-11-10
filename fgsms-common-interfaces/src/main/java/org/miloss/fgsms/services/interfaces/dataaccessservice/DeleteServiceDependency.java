
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
 *         &lt;element name="DeleteServiceDependencyRequest" type="{urn:org:miloss:fgsms:services:interfaces:dataAccessService}DeleteServiceDependencyRequestMsg"/>
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
    "deleteServiceDependencyRequest"
})
@XmlRootElement(name = "DeleteServiceDependency")
public class DeleteServiceDependency
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "DeleteServiceDependencyRequest", required = true)
    protected DeleteServiceDependencyRequestMsg deleteServiceDependencyRequest;

    /**
     * Gets the value of the deleteServiceDependencyRequest property.
     * 
     * @return
     *     possible object is
     *     {@link DeleteServiceDependencyRequestMsg }
     *     
     */
    public DeleteServiceDependencyRequestMsg getDeleteServiceDependencyRequest() {
        return deleteServiceDependencyRequest;
    }

    /**
     * Sets the value of the deleteServiceDependencyRequest property.
     * 
     * @param value
     *     allowed object is
     *     {@link DeleteServiceDependencyRequestMsg }
     *     
     */
    public void setDeleteServiceDependencyRequest(DeleteServiceDependencyRequestMsg value) {
        this.deleteServiceDependencyRequest = value;
    }

    public boolean isSetDeleteServiceDependencyRequest() {
        return (this.deleteServiceDependencyRequest!= null);
    }

}
