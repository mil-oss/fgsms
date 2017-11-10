
package org.miloss.fgsms.services.interfaces.policyconfiguration;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 *         gets the plugin list
 *            
 * 
 * <p>Java class for GetPluginInformationRequestMsg complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetPluginInformationRequestMsg">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="GetPluginInformationRequestWrapper" type="{urn:org:miloss:fgsms:services:interfaces:policyConfiguration}GetPluginInformationRequestWrapper"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetPluginInformationRequestMsg", propOrder = {
    "getPluginInformationRequestWrapper"
})
public class GetPluginInformationRequestMsg
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "GetPluginInformationRequestWrapper", required = true)
    protected GetPluginInformationRequestWrapper getPluginInformationRequestWrapper;

    /**
     * Gets the value of the getPluginInformationRequestWrapper property.
     * 
     * @return
     *     possible object is
     *     {@link GetPluginInformationRequestWrapper }
     *     
     */
    public GetPluginInformationRequestWrapper getGetPluginInformationRequestWrapper() {
        return getPluginInformationRequestWrapper;
    }

    /**
     * Sets the value of the getPluginInformationRequestWrapper property.
     * 
     * @param value
     *     allowed object is
     *     {@link GetPluginInformationRequestWrapper }
     *     
     */
    public void setGetPluginInformationRequestWrapper(GetPluginInformationRequestWrapper value) {
        this.getPluginInformationRequestWrapper = value;
    }

    public boolean isSetGetPluginInformationRequestWrapper() {
        return (this.getPluginInformationRequestWrapper!= null);
    }

}
