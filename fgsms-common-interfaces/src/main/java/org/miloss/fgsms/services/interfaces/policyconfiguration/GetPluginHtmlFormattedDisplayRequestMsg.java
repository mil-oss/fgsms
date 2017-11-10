
package org.miloss.fgsms.services.interfaces.policyconfiguration;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GetPluginHtmlFormattedDisplayRequestMsg complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetPluginHtmlFormattedDisplayRequestMsg">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="GetPluginHtmlFormattedDisplayRequestMsgWrapper" type="{urn:org:miloss:fgsms:services:interfaces:policyConfiguration}GetPluginHtmlFormattedDisplayRequestMsgWrapper"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetPluginHtmlFormattedDisplayRequestMsg", propOrder = {
    "getPluginHtmlFormattedDisplayRequestMsgWrapper"
})
public class GetPluginHtmlFormattedDisplayRequestMsg
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "GetPluginHtmlFormattedDisplayRequestMsgWrapper", required = true, nillable = true)
    protected GetPluginHtmlFormattedDisplayRequestMsgWrapper getPluginHtmlFormattedDisplayRequestMsgWrapper;

    /**
     * Gets the value of the getPluginHtmlFormattedDisplayRequestMsgWrapper property.
     * 
     * @return
     *     possible object is
     *     {@link GetPluginHtmlFormattedDisplayRequestMsgWrapper }
     *     
     */
    public GetPluginHtmlFormattedDisplayRequestMsgWrapper getGetPluginHtmlFormattedDisplayRequestMsgWrapper() {
        return getPluginHtmlFormattedDisplayRequestMsgWrapper;
    }

    /**
     * Sets the value of the getPluginHtmlFormattedDisplayRequestMsgWrapper property.
     * 
     * @param value
     *     allowed object is
     *     {@link GetPluginHtmlFormattedDisplayRequestMsgWrapper }
     *     
     */
    public void setGetPluginHtmlFormattedDisplayRequestMsgWrapper(GetPluginHtmlFormattedDisplayRequestMsgWrapper value) {
        this.getPluginHtmlFormattedDisplayRequestMsgWrapper = value;
    }

    public boolean isSetGetPluginHtmlFormattedDisplayRequestMsgWrapper() {
        return (this.getPluginHtmlFormattedDisplayRequestMsgWrapper!= null);
    }

}
