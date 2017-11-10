
package org.miloss.fgsms.services.interfaces.dataaccessservice;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
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
 *         &lt;element name="GetAlertsResponse" type="{urn:org:miloss:fgsms:services:interfaces:dataAccessService}GetAlertsResponseMsg" maxOccurs="unbounded" minOccurs="0"/>
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
    "getAlertsResponse"
})
@XmlRootElement(name = "GetAlertsResponse")
public class GetAlertsResponse
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "GetAlertsResponse")
    protected List<GetAlertsResponseMsg> getAlertsResponse;

    /**
     * Gets the value of the getAlertsResponse property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the getAlertsResponse property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getGetAlertsResponse().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link GetAlertsResponseMsg }
     * 
     * 
     */
    public List<GetAlertsResponseMsg> getGetAlertsResponse() {
        if (getAlertsResponse == null) {
            getAlertsResponse = new ArrayList<GetAlertsResponseMsg>();
        }
        return this.getAlertsResponse;
    }

    public boolean isSetGetAlertsResponse() {
        return ((this.getAlertsResponse!= null)&&(!this.getAlertsResponse.isEmpty()));
    }

    public void unsetGetAlertsResponse() {
        this.getAlertsResponse = null;
    }

}
